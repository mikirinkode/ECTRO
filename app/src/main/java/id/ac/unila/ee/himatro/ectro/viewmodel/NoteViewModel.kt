package id.ac.unila.ee.himatro.ectro.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.NoteEntity
import id.ac.unila.ee.himatro.ectro.utils.DateHelper
import id.ac.unila.ee.himatro.ectro.utils.Event
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val preferences: EctroPreferences
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _noteAlreadyAdded = MutableLiveData<Boolean>()
    val noteAlreadyAdded: LiveData<Boolean> = _noteAlreadyAdded

    private val _noteEntity = MutableLiveData<NoteEntity>()
    val noteEntity: LiveData<NoteEntity> = _noteEntity

    private val _responseMessage = MutableLiveData<Event<String>>()
    val responseMessage: LiveData<Event<String>> = _responseMessage

    fun addNote(text: String, eventId: String) {

        val docRef = fireStore.collection(FirestoreUtils.TABLE_NOTES).document()
        val loggedUser = auth.currentUser

        val note = hashMapOf(
            FirestoreUtils.TABLE_NOTE_ID to docRef.id,
            FirestoreUtils.TABLE_NOTE_EVENT_ID to eventId,
            FirestoreUtils.TABLE_NOTE_USER_ID to loggedUser?.uid,
            FirestoreUtils.TABLE_NOTE_USER_ID to loggedUser?.uid,
            FirestoreUtils.TABLE_NOTE_CONTENT to text,
            FirestoreUtils.TABLE_NOTE_CREATED_AT to DateHelper.getCurrentDate()
        )

        _isLoading.postValue(true)
        docRef.set(note)
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

    fun checkNoteByEventId(eventId: String) {
        _isLoading.postValue(true)
        fireStore.collection(FirestoreUtils.TABLE_NOTES)
            .whereEqualTo(FirestoreUtils.TABLE_NOTE_EVENT_ID, eventId)
            .get()
            .addOnSuccessListener { documentList ->
                _isLoading.postValue(false)
                _isError.postValue(false)
                if (documentList.isEmpty){
                    _noteAlreadyAdded.postValue(false)
                } else {
                    _noteAlreadyAdded.postValue(true)
                    for (document in documentList){
                        _noteEntity.postValue(document.toObject())
                    }
                }
            }
            .addOnFailureListener {
                _isLoading.postValue(false)
                _isError.postValue(true)
                _responseMessage.postValue(Event(it.message.toString()))
                Log.e(TAG, it.message.toString())
            }
    }

    fun updateNote(text: String, noteId: String) {
        _isLoading.postValue(true)

        val updateNote = hashMapOf(
            FirestoreUtils.TABLE_NOTE_CONTENT to text,
            FirestoreUtils.TABLE_NOTE_UPDATED_AT to DateHelper.getCurrentDate()
        )

        fireStore.collection(FirestoreUtils.TABLE_NOTES)
            .document(noteId)
            .set(updateNote, SetOptions.merge())
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


    companion object {
        private const val TAG = "NoteViewModel"
    }
}