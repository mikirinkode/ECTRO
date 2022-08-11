package id.ac.unila.ee.himatro.ectro.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.utils.Event
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
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


    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData


    fun observeUserData() {
        val loggedUser = auth.currentUser

        if (loggedUser != null) {
            val userInDB: DocumentReference = fireStore.collection(TABLE_USER).document(loggedUser.uid)

            userInDB.get()
                .addOnSuccessListener { document ->
                    val user: User? = document.toObject<User>()

                    if (user != null) {
                        _isError.postValue(false)
                        // save user data to local preferences
                        preferences.startSession(user)
                        _userData.value = User(
                            name = user.name,
                            email = user.email,
                            npm = user.npm,
                            photoUrl = user.photoUrl,
                            linkedin = user.linkedin,
                            instagram = user.instagram,
                            role = user.role,
                            roleRequestStatus = user.roleRequestStatus,
                            lastLoginAt = user.lastLoginAt
                        )
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                    _isError.postValue(true)
                    _responseMessage.postValue(Event(it.message.toString()))
                }
        }
    }

    companion object {
        private const val TAG = "UserViewModel"
    }
}