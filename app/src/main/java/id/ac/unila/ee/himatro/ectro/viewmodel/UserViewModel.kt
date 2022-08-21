package id.ac.unila.ee.himatro.ectro.viewmodel

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.ui.profile.EditProfileActivity
import id.ac.unila.ee.himatro.ectro.utils.Event
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_INSTAGRAM
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_LINKEDIN
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_NAME
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_NPM
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

    private val _isUpdateSuccess = MutableLiveData<Boolean>()
    val isUpdateSuccess: LiveData<Boolean> = _isUpdateSuccess

    private val _responseMessage = MutableLiveData<Event<String>>()
    val responseMessage: LiveData<Event<String>> = _responseMessage

    private val _loggedUserData = MutableLiveData<User>()
    val loggedUserData: LiveData<User> = _loggedUserData

    fun observeLoggedUserData() {
        val loggedUser = auth.currentUser

        if (loggedUser != null) {
            val userInDB: DocumentReference =
                fireStore.collection(TABLE_USER).document(loggedUser.uid)

            _isLoading.postValue(true)
            userInDB.get()
                .addOnSuccessListener { document ->
                    val user: User? = document.toObject<User>()
                    _isLoading.postValue(false)

                    if (user != null) {
                        _isError.postValue(false)
                        // save user data to local preferences
                        preferences.startSession(user)
                        _loggedUserData.value = User(
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
                    _isLoading.postValue(false)
                    _responseMessage.postValue(Event(it.message.toString()))
                }
        }
    }

    fun getUserDataByUid(uid: String): LiveData<User> {
        val userData = MutableLiveData<User>()
        val userInDB: DocumentReference = fireStore.collection(TABLE_USER).document(uid)
        userInDB.get()
            .addOnSuccessListener { document ->
                _isLoading.postValue(false)
                val user: User? = document.toObject()

                if (user != null) {
                    _isError.postValue(false)
                    userData.postValue(user!!)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
                _isError.postValue(true)
                _isLoading.postValue(false)
                _responseMessage.postValue(Event(it.message.toString()))
            }
        return userData
    }

    fun updateUserProfile(name: String, npm: String, instagram: String, linkedin: String) {
        _isLoading.postValue(true)
        val user = auth.currentUser
        if (user != null) {
            val newData = hashMapOf(
                TABLE_USER_NAME to name,
                TABLE_USER_NPM to npm,
                TABLE_USER_INSTAGRAM to instagram,
                TABLE_USER_LINKEDIN to linkedin,
            )

            fireStore.collection(TABLE_USER)
                .document(user.uid)
                .set(newData, SetOptions.merge())
                .addOnSuccessListener {
                    _isLoading.postValue(false)
                    _isUpdateSuccess.postValue(true)


                    // if success, update data on local preferences then back to Main
                    preferences.setValues(EctroPreferences.USER_NAME, name)
                    preferences.setValues(EctroPreferences.USER_NPM, npm)
                    preferences.setValues(EctroPreferences.USER_INSTAGRAM_ACCOUNT, instagram)
                    preferences.setValues(EctroPreferences.USER_LINKEDIN_ACCOUNT, linkedin)


                }
                .addOnFailureListener {
                    _isLoading.postValue(false)
                    _isUpdateSuccess.postValue(false)
                    _responseMessage.postValue(Event(it.message.toString()))
                }

        } else {
            Log.e(TAG, "FailUpdateUserData")
            _isLoading.postValue(false)
        }
    }

    companion object {
        private const val TAG = "UserViewModel"
    }
}