package id.ac.unila.ee.himatro.ectro.ui.event

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.EventEntity
import id.ac.unila.ee.himatro.ectro.databinding.ActivityDetailEventBinding
import id.ac.unila.ee.himatro.ectro.ui.event.attendance.AttendanceFormActivity
import id.ac.unila.ee.himatro.ectro.ui.event.notes.AddNoteActivity
import id.ac.unila.ee.himatro.ectro.ui.event.participant.ParticipantListActivity
import id.ac.unila.ee.himatro.ectro.ui.profile.DetailUserActivity
import id.ac.unila.ee.himatro.ectro.viewmodel.AttendanceViewModel
import id.ac.unila.ee.himatro.ectro.viewmodel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class DetailEventActivity : AppCompatActivity() {
    private val binding: ActivityDetailEventBinding by lazy {
        ActivityDetailEventBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var preferences: EctroPreferences

    @Inject
    lateinit var auth: FirebaseAuth

    private val userViewModel: UserViewModel by viewModels()
    private val attendanceViewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val entity = intent.getParcelableExtra<EventEntity>(EXTRA_ENTITY)
        setData(entity)



        binding.apply {

            btnBack.setOnClickListener { onBackPressed() }

        }
    }

    private fun setData(eventEntity: EventEntity?) {
        binding.apply {
            if (eventEntity != null) {
                val loggedUser = auth.currentUser

                if (loggedUser?.uid == eventEntity.creatorId) {
                    btnEdit.visibility = View.VISIBLE
                    btnEdit.setOnClickListener {
                        startActivity(Intent(this@DetailEventActivity, AddEventActivity::class.java)
                            .putExtra(AddEventActivity.EXTRA_EVENT, eventEntity)
                            .putExtra(AddEventActivity.STATE, AddEventActivity.UPDATE_ID)
                        )
                    }
                } else {
                    btnEdit.visibility = View.GONE
                }

                tvEventName.text = eventEntity.name
                tvEventDesc.text = eventEntity.desc
                tvEventCategory.text = eventEntity.category

                tvEventDate.text = eventEntity.date
                tvEventTime.text = eventEntity.time
                tvEventPlace.text = eventEntity.place

                // show attendance layout
                if (eventEntity.isNeedNotes == true) {
                    btnNotes.visibility = View.VISIBLE
                    btnNotes.setOnClickListener {
                        startActivity(
                            Intent(
                                this@DetailEventActivity,
                                AddNoteActivity::class.java
                            ).putExtra(AddNoteActivity.EXTRA_EVENT_ID, eventEntity.eventId)
                        )
                    }
                } else {
                    btnNotes.visibility = View.GONE
                }

                if (eventEntity.isNeedAttendanceForm == true) {
                    observeHasFilledAttendance(
                        eventEntity.eventId,
                        eventEntity.actionAfterAttendance,
                        eventEntity.extraActionName
                    )
                    observeTotalParticipant(eventEntity.eventId)

                    layoutEventAttendance.visibility = View.VISIBLE
                    btnAttendance.visibility = View.VISIBLE
                    btnParticipant.visibility = View.VISIBLE

                    btnParticipant.setOnClickListener {
                        startActivity(
                            Intent(
                                this@DetailEventActivity,
                                ParticipantListActivity::class.java
                            ).putExtra(
                                ParticipantListActivity.EXTRA_EVENT_ID,
                                eventEntity.eventId
                            )
                        )
                    }

                    btnAttendance.setOnClickListener {
                        if (isUserDataComplete()) {
                            startActivity(
                                Intent(
                                    this@DetailEventActivity,
                                    AttendanceFormActivity::class.java
                                ).putExtra(AttendanceFormActivity.EXTRA_EVENT, eventEntity)
                            )
                        } else {
                            Toast.makeText(
                                this@DetailEventActivity,
                                getString(R.string.please_complete_your_data),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    layoutEventAttendance.visibility = View.GONE
                    btnAttendance.visibility = View.GONE
                }

                // show extra action button
                if (eventEntity.extraActionName.isNotEmpty()) {
                    layoutAdditionalButton.visibility = View.VISIBLE

                    if (eventEntity.actionAfterAttendance == false) {
                        btnAdditional.isEnabled = true
                        btnAdditional.text = eventEntity.extraActionName
                    }


                    btnAdditional.setOnClickListener {
                        try {
                            val uri: Uri = Uri.parse(eventEntity.extraActionLink)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@DetailEventActivity,
                                "Invalid Link",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    layoutAdditionalButton.visibility = View.GONE
                }

                observeUploaderInfo(eventEntity.creatorId)
                observeIsLoading()
            }
        }
    }

    private fun observeUploaderInfo(userId: String) {
        userViewModel.getUserDataByUid(userId).observe(this) { user ->
            binding.apply {
                layoutCreatedBy.setOnClickListener {
                    startActivity(
                        Intent(
                            this@DetailEventActivity,
                            DetailUserActivity::class.java
                        ).putExtra(DetailUserActivity.EXTRA_USER_ID, userId)
                    )
                }

                tvUserName.text = user.name

                if (user.photoUrl.isNotEmpty()) {
                    Glide.with(applicationContext)
                        .load(user.photoUrl)
                        .placeholder(R.drawable.ic_image_loading)
                        .error(R.drawable.ic_image_default)
                        .into(ivUserPhoto)
                } else {
                    Glide.with(applicationContext)
                        .load(R.drawable.ic_default_profile)
                        .into(ivUserPhoto)
                }
            }
        }
    }

    private fun observeHasFilledAttendance(
        eventId: String?,
        actionAfterAttendance: Boolean?,
        extraActionName: String?
    ) {
        if (eventId != null) {
            attendanceViewModel.checkAttendanceFilled(eventId)
            attendanceViewModel.hasFilledAttendance.observe(this) { hasFilledAttendance ->
                if (hasFilledAttendance) {
                    binding.btnAttendance.isEnabled = false
                    binding.btnAttendance.text = getString(R.string.has_filled_attendance)
                    binding.btnAttendance.isAllCaps = false
                } else {
                    binding.btnAttendance.isEnabled = true
                    binding.btnAttendance.text = getString(R.string.fill_attendance)
                }

                // if action only active after user fill attendance && user has not fill it
                // then button will be disabled
                if (actionAfterAttendance == true && !hasFilledAttendance) {
                    binding.btnAdditional.isEnabled = false
                    binding.btnAdditional.text = getString(R.string.please_fill_attendance_first)
                } else {
                    binding.btnAdditional.isEnabled = true
                    binding.btnAdditional.text = extraActionName
                }
            }
        }
    }

    private fun observeTotalParticipant(eventId: String) {
        attendanceViewModel.getTotalAttendees(eventId).observe(this) { totalAttendees ->
            binding.tvTotalParticipant.text = totalAttendees.toString()
        }
    }

    private fun isUserDataComplete(): Boolean {
        val userNpm = preferences.getValues(EctroPreferences.USER_NPM)
        val userDepartment = preferences.getValues(EctroPreferences.USER_DEPARTMENT)
        val userDivision = preferences.getValues(EctroPreferences.USER_DIVISION)
        val userPosition = preferences.getValues(EctroPreferences.USER_POSITION)
        val activePeriod = preferences.getValues(EctroPreferences.ACTIVE_PERIOD)

        return !userNpm.isNullOrEmpty() && !userDepartment.isNullOrEmpty() && !userPosition.isNullOrEmpty()
    }


    private fun observeIsLoading() {
        userViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingUser.visibility = View.VISIBLE
            } else {
                binding.loadingUser.visibility = View.GONE
            }
        }

        attendanceViewModel.isLoading.observe(this){ isLoading ->
            if (isLoading){
                binding.loadingCheckAttendance.visibility = View.VISIBLE
            } else {
                binding.loadingCheckAttendance.visibility = View.GONE
            }
        }
    }

    companion object {
        const val EXTRA_ENTITY = "extra_entity"
        private const val TAG = "DetailEventActivity"
    }
}