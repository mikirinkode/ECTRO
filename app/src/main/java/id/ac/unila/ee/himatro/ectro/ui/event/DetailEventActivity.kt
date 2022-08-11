package id.ac.unila.ee.himatro.ectro.ui.event

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.model.EventEntity
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.databinding.ActivityDetailEventBinding
import id.ac.unila.ee.himatro.ectro.ui.event.attendance.AttendanceFormActivity
import id.ac.unila.ee.himatro.ectro.ui.event.notes.AddNoteActivity
import id.ac.unila.ee.himatro.ectro.ui.event.participant.ParticipantListActivity
import javax.inject.Inject

@AndroidEntryPoint
class DetailEventActivity : AppCompatActivity() {
    private val binding: ActivityDetailEventBinding by lazy {
        ActivityDetailEventBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var fireStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val entity = intent.getParcelableExtra<EventEntity>(EXTRA_ENTITY)
        setData(entity)

        binding.apply {

            btnBack.setOnClickListener { onBackPressed() }

            btnParticipant.setOnClickListener {
                startActivity(Intent(this@DetailEventActivity, ParticipantListActivity::class.java))
            }

            btnNotes.setOnClickListener {
                startActivity(Intent(this@DetailEventActivity, AddNoteActivity::class.java))
            }

            layoutCreatedBy.setOnClickListener {

            }
        }
    }

    private fun setData(eventEntity: EventEntity?) {
        binding.apply {
            if (eventEntity != null) {
                tvEventName.text = eventEntity.name
                tvEventDesc.text = eventEntity.desc
                tvEventCategory.text = eventEntity.category

                tvEventDate.text = eventEntity.date
                tvEventTime.text = eventEntity.time
                tvEventPlace.text = eventEntity.place

                // show attendance layout
                if (eventEntity.isNeedAttendanceForm == true) {
                    layoutEventAttendance.visibility = View.VISIBLE

                    btnAttendance.setOnClickListener {
                        startActivity(Intent(this@DetailEventActivity, AttendanceFormActivity::class.java))
                    }
                } else {
                    layoutEventAttendance.visibility = View.GONE
                }

                // show extra action button
                if (eventEntity.extraActionName.isNotEmpty()) {
                    layoutAdditionalButton.visibility = View.VISIBLE

                    // if action only active after user fill attendance && user has not fill it
                    // then button will be disabled
                    if (eventEntity.actionAfterAttendance == true && !hasFilledAttendance()) {
                        btnAdditional.isEnabled = false
                        btnAdditional.text = getString(R.string.please_fill_attendance_first)
                    } else {
                        btnAdditional.isEnabled = true
                        btnAdditional.text = eventEntity.extraActionName
                    }


                    btnAdditional.setOnClickListener {
                        Toast.makeText(
                            this@DetailEventActivity,
                            eventEntity.extraActionLink,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    layoutAdditionalButton.visibility = View.GONE
                }

                observeUploaderInfo(eventEntity.creatorUid)
            }
        }
    }

    private fun observeUploaderInfo(userId: String) {

        val userInDB: DocumentReference = fireStore.collection("users").document(userId)

        userInDB.get()
            .addOnSuccessListener { document ->
                val user: User? = document.toObject<User>()

                if (user != null) {
                    binding.apply {
                        tvUserName.text = user.name

                        if (user.photoUrl.isNotEmpty()) {
                            Glide.with(this@DetailEventActivity)
                                .load(user.photoUrl)
                                .into(ivUserPhoto)
                        } else {
                            Glide.with(this@DetailEventActivity)
                                .load(R.drawable.ic_default_profile)
                                .into(ivUserPhoto)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
                Toast.makeText(
                    this,
                    getString(R.string.database_connection_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun hasFilledAttendance(): Boolean{
        return false
    }

    companion object {
        const val EXTRA_ENTITY = "extra_entity"
        private const val TAG = "DetailEventActivity"
    }
}