package id.ac.unila.ee.himatro.ectro.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.EventEntity
import id.ac.unila.ee.himatro.ectro.utils.Event
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


    private val _eventList = MutableLiveData<ArrayList<EventEntity>>()
    val eventList: LiveData<ArrayList<EventEntity>> = _eventList


    fun observeEventList() {
        fireStore.collection("events").get()
            .addOnSuccessListener { documentList ->
                val eventEntityList: ArrayList<EventEntity> = ArrayList()
                for (document in documentList) {
                    if (document != null) {
                        eventEntityList.add(
                            document.toObject()
                        )
                    }
                }
                _eventList.postValue(eventEntityList)
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
                _isError.postValue(true)
                _responseMessage.postValue(Event(it.message.toString()))
            }
    }

    companion object {
        private const val TAG = "EventViewModel"
    }
}