package id.ac.unila.ee.himatro.ectro.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.UserAttendance
import id.ac.unila.ee.himatro.ectro.utils.DateHelper
import id.ac.unila.ee.himatro.ectro.utils.Event
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_ATTENDANCES
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_ATTENDANCE_EVENT_ID
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_ATTENDANCE_IS_ATTEND
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_ATTENDANCE_USER_ID
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val preferences: EctroPreferences
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _responseMessage = MutableLiveData<Event<String>>()
    val responseMessage: LiveData<Event<String>> = _responseMessage

    private val _hasFilledAttendance = MutableLiveData<Boolean>()
    val hasFilledAttendance: LiveData<Boolean> = _hasFilledAttendance


    fun insertUserAttendance(
        reasonCannotAttend: String,
        attendanceStatus: String,
        eventId: String,
        isAttend: Boolean
    ) {
        val userAttendanceRef = fireStore.collection(TABLE_ATTENDANCES).document()
        val loggedUser = auth.currentUser

        val name = preferences.getValues(EctroPreferences.USER_NAME)

        val userAttendance = hashMapOf(
            FirestoreUtils.TABLE_ATTENDANCE_ID to userAttendanceRef.id,
            FirestoreUtils.TABLE_ATTENDANCE_EVENT_ID to eventId,
            FirestoreUtils.TABLE_ATTENDANCE_USER_ID to loggedUser?.uid,
            FirestoreUtils.TABLE_ATTENDANCE_USER_NAME to name,
            FirestoreUtils.TABLE_ATTENDANCE_STATUS to attendanceStatus,
            FirestoreUtils.TABLE_ATTENDANCE_IS_ATTEND to isAttend,
            FirestoreUtils.TABLE_ATTENDANCE_REASON to reasonCannotAttend,
            FirestoreUtils.TABLE_ATTENDANCE_DATE to DateHelper.getCurrentDate()
        )

        _isLoading.postValue(true)
        userAttendanceRef
            .set(userAttendance)
            .addOnSuccessListener {
                _isLoading.postValue(false)
                _isError.postValue(false)
            }
            .addOnFailureListener {
                _isLoading.postValue(false)
                _isError.postValue(true)
                _responseMessage.postValue(Event(it.message.toString()))
                Log.e(TAG, it.message.toString())
            }
    }

    fun checkAttendanceFilled(eventId: String) {
        val loggedUser = auth.currentUser

        fireStore.collection(TABLE_ATTENDANCES)
            .whereEqualTo(TABLE_ATTENDANCE_USER_ID, loggedUser?.uid ?: "")
            .whereEqualTo(TABLE_ATTENDANCE_EVENT_ID, eventId)
            .get()
            .addOnSuccessListener { documentList ->
                if (documentList.isEmpty) {
                    _hasFilledAttendance.postValue(false)
                } else {
                    _hasFilledAttendance.postValue(true)
                }
            }
    }

    fun getTotalAttendees(eventId: String): LiveData<Int> {
        val total = MutableLiveData<Int>()

        fireStore.collection(TABLE_ATTENDANCES)
            .whereEqualTo(TABLE_ATTENDANCE_EVENT_ID, eventId)
            .whereEqualTo(TABLE_ATTENDANCE_IS_ATTEND, true)
            .get()
            .addOnSuccessListener { documentList ->
                total.postValue(documentList.size())
            }

        return total
    }

    fun observeAttendanceByEventId(eventId: String?): LiveData<List<UserAttendance>> {
        val attendanceList = MutableLiveData<List<UserAttendance>>()

        _isLoading.postValue(true)
        fireStore.collection(TABLE_ATTENDANCES)
            .whereEqualTo(TABLE_ATTENDANCE_EVENT_ID, eventId)
            .get()
            .addOnSuccessListener { documentList ->
                _isLoading.postValue(false)
                _isError.postValue(false)
                val arrayList = ArrayList<UserAttendance>()
                for (document in documentList) {
                    arrayList.add(document.toObject())
                }
                attendanceList.postValue(arrayList)
            }
            .addOnFailureListener {
                _isLoading.postValue(false)
                _isError.postValue(true)
                _responseMessage.postValue(Event(it.message.toString()))
                Log.e(TAG, it.message.toString())
            }

        return attendanceList
    }


    companion object {
        private const val TAG = "AttendanceViewModel"
    }
}