package id.ac.unila.ee.himatro.ectro.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.databinding.FragmentProfileBinding
import id.ac.unila.ee.himatro.ectro.ui.settings.SettingsActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val preferences: EctroPreferences by lazy {
        EctroPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUserInfo()

        binding.apply {
            btnSettings.setOnClickListener {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }
        }
    }

    private fun updateUserInfo() {
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
                tvPosition.text = ""
                tvPosition.visibility = View.GONE
            } else {
                tvPosition.text = "$userPosition $userDivision"
                tvPosition.visibility = View.VISIBLE
            }

            if (userDepartment.isNullOrEmpty()){
                tvDepartment.text = ""
                tvDepartment.visibility = View.GONE
            } else {
                tvDepartment.text = userDepartment
                tvDepartment.visibility = View.VISIBLE
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
}