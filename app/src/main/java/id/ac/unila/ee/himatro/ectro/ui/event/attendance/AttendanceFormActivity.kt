package id.ac.unila.ee.himatro.ectro.ui.event.attendance

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.EventEntity
import id.ac.unila.ee.himatro.ectro.databinding.ActivityAttendanceFormBinding
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.utils.AlarmReceiver
import id.ac.unila.ee.himatro.ectro.viewmodel.AttendanceViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceFormActivity : AppCompatActivity() {

    private val binding: ActivityAttendanceFormBinding by lazy {
        ActivityAttendanceFormBinding.inflate(layoutInflater)
    }

    private val viewModel: AttendanceViewModel by viewModels()

    @Inject
    lateinit var preferences: EctroPreferences

    private val alarmReceiver: AlarmReceiver by lazy {
        AlarmReceiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeIsLoading()

        val event = intent.getParcelableExtra<EventEntity>(EXTRA_EVENT)

        updateUserInfo(event?.name ?: "")

        binding.apply {

            rgAttendanceStatus.setOnCheckedChangeListener { _, buttonId ->
                when (buttonId) {
                    R.id.rb_sick -> {
                        tilReasonCannotAttend.visibility = View.VISIBLE
                    }
                    R.id.rb_permission -> {
                        tilReasonCannotAttend.visibility = View.VISIBLE
                    }
                    else -> tilReasonCannotAttend.visibility = View.GONE
                }
            }

            btnConfirmation.setOnClickListener {
                var isValid = true
                val attendanceStatus = when (rgAttendanceStatus.checkedRadioButtonId) {
                    R.id.rb_present -> ATTENDANCE_PRESENT
                    R.id.rb_permission -> ATTENDANCE_PERMISSION
                    R.id.rb_sick -> ATTENDANCE_SICK
                    else -> ""
                }

                val reasonCannotAttend = edtReasonCannotAttend.text.toString().trim()

                if (attendanceStatus.isEmpty()) {
                    isValid = false
                    Toast.makeText(
                        this@AttendanceFormActivity,
                        getString(R.string.please_choose_attendance_status),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if ((attendanceStatus == ATTENDANCE_PERMISSION || attendanceStatus == ATTENDANCE_SICK) && reasonCannotAttend.isEmpty()) {
                    edtReasonCannotAttend.error =
                        getString(R.string.empty_reason)
                    isValid = false
                }

                if (isValid) {
                    if (event != null) {
                        if (attendanceStatus == ATTENDANCE_PRESENT){
                            val userName = preferences.getValues(EctroPreferences.USER_NAME)

                            alarmReceiver.setNotification(
                                this@AttendanceFormActivity,
                                event.date,
                                event.time,
                                "Reminder Acara ${event.name}",
                            "Hai $userName, acara akan segera mulai nih.")
                        }

                        viewModel.insertUserAttendance(
                            reasonCannotAttend,
                            attendanceStatus,
                            event.eventId,
                            attendanceStatus == ATTENDANCE_PRESENT
                        )
                        viewModel.isError.observe(this@AttendanceFormActivity) { isError ->
                            if (isError) {
                                viewModel.responseMessage.observe(this@AttendanceFormActivity) {
                                    if (it != null) {
                                        it.getContentIfNotHandled()?.let { msg ->
                                            Toast.makeText(
                                                this@AttendanceFormActivity,
                                                msg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@AttendanceFormActivity,
                                    getString(R.string.successfully_fill_attendance),
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@AttendanceFormActivity,
                                        MainActivity::class.java
                                    )
                                )
                                finishAffinity()
                            }
                        }
                    }
                }
            }

            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    private fun updateUserInfo(eventName: String) {
        val userFullName = preferences.getValues(EctroPreferences.USER_NAME)
        val userNpm = preferences.getValues(EctroPreferences.USER_NPM)

        binding.apply {
            tvUserName.text = userFullName
            tvUserNpm.text = userNpm
            tvEventName.text = eventName
        }
    }

    private fun observeIsLoading() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.loadingIndicator.visibility = View.GONE
            }
        }
    }


    companion object {
        const val ATTENDANCE_PRESENT = "Hadir"
        const val ATTENDANCE_PERMISSION = "Izin"
        const val ATTENDANCE_SICK = "Sakit"
        const val EXTRA_EVENT = "extra_event"
    }
}