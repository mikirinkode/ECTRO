package id.ac.unila.ee.himatro.ectro.ui.event

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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

        binding.apply {

            btnAddEvent.setOnClickListener {
                var isValid = true
                val eventName = edtEventName.text.toString().trim()
                val eventDesc = edtEventDesc.text.toString().trim()

                val eventType =
                    if (rgEventType.checkedRadioButtonId == R.id.rb_offline) "Offline" else "Online"
                val eventPlace = edtEventPlace.text.toString().trim()
                val eventDate = edtEventDate.text.toString().trim()
                val eventTime = edtEventTime.text.toString().trim()

                val eventCategory = edtEventCategory.text.toString().trim()
                val attendanceForm = switchAttendance.isChecked

                if (eventName.isEmpty()) {
                    edtEventName.error = getString(R.string.empty_event_name)
                    isValid = false
                }
                if (eventType.isEmpty()) {
                    Toast.makeText(
                        this@AddEventActivity,
                        getString(R.string.empty_event_type),
                        Toast.LENGTH_SHORT
                    ).show()
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
                            "userId" to firebaseUser?.uid
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

                    if (rgEventType.checkedRadioButtonId == R.id.rb_offline){
                        rbOffline.isChecked = true
                    } else if (rgEventType.checkedRadioButtonId == R.id.rb_online){
                        rbOnline.isChecked = true
                    }
                    edtEventPlace.setText(savedState.name)
                    edtEventDate.setText(savedState.name)
                    edtEventTime.setText(savedState.name)
                    edtEventCategory.setText(savedState.name)
                    switchAttendance.isChecked = savedState.isNeedAttendanceForm
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
                    edtEventName.text.toString().trim(),
                    edtEventDesc.text.toString().trim(),
                    eventType,
                    edtEventPlace.text.toString().trim(),
                    edtEventDate.text.toString().trim(),
                    edtEventTime.text.toString().trim(),
                    edtEventCategory.text.toString().trim(),
                    switchAttendance.isChecked,
                )
            )
        }
    }

    companion object {
        private const val TAG = "CreateEventActivity"
        private const val STATE_RESULT = "state_result"
    }

}