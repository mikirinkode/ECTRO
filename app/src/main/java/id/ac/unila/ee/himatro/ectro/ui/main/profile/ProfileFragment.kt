package id.ac.unila.ee.himatro.ectro.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.ROLE_REQUEST_STATUS
import id.ac.unila.ee.himatro.ectro.databinding.FragmentProfileBinding
import id.ac.unila.ee.himatro.ectro.ui.settings.SettingsActivity
import id.ac.unila.ee.himatro.ectro.viewmodel.RoleRequestViewModel
import id.ac.unila.ee.himatro.ectro.viewmodel.UserViewModel
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

    private val viewModel: RoleRequestViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

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

        loadUserLocalPrefs()

        binding.apply {
            if (preferences.getValues(ROLE_REQUEST_STATUS) != EctroPreferences.COMPLETED_STATUS){
                userViewModel.observeLoggedUserData()
                userViewModel.loggedUserData.observe(viewLifecycleOwner) { user ->
                    setupUi(
                        user.name,
                        user.photoUrl,
                        user.instagram,
                        user.linkedin,
                        user.role.department,
                        user.role.division,
                        user.role.position
                    )

                    if (user.roleRequestStatus == EctroPreferences.COMPLETED_STATUS) {
                        btnRequestRole.visibility = View.GONE
                    } else {
                        btnRequestRole.visibility = View.VISIBLE

                        btnRequestRole.setOnClickListener {
                            if (isUserDataComplete()) {
                                if (user.roleRequestStatus != EctroPreferences.WAITING_STATUS && user.roleRequestStatus != EctroPreferences.COMPLETED_STATUS) {
                                    createRoleRequest()
                                } else if (user.roleRequestStatus == EctroPreferences.WAITING_STATUS) {
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
            }

            btnSettings.setOnClickListener {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }

        }
    }

    private fun createRoleRequest() {
        viewModel.createRoleRequest()
        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                viewModel.responseMessage.observe(viewLifecycleOwner) {
                    if (it != null) {
                        it.getContentIfNotHandled()?.let { msg ->
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                binding.btnRequestRole.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    getString(R.string.successfully_request_role),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isUserDataComplete(): Boolean {
        val userName = preferences.getValues(EctroPreferences.USER_NAME)
        val userNpm = preferences.getValues(EctroPreferences.USER_NPM)

        return !userName.isNullOrEmpty() && !userNpm.isNullOrEmpty()
    }

    private fun loadUserLocalPrefs() {
        val userName = preferences.getValues(EctroPreferences.USER_NAME)
        val userPhotoUrl = preferences.getValues(EctroPreferences.USER_PHOTO_URL)
        val instagramAccount = preferences.getValues(EctroPreferences.USER_INSTAGRAM_ACCOUNT)
        val linkedinAccount = preferences.getValues(EctroPreferences.USER_LINKEDIN_ACCOUNT)

        val userDepartment = preferences.getValues(EctroPreferences.USER_DEPARTMENT)
        val userDivision = preferences.getValues(EctroPreferences.USER_DIVISION)
        val userPosition = preferences.getValues(EctroPreferences.USER_POSITION)

        setupUi(
            userName,
            userPhotoUrl,
            instagramAccount,
            linkedinAccount,
            userDepartment,
            userDivision,
            userPosition
        )
    }

    private fun setupUi(
        userName: String?,
        userPhotoUrl: String?,
        instagramAccount: String?,
        linkedinAccount: String?,
        userDepartment: String?,
        userDivision: String?,
        userPosition: String?
    ) {


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