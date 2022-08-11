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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.databinding.FragmentProfileBinding
import id.ac.unila.ee.himatro.ectro.ui.settings.SettingsActivity
import id.ac.unila.ee.himatro.ectro.utils.DateHelper
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_ROLE_REQUEST
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_RR_APPLICANT_EMAIL
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_RR_APPLICANT_NAME
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_RR_APPLICANT_NPM
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_RR_APPLICANT_UID
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_RR_REQUEST_AT
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_RR_REQUEST_ID
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_RR_STATUS
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_REQUEST_STATUS
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var fireStore: FirebaseFirestore

    private val firebaseUser: FirebaseUser? by lazy {
        auth.currentUser
    }

    @Inject
    lateinit var preferences: EctroPreferences


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
                    Log.e(TAG, roleRequestStatus.toString())

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
            val requestId = preferences.getValues(EctroPreferences.USER_NPM) + System.currentTimeMillis()
            val roleRequest = hashMapOf(
                TABLE_RR_REQUEST_ID to requestId,
                TABLE_RR_STATUS to EctroPreferences.WAITING_STATUS,
                TABLE_RR_APPLICANT_UID to firebaseUser?.uid,
                TABLE_RR_APPLICANT_NAME to preferences.getValues(EctroPreferences.USER_NAME),
                TABLE_RR_APPLICANT_NPM to preferences.getValues(EctroPreferences.USER_NPM),
                TABLE_RR_APPLICANT_EMAIL to preferences.getValues(EctroPreferences.USER_EMAIL),
                TABLE_RR_REQUEST_AT to DateHelper.getCurrentDate()
            )

            binding.loadingIndicator.visibility = View.VISIBLE
            fireStore.collection(TABLE_ROLE_REQUEST)
                .document(requestId)
                .set(roleRequest)
                .addOnSuccessListener {

                    val updateData = hashMapOf(
                        TABLE_USER_REQUEST_STATUS to EctroPreferences.WAITING_STATUS
                    )

                    fireStore.collection(TABLE_USER)
                        .document(firebaseUser!!.uid)
                        .set(updateData, SetOptions.merge())

                    preferences.setValues(EctroPreferences.ROLE_REQUEST_STATUS, EctroPreferences.WAITING_STATUS)

                    binding.loadingIndicator.visibility = View.GONE
                    binding.btnRequestRole.visibility = View.GONE
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

        return !userName.isNullOrEmpty() && !userNpm.isNullOrEmpty()
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