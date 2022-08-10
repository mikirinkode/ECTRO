package id.ac.unila.ee.himatro.ectro.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.databinding.FragmentProfileBinding
import id.ac.unila.ee.himatro.ectro.ui.event.AddEventActivity
import id.ac.unila.ee.himatro.ectro.ui.settings.SettingsActivity
import id.ac.unila.ee.himatro.ectro.utils.DateHelper

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val preferences: EctroPreferences by lazy {
        EctroPreferences(requireContext())
    }
    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val firebaseUser: FirebaseUser? by lazy {
        auth.currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()

        binding.apply {
            val roleRequestStatus =
                preferences.getValues(EctroPreferences.ROLE_REQUEST_STATUS)

            if(roleRequestStatus == EctroPreferences.COMPLETED_STATUS){
                btnRequestRole.visibility = View.GONE
            }

            btnSettings.setOnClickListener {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }

            btnRequestRole.setOnClickListener {
                if (isUserDataComplete()) {
                    if (roleRequestStatus != EctroPreferences.WAITING_STATUS) {
                        createRoleRequest()
                    } else if (roleRequestStatus == EctroPreferences.WAITING_STATUS) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.request_processed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_complete_your_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun createRoleRequest() {
        if (firebaseUser != null) {
            val roleRequest = hashMapOf(
                "status" to EctroPreferences.WAITING_STATUS,
                "userId" to firebaseUser?.uid,
                "requestedAt" to DateHelper.getCurrentDate()
            )

            binding.loadingIndicator.visibility = View.VISIBLE
            db.collection("roleRequest")
                .document()
                .set(roleRequest)
                .addOnSuccessListener {

                    val updateData = hashMapOf(
                        "roleRequestStatus" to EctroPreferences.WAITING_STATUS
                    )

                    db.collection("users")
                        .document(firebaseUser!!.uid)
                        .set(updateData, SetOptions.merge())

                    preferences.setValues(EctroPreferences.ROLE_REQUEST_STATUS, EctroPreferences.WAITING_STATUS)

                    binding.loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.successfully_request_role),
                        Toast.LENGTH_SHORT
                    ).show()

                }
                .addOnFailureListener {
                    binding.loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, it.message.toString())
                }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.an_error_occurred),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isUserDataComplete(): Boolean {
        val userName = preferences.getValues(EctroPreferences.USER_NAME)
        val userNpm = preferences.getValues(EctroPreferences.USER_NPM)

        return !userNpm.isNullOrEmpty() && !userNpm.isNullOrEmpty()
    }

    private fun setupUi() {
        val userName = preferences.getValues(EctroPreferences.USER_NAME)
        val userPhotoUrl = preferences.getValues(EctroPreferences.USER_PHOTO_URL)
        val instagramAccount = preferences.getValues(EctroPreferences.USER_INSTAGRAM_ACCOUNT)
        val linkedinAccount = preferences.getValues(EctroPreferences.USER_LINKEDIN_ACCOUNT)

        val userDepartment = preferences.getValues(EctroPreferences.USER_DEPARTMENT)
        val userDivision = preferences.getValues(EctroPreferences.USER_DIVISION)
        val userPosition = preferences.getValues(EctroPreferences.USER_POSITION)

        binding.apply {
            tvUserName.text = userName
            tvUserInstagram.text = if (instagramAccount.isNullOrEmpty()) "-" else instagramAccount
            tvUserLinkedin.text = if (linkedinAccount.isNullOrEmpty()) "-" else linkedinAccount

            if (userDivision.isNullOrEmpty() && userPosition.isNullOrEmpty()) {
                tvPosition.visibility = View.GONE
                tvDummyPosition.visibility = View.GONE
            } else {
                tvPosition.text = "$userPosition $userDivision"
                tvDummyPosition.text = "$userPosition $userDivision"
                tvPosition.visibility = View.VISIBLE
                tvDummyPosition.visibility = View.VISIBLE
            }

            if (userDepartment.isNullOrEmpty()) {
                tvDepartment.visibility = View.GONE
                tvDummyDepartment.visibility = View.GONE
            } else {
                tvDepartment.text = userDepartment
                tvDummyDepartment.text = userDepartment
                tvDepartment.visibility = View.VISIBLE
                tvDummyDepartment.visibility = View.VISIBLE
            }

            if (userPhotoUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(R.drawable.ic_default_profile)
                    .into(ivUserPhoto)
            } else {
                Glide.with(requireContext())
                    .load(userPhotoUrl)
                    .into(ivUserPhoto)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}