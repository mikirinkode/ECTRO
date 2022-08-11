package id.ac.unila.ee.himatro.ectro.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
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
import id.ac.unila.ee.himatro.ectro.data.model.Event
import id.ac.unila.ee.himatro.ectro.databinding.FragmentHomeBinding
import id.ac.unila.ee.himatro.ectro.ui.event.AddEventActivity
import id.ac.unila.ee.himatro.ectro.ui.member.MemberListActivity
import id.ac.unila.ee.himatro.ectro.ui.profile.EditProfileActivity
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var fireStore: FirebaseFirestore

    @Inject
    lateinit var preferences: EctroPreferences

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
            rvEvent.layoutManager = object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            rvEvent.adapter = adapter

            Glide.with(requireContext())
                .load(R.drawable.ic_sample_announcement_picture)
                .into(ivAnnouncementPicture)

            ivUserPhoto.setOnClickListener {
                startActivity(Intent(requireContext(), EditProfileActivity::class.java))
            }

            btnUserRecap.setOnClickListener {
                startActivity(Intent(requireContext(), MemberListActivity::class.java))
            }

            btnAddEvent.setOnClickListener {
                if (isUserDataComplete()) {
                    startActivity(Intent(requireContext(), AddEventActivity::class.java))
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
        }

        updateUi()
        observeEventList()
    }

    private fun isUserDataComplete(): Boolean {
        val userNpm = preferences.getValues(USER_NPM)
        val userDepartment = preferences.getValues(USER_DEPARTMENT)
        val userDivision = preferences.getValues(USER_DIVISION)
        val userPosition = preferences.getValues(USER_POSITION)
        val activePeriod = preferences.getValues(ACTIVE_PERIOD)

        return !userNpm.isNullOrEmpty() && !userDepartment.isNullOrEmpty() && !userDivision.isNullOrEmpty() && !userPosition.isNullOrEmpty()
    }

    private fun updateUi() {
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

    private fun observeEventList() {
        fireStore.collection("events").get()
            .addOnSuccessListener { documentList ->
                val eventList: ArrayList<Event> = ArrayList()
                for (document in documentList) {
                    if (document != null) {
                        eventList.add(
                            document.toObject()
                        )
                    }
                }

                if (eventList.isEmpty()) {
                    binding.emptyMessage.visibility = View.VISIBLE
                } else {
                    binding.emptyMessage.visibility = View.GONE
                    adapter.setData(eventList)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}