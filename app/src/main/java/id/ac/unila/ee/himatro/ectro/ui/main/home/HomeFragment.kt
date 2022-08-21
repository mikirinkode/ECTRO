package id.ac.unila.ee.himatro.ectro.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.ACTIVE_PERIOD
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_DEPARTMENT
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_DIVISION
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_NAME
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_NPM
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_PHOTO_URL
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_POSITION
import id.ac.unila.ee.himatro.ectro.databinding.FragmentHomeBinding
import id.ac.unila.ee.himatro.ectro.ui.UserFeedbackActivity
import id.ac.unila.ee.himatro.ectro.ui.event.AddEventActivity
import id.ac.unila.ee.himatro.ectro.ui.event.EventListActivity
import id.ac.unila.ee.himatro.ectro.ui.member.MemberListActivity
import id.ac.unila.ee.himatro.ectro.ui.profile.EditProfileActivity
import id.ac.unila.ee.himatro.ectro.viewmodel.EventViewModel
import id.ac.unila.ee.himatro.ectro.viewmodel.UserViewModel
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferences: EctroPreferences

    private val userViewModel: UserViewModel by viewModels()

    private val eventViewModel: EventViewModel by viewModels()

    private val adapter: EventAdapter by lazy {
        EventAdapter()
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

        binding.apply {
            // set recyclerview layout
            rvEvent.layoutManager = LinearLayoutManager(requireContext())
            rvEvent.adapter = adapter

            // set announcement
            Glide.with(requireContext())
                .load(R.drawable.ic_sample_announcement_picture)
                .into(ivAnnouncementPicture)

            ivUserPhoto.setOnClickListener {
                startActivity(Intent(activity, EditProfileActivity::class.java))
            }

            btnUserRecap.setOnClickListener {
                if (isUserDataComplete()) {
                    startActivity(Intent(requireContext(), MemberListActivity::class.java))
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_complete_your_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnAddEvent.setOnClickListener {
                if (isUserDataComplete()) {
                    startActivity(Intent(activity, AddEventActivity::class.java))
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_complete_your_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnMore.setOnClickListener {
                Toast.makeText(requireContext(), "Under Development", Toast.LENGTH_SHORT).show()
            }

            btnAnnouncement.setOnClickListener {
                startActivity(Intent(requireContext(), UserFeedbackActivity::class.java))
            }

            btnSeeAllEvent.setOnClickListener {
                startActivity(Intent(requireContext(), EventListActivity::class.java))
            }
        }

        observeUser()
        observeEvent()
    }

    private fun observeEvent() {
        eventViewModel.observeEventList(HOME_EVENT_LIMIT).observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.emptyMessage.visibility = View.VISIBLE
            } else {
                binding.emptyMessage.visibility = View.GONE
                adapter.setData(list)
                Log.e(TAG, list.size.toString())
                Log.e(TAG, adapter.itemCount.toString())
            }
        }


        eventViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                eventViewModel.responseMessage.observe(viewLifecycleOwner) {
                    if (it != null) {
                        it.getContentIfNotHandled()?.let { msg ->
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun isUserDataComplete(): Boolean {
        val userNpm = preferences.getValues(USER_NPM)
        val userDepartment = preferences.getValues(USER_DEPARTMENT)
        val userDivision = preferences.getValues(USER_DIVISION)
        val userPosition = preferences.getValues(USER_POSITION)
        val activePeriod = preferences.getValues(ACTIVE_PERIOD)

        return !userNpm.isNullOrEmpty() && !userDepartment.isNullOrEmpty()  && !userPosition.isNullOrEmpty()
    }

    private fun observeUser() {

        val userName = preferences.getValues(USER_NAME)
        val userPhotoUrl = preferences.getValues(USER_PHOTO_URL)

        if (userName == "") {
            userViewModel.observeLoggedUserData()
            userViewModel.isError.observe(viewLifecycleOwner) { isError ->
                if (isError) {
                    userViewModel.responseMessage.observe(viewLifecycleOwner) {
                        if (it != null) {
                            it.getContentIfNotHandled()?.let { msg ->
                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    userViewModel.loggedUserData.observe(viewLifecycleOwner) { user ->
                        updateUi(user.name, user.photoUrl)
                    }
                }
            }
        } else {
            updateUi(userName, userPhotoUrl)
        }
    }

    private fun updateUi(name: String?, photoUrl: String?) {
        binding.apply {
            tvUserName.text = name
            if (photoUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(R.drawable.ic_default_profile)
                    .into(ivUserPhoto)
            } else {
                Glide.with(requireContext())
                    .load(photoUrl)
                    .into(ivUserPhoto)
            }
        }
    }

    // TODO: CREATE SHIMMER LOADING FOR USER INFO AND EVENT LIST
    private fun observeIsLoading() {
        userViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading){
            } else {
            }
        }
        eventViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading){
            } else {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
        private const val HOME_EVENT_LIMIT = 2L
    }
}