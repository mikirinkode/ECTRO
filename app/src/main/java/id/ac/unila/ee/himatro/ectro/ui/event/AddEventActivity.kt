package id.ac.unila.ee.himatro.ectro.ui.event

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.model.Event
import id.ac.unila.ee.himatro.ectro.databinding.ActivityAddEventBinding
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.utils.DateHelper

class AddEventActivity : AppCompatActivity() {

    private val binding: ActivityAddEventBinding by lazy {
        ActivityAddEventBinding.inflate(layoutInflater)
    }


    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private val firebaseUser: FirebaseUser? by lazy {
        FirebaseAuth.getInstance().currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // set Dropdown for online event type
        val arrayString: Array<String> = resources.getStringArray(R.array.online_event_media)
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayString)
        binding.actOnlineEventMedia.setAdapter(arrayAdapter)

        binding.apply {

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
                    eventType = "Offline"
                } else if (rgEventType.checkedRadioButtonId == R.id.rb_online) {
                    eventType = "Online"
                }

                val eventPlace = edtEventPlace.text.toString().trim()
                val eventDate = edtEventDate.text.toString().trim()
                val eventTime = edtEventTime.text.toString().trim()

                val attendanceForm = switchAttendance.isChecked
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
                } else if (!eventType.isNullOrEmpty() && eventType == "Online" && additionalLink.isEmpty() && onlineEventMedia == onlineMeeting) {
                    edtAdditionalLink.error = getString(R.string.empty_meeting_link)
                    isValid = false
                }

                if (!eventType.isNullOrEmpty() && eventType == "Online" && onlineEventMedia.isEmpty()) {
                    actOnlineEventMedia.error = getString(R.string.please_choose_online_media)
                    isValid = false
                }

                if (additionalName.isNotEmpty() && additionalLink.isEmpty()) {
                    edtAdditionalLink.error = getString(R.string.empty_additional_link)
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
                    if (firebaseUser != null) {
                        val event = hashMapOf(
                            "name" to eventName,
                            "desc" to eventDesc,
                            "type" to eventType,
                            "date" to eventDate,
                            "time" to eventTime,
                            "place" to eventPlace,
                            "category" to eventCategory,
                            "isNeedAttendanceForm" to attendanceForm,
                            "extraActionName" to additionalName,
                            "extraActionLink" to additionalLink,
                            "actionAfterAttendance" to actionAfterAttendance,
                            "userId" to firebaseUser?.uid,
                            "createdAt" to DateHelper.getCurrentDate()
                        )

                        loadingIndicator.visibility = View.VISIBLE
                        db.collection("events")
                            .document()
                            .set(event)
                            .addOnSuccessListener {
                                loadingIndicator.visibility = View.GONE
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
                                finish()
                            }
                            .addOnFailureListener {
                                loadingIndicator.visibility = View.GONE
                                Toast.makeText(
                                    this@AddEventActivity,
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e(TAG, it.message.toString())
                            }
                    } else {
                        Toast.makeText(
                            this@AddEventActivity,
                            getString(R.string.an_error_occurred),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            btnBack.setOnClickListener { onBackPressed() }

            // restore saved data
            if (savedInstanceState != null) {
                val savedState = savedInstanceState.getParcelable<Event>(STATE_RESULT)
                if (savedState != null) {
                    edtEventName.setText(savedState.name)
                    edtEventDesc.setText(savedState.name)

                    if (rgEventType.checkedRadioButtonId == R.id.rb_offline) {
                        rbOffline.isChecked = true
                    } else if (rgEventType.checkedRadioButtonId == R.id.rb_online) {
                        rbOnline.isChecked = true
                    }
                    edtEventPlace.setText(savedState.name)
                    edtEventDate.setText(savedState.name)
                    edtEventTime.setText(savedState.name)
                    edtEventCategory.setText(savedState.name)
                    edtAdditionalName.setText(savedState.extraActionName)
                    edtAdditionalLink.setText(savedState.extraActionLink)
                    if (savedState.isNeedAttendanceForm != null) {
                        switchAttendance.isChecked = savedState.isNeedAttendanceForm!!
                    }
                    if (savedState.actionAfterAttendance != null) {
                        switchActionAfterAttendance.isChecked = savedState.actionAfterAttendance!!
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
                Event(
                    name = edtEventName.text.toString().trim(),
                    desc = edtEventDesc.text.toString().trim(),
                    category = edtEventCategory.text.toString().trim(),
                    type = eventType,
                    place = edtEventPlace.text.toString().trim(),
                    date = edtEventDate.text.toString().trim(),
                    time = edtEventTime.text.toString().trim(),
                    isNeedAttendanceForm = switchAttendance.isChecked,
                    extraActionName = edtAdditionalName.text.toString().trim(),
                    extraActionLink = edtAdditionalLink.text.toString().trim(),
                    actionAfterAttendance = switchActionAfterAttendance.isChecked,
                )
            )
        }
    }

    companion object {
        private const val TAG = "CreateEventActivity"
        private const val STATE_RESULT = "state_result"
    }

}