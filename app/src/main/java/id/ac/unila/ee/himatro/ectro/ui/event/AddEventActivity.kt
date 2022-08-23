package id.ac.unila.ee.himatro.ectro.ui.event

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.model.EventEntity
import id.ac.unila.ee.himatro.ectro.databinding.ActivityAddEventBinding
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.utils.AlarmReceiver
import id.ac.unila.ee.himatro.ectro.utils.DateHelper
import id.ac.unila.ee.himatro.ectro.utils.DatePickerFragment
import id.ac.unila.ee.himatro.ectro.utils.TimePickerFragment
import id.ac.unila.ee.himatro.ectro.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddEventActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener,
    TimePickerFragment.DialogTimeListener {

    private val binding: ActivityAddEventBinding by lazy {
        ActivityAddEventBinding.inflate(layoutInflater)
    }

    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // set Dropdown for online event type
        val arrayString: Array<String> = resources.getStringArray(R.array.online_event_media)
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayString)
        binding.actOnlineEventMedia.setAdapter(arrayAdapter)

        observeLoading()

        binding.apply {
            /**
             *  Setup Date Picker
             */
            // Build constraints.
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setStart(MaterialDatePicker.todayInUtcMilliseconds())
                    .setValidator(DateValidatorPointForward.now()) // Makes only dates from today forward selectable.


            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.choose_event_date))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build()

            /**
             *  Setup Time Picker
             */
            val calendar = Calendar.getInstance()
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText(getString(R.string.choose_event_start_time))
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build()

            tilEventDate.setOnClickListener {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, "DatePicker")
            }

            datePicker.addOnPositiveButtonClickListener {
                edtEventDate.text = datePicker.headerText
            }

            tilEventTime.setOnClickListener {
                val timePickerFragmentOne = TimePickerFragment()
                timePickerFragmentOne.show(supportFragmentManager, "TimePicker")
            }

            timePicker.addOnPositiveButtonClickListener {
                edtEventTime.text = "${timePicker.hour}:${timePicker.minute}"
            }

            switchAttendance.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    switchActionAfterAttendance.visibility = View.VISIBLE
                    tvActionAfterAttendance.visibility = View.VISIBLE
                } else {
                    switchActionAfterAttendance.visibility = View.GONE
                    tvActionAfterAttendance.visibility = View.GONE
                    switchActionAfterAttendance.isChecked = false
                }
            }

            rgEventType.setOnCheckedChangeListener { _, buttonId ->
                if (buttonId == R.id.rb_offline) {
                    tilOnlineEventMedia.visibility = View.GONE
                } else if (buttonId == R.id.rb_online) {
                    tilOnlineEventMedia.visibility = View.VISIBLE
                }
            }

            btnAddEvent.setOnClickListener {
                var isValid = true
                val eventName = edtEventName.text.toString().trim()
                val eventDesc = edtEventDesc.text.toString().trim()
                val eventCategory = edtEventCategory.text.toString().trim()

                var eventType: String? = null
                val onlineEventMedia = actOnlineEventMedia.text.toString().trim()

                if (rgEventType.checkedRadioButtonId == R.id.rb_offline) {
                    eventType = getString(R.string.offline)
                } else if (rgEventType.checkedRadioButtonId == R.id.rb_online) {
                    eventType = getString(R.string.online)
                }

                val eventPlace = edtEventPlace.text.toString().trim()
                val eventDate = edtEventDate.text.toString().trim()
                val eventTime = edtEventTime.text.toString().trim()

                val isNeedNotes = switchNotes.isChecked
                val isNeedAttendance = switchAttendance.isChecked
                val additionalName = edtAdditionalName.text.toString().trim()
                val additionalLink = edtAdditionalLink.text.toString().trim()
                val actionAfterAttendance = switchActionAfterAttendance.isChecked

                if (eventName.isEmpty()) {
                    edtEventName.error = getString(R.string.empty_event_name)
                    isValid = false
                }

                val onlineMeeting = resources.getStringArray(R.array.online_event_media)[0]
                if (eventType.isNullOrEmpty()) {
                    Toast.makeText(
                        this@AddEventActivity,
                        getString(R.string.empty_event_type),
                        Toast.LENGTH_SHORT
                    ).show()
                    isValid = false
                } else if (!eventType.isNullOrEmpty() && eventType == getString(R.string.online) && additionalLink.isEmpty() && onlineEventMedia == onlineMeeting) {
                    edtAdditionalLink.error = getString(R.string.empty_meeting_link)
                    isValid = false
                }

                if (!eventType.isNullOrEmpty() && eventType == getString(R.string.online) && onlineEventMedia.isEmpty()) {
                    actOnlineEventMedia.error = getString(R.string.please_choose_online_media)
                    isValid = false
                }

                if (additionalName.isNotEmpty() && additionalLink.isEmpty()) {
                    edtAdditionalLink.error = getString(R.string.empty_additional_link)
                    isValid = false
                }

                if (additionalName.length > 30){
                    edtAdditionalName.error = getString(R.string.additional_action_name_is_too_long)
                    isValid = false
                }

                if (additionalLink.isNotEmpty() && additionalName.isEmpty()) {
                    edtAdditionalName.error = getString(R.string.empty_additional_link)
                    isValid = false
                }

                if (eventPlace.isEmpty()) {
                    edtEventPlace.error = getString(R.string.empty_event_place)
                    isValid = false
                }
                if (eventDate.isEmpty()) {
                    Toast.makeText(
                        this@AddEventActivity,
                        getString(R.string.empty_event_date),
                        Toast.LENGTH_SHORT
                    ).show()
                    isValid = false
                }

                if (eventTime.isEmpty()) {
                    Toast.makeText(
                        this@AddEventActivity,
                        getString(R.string.empty_event_time),
                        Toast.LENGTH_SHORT
                    ).show()
                    isValid = false
                }

                if (eventCategory.isEmpty()) {
                    edtEventCategory.error = getString(R.string.empty_event_category)
                    isValid = false
                }

                if (isValid) {
                    viewModel.createEvent(
                        eventName = eventName,
                        eventDesc = eventDesc,
                        eventType = eventType ?: "",
                        eventDate = eventDate,
                        eventTime = eventTime,
                        eventPlace = eventPlace,
                        eventCategory = eventCategory,
                        isNeedNotes = isNeedNotes,
                        isNeedAttendance = isNeedAttendance,
                        additionalName = additionalName,
                        additionalLink = additionalLink,
                        actionAfterAttendance = actionAfterAttendance
                    )
                    viewModel.isError.observe(this@AddEventActivity) { isError ->
                        if (isError) {
                            viewModel.responseMessage.observe(this@AddEventActivity) {
                                if (it != null) {
                                    it.getContentIfNotHandled()?.let { msg ->
                                        Toast.makeText(
                                            this@AddEventActivity,
                                            msg,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@AddEventActivity,
                                getString(R.string.successfully_add_event),
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    this@AddEventActivity,
                                    MainActivity::class.java
                                )
                            )
                            finishAffinity()
                        }
                    }
                }
            }

            btnBack.setOnClickListener { onBackPressed() }

            // restore saved data
            restoreSavedDate(savedInstanceState)
        }
    }

    private fun observeLoading() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.loadingIndicator.visibility = View.GONE
            }
        }
    }

    private fun restoreSavedDate(savedInstanceState: Bundle?) {
        binding.apply {
            if (savedInstanceState != null) {
                val savedState = savedInstanceState.getParcelable<EventEntity>(STATE_RESULT)
                if (savedState != null) {
                    edtEventName.setText(savedState.name)
                    edtEventDesc.setText(savedState.name)

                    if (rgEventType.checkedRadioButtonId == R.id.rb_offline) {
                        rbOffline.isChecked = true
                    } else if (rgEventType.checkedRadioButtonId == R.id.rb_online) {
                        rbOnline.isChecked = true
                    }
                    edtEventPlace.setText(savedState.name)
                    edtEventDate.text = savedState.name
                    edtEventTime.text = savedState.name
                    edtEventCategory.setText(savedState.name)
                    edtAdditionalName.setText(savedState.extraActionName)
                    edtAdditionalLink.setText(savedState.extraActionLink)
                    if (savedState.isNeedAttendanceForm != null) {
                        switchAttendance.isChecked = savedState.isNeedAttendanceForm!!
                    }
                    if (savedState.actionAfterAttendance != null) {
                        switchActionAfterAttendance.isChecked = savedState.actionAfterAttendance!!
                    }
                    if (savedState.isNeedNotes != null) {
                        switchNotes.isChecked = savedState.isNeedNotes!!
                    }
                }
            }
        }
    }

    // prevent data loss when device rotated
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.apply {
            val eventType =
                if (rgEventType.checkedRadioButtonId == R.id.rb_offline) "Offline" else "Online"
            outState.putParcelable(
                STATE_RESULT,
                EventEntity(
                    name = edtEventName.text.toString().trim(),
                    desc = edtEventDesc.text.toString().trim(),
                    category = edtEventCategory.text.toString().trim(),
                    type = eventType,
                    place = edtEventPlace.text.toString().trim(),
                    date = edtEventDate.text.toString().trim(),
                    time = edtEventTime.text.toString().trim(),
                    isNeedNotes = switchNotes.isChecked,
                    isNeedAttendanceForm = switchAttendance.isChecked,
                    extraActionName = edtAdditionalName.text.toString().trim(),
                    extraActionLink = edtAdditionalLink.text.toString().trim(),
                    actionAfterAttendance = switchActionAfterAttendance.isChecked,
                )
            )
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Set text dari textview once
        binding.edtEventDate.text = dateFormat.format(calendar.time)
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        // Siapkan time formatter-nya terlebih dahulu
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())


        binding.edtEventTime.text = dateFormat.format(calendar.time)
    }


    companion object {
        private const val TAG = "CreateEventActivity"
        private const val STATE_RESULT = "state_result"
    }
}