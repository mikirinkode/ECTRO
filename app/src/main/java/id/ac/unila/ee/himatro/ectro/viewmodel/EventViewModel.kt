package id.ac.unila.ee.himatro.ectro.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.EventEntity
import id.ac.unila.ee.himatro.ectro.data.model.UserAttendance
import id.ac.unila.ee.himatro.ectro.utils.DateHelper
import id.ac.unila.ee.himatro.ectro.utils.Event
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_EVENTS
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_EVENT_CREATED_AT
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
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

    fun observeEventList(limit: Long): LiveData<List<EventEntity>> {
        val eventList = MutableLiveData<List<EventEntity>>()

        _isLoading.postValue(true)
        fireStore.collection(TABLE_EVENTS)
            .orderBy(TABLE_EVENT_CREATED_AT, Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener { documentList ->
                _isLoading.postValue(false)
                val newList: ArrayList<EventEntity> = ArrayList()
                for (document in documentList) {
                    if (document != null) {
                        newList.add(
                            document.toObject()
                        )
                    }
                }
                eventList.postValue(newList)
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
                _isLoading.postValue(false)
                _isError.postValue(true)
                _responseMessage.postValue(Event(it.message.toString()))
            }

        return eventList
    }

    fun getAllEventList(): LiveData<List<EventEntity>> {
        val eventList = MutableLiveData<List<EventEntity>>()

        _isLoading.postValue(true)
        fireStore.collection(TABLE_EVENTS)
            .orderBy(TABLE_EVENT_CREATED_AT, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documentList ->
                _isLoading.postValue(false)
                val newList: ArrayList<EventEntity> = ArrayList()
                for (document in documentList) {
                    if (document != null) {
                        newList.add(
                            document.toObject()
                        )
                    }
                }
                eventList.postValue(newList)
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
                _isLoading.postValue(false)
                _isError.postValue(true)
                _responseMessage.postValue(Event(it.message.toString()))
            }

        return eventList
    }

    fun createEvent(
        eventName: String,
        eventDesc: String,
        eventType: String,
        eventDate: String,
        eventTime: String,
        eventPlace: String,
        eventCategory: String,
        isNeedNotes: Boolean,
        isNeedAttendance: Boolean,
        additionalName: String,
        additionalLink: String,
        actionAfterAttendance: Boolean
    ) {
        val firebaseUser = auth.currentUser

        _isLoading.postValue(true)
        if (firebaseUser != null) {
            val eventDocumentRef = fireStore.collection(TABLE_EVENTS).document()

            val event = hashMapOf(
                FirestoreUtils.TABLE_EVENT_ID to eventDocumentRef.id,
                FirestoreUtils.TABLE_EVENT_NAME to eventName,
                FirestoreUtils.TABLE_EVENT_DESC to eventDesc,
                FirestoreUtils.TABLE_EVENT_TYPE to eventType,
                FirestoreUtils.TABLE_EVENT_DATE to eventDate,
                FirestoreUtils.TABLE_EVENT_TIME to eventTime,
                FirestoreUtils.TABLE_EVENT_PLACE to eventPlace,
                FirestoreUtils.TABLE_EVENT_CATEGORY to eventCategory,
                FirestoreUtils.TABLE_EVENT_NEED_NOTES to isNeedNotes,
                FirestoreUtils.TABLE_EVENT_ATTENDANCE_FORM to isNeedAttendance,
                FirestoreUtils.TABLE_EVENT_EXTRA_ACTION_NAME to additionalName,
                FirestoreUtils.TABLE_EVENT_EXTRA_ACTION_LINK to additionalLink,
                FirestoreUtils.TABLE_EVENT_ACTION_AFTER_ATTENDANCE to actionAfterAttendance,
                FirestoreUtils.TABLE_EVENT_CREATOR_ID to firebaseUser?.uid,
                TABLE_EVENT_CREATED_AT to DateHelper.getCurrentDate()
            )


            eventDocumentRef.set(event)
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
    }

    companion object {
        private const val TAG = "EventViewModel"
    }
}