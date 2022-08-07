package id.ac.unila.ee.himatro.ectro.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_NAME
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_PHOTO_URL
import id.ac.unila.ee.himatro.ectro.databinding.FragmentHomeBinding
import id.ac.unila.ee.himatro.ectro.ui.event.CreateEventActivity
import id.ac.unila.ee.himatro.ectro.ui.profile.EditProfileActivity


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val preferences: EctroPreferences by lazy {
        EctroPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUserInfo()

        binding.apply {
            Glide.with(requireContext())
                .load(R.drawable.ic_sample_announcement_picture)
                .into(ivAnnouncementPicture)

            ivUserPhoto.setOnClickListener {
                startActivity(Intent(requireContext(), EditProfileActivity::class.java))
            }

            btnUserRecap.setOnClickListener {
                Toast.makeText(requireContext(), "Under Development", Toast.LENGTH_SHORT).show()
            }

            btnCreateEvent.setOnClickListener {
                startActivity(Intent(requireContext(), CreateEventActivity::class.java))
            }

            btnMore.setOnClickListener {
                Toast.makeText(requireContext(), "Under Development", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserInfo() {
        val userName = preferences.getValues(USER_NAME)
        val userPhotoUrl = preferences.getValues(USER_PHOTO_URL)

        binding.apply {
            tvUserName.text = userName

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