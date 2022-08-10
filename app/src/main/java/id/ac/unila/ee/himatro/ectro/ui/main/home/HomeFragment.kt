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
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_NAME
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_PHOTO_URL
import id.ac.unila.ee.himatro.ectro.data.model.Event
import id.ac.unila.ee.himatro.ectro.databinding.FragmentHomeBinding
import id.ac.unila.ee.himatro.ectro.ui.event.AddEventActivity
import id.ac.unila.ee.himatro.ectro.ui.profile.EditProfileActivity


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val preferences: EctroPreferences by lazy {
        EctroPreferences(requireContext())
    }
    private val fireStore: FirebaseFirestore by lazy {
        Firebase.firestore
    }

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
                Toast.makeText(requireContext(), "Under Development", Toast.LENGTH_SHORT).show()
            }

            btnAddEvent.setOnClickListener {
                startActivity(Intent(requireContext(), AddEventActivity::class.java))
            }

            btnMore.setOnClickListener {
                Toast.makeText(requireContext(), "Under Development", Toast.LENGTH_SHORT).show()
            }
        }

        updateUserInfo()
        observeEventList()
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