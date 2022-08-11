package id.ac.unila.ee.himatro.ectro.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.utils.DateHelper
import id.ac.unila.ee.himatro.ectro.utils.Event
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
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

    fun loginUser(email: String, password: String) {
        _isLoading.postValue(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    _isLoading.postValue(false)

                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser

                    // set last login
                    val lastLogin = hashMapOf(
                        FirestoreUtils.TABLE_USER_LAST_LOGIN to DateHelper.getCurrentDate()
                    )

                    if (user != null) {
                        _isError.postValue(false)

                        // Post Last Login Data to Collection
                        fireStore.collection(FirestoreUtils.TABLE_USER)
                            .document(user.uid)
                            .set(lastLogin, SetOptions.merge())

                    } else {
                        _isError.postValue(true)
                        _responseMessage.postValue(Event(task.exception?.message.toString()))
                    }
                } else {
                    _isLoading.postValue(false)
                    _isError.postValue(true)
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "signInWithEmail:failure", task.exception)
                    _responseMessage.postValue(Event(task.exception?.message.toString()))
                }
            }
    }

    fun registerUser(name: String, email: String, password: String) {
        _isLoading.postValue(true)

        // create user entity for fireStore
        val user = hashMapOf(
            FirestoreUtils.TABLE_USER_NAME to name,
            FirestoreUtils.TABLE_USER_EMAIL to email,
            FirestoreUtils.TABLE_USER_NPM to "",
            FirestoreUtils.TABLE_USER_PHOTO_URL to "",
            FirestoreUtils.TABLE_USER_LINKEDIN to "",
            FirestoreUtils.TABLE_USER_INSTAGRAM to "",
            FirestoreUtils.TABLE_USER_ROLE to hashMapOf(
                FirestoreUtils.TABLE_USER_DEPARTMENT to "",
                FirestoreUtils.TABLE_USER_DIVISION to "",
                FirestoreUtils.TABLE_USER_POSITION to "",
                FirestoreUtils.TABLE_USER_ACTIVE_PERIOD to ""
            ),
            FirestoreUtils.TABLE_USER_LAST_LOGIN to DateHelper.getCurrentDate()
        )

        // create user entity for local preference
        val userEntity = User(
            email = email,
            name = name,
            lastLoginAt = DateHelper.getCurrentDate()
        )

        // try to create new user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser? = task.result.user

                    if (firebaseUser != null) {
                        firebaseUser.sendEmailVerification()

                        // try to add new user document to fireStore
                        fireStore.collection(FirestoreUtils.TABLE_USER).document(firebaseUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                _isLoading.postValue(false)
                                _isError.postValue(false)

                                // save user info to preferences
                                preferences.startSession(userEntity)

                            }
                            .addOnFailureListener {
                                // failed to add new user document to fireStore
                                _isLoading.postValue(false)
                                _isError.postValue(true)
                                _responseMessage.postValue(Event(task.exception?.message.toString()))
                            }
                    }
                } else {
                    // failed to create new user
                    _isLoading.postValue(false)
                    _isError.postValue(true)
                    _responseMessage.postValue(Event(task.exception?.message.toString()))
                    Log.e(TAG, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}