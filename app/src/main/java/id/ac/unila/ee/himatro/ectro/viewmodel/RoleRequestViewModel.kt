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
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest
import id.ac.unila.ee.himatro.ectro.utils.DateHelper
import id.ac.unila.ee.himatro.ectro.utils.Event
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils
import javax.inject.Inject

@HiltViewModel
class RoleRequestViewModel @Inject constructor(
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

    private val _roleRequestStatus = MutableLiveData<String>()
    val roleRequestStatus: LiveData<String> = _roleRequestStatus

    private val _roleRequestList = MutableLiveData<ArrayList<RoleRequest>>()
    val roleRequestList: LiveData<ArrayList<RoleRequest>> = _roleRequestList


    fun createRoleRequest() {
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            val requestId =
                preferences.getValues(EctroPreferences.USER_NPM) + System.currentTimeMillis()
            val roleRequest = hashMapOf(
                FirestoreUtils.TABLE_RR_REQUEST_ID to requestId,
                FirestoreUtils.TABLE_RR_STATUS to EctroPreferences.WAITING_STATUS,
                FirestoreUtils.TABLE_RR_APPLICANT_ID to firebaseUser.uid,
                FirestoreUtils.TABLE_RR_APPLICANT_NAME to preferences.getValues(EctroPreferences.USER_NAME),
                FirestoreUtils.TABLE_RR_APPLICANT_NPM to preferences.getValues(EctroPreferences.USER_NPM),
                FirestoreUtils.TABLE_RR_APPLICANT_EMAIL to preferences.getValues(EctroPreferences.USER_EMAIL),
                FirestoreUtils.TABLE_RR_REQUEST_AT to DateHelper.getCurrentDate()
            )

            _isLoading.postValue(true)
            fireStore.collection(FirestoreUtils.TABLE_ROLE_REQUEST)
                .document(requestId)
                .set(roleRequest)
                .addOnSuccessListener {

                    val updateData = hashMapOf(
                        FirestoreUtils.TABLE_USER_REQUEST_STATUS to EctroPreferences.WAITING_STATUS
                    )

                    fireStore.collection(FirestoreUtils.TABLE_USER)
                        .document(firebaseUser.uid)
                        .set(updateData, SetOptions.merge())

                    preferences.setValues(
                        EctroPreferences.ROLE_REQUEST_STATUS,
                        EctroPreferences.WAITING_STATUS
                    )

                    _isLoading.postValue(false)
                    _roleRequestStatus.postValue(EctroPreferences.WAITING_STATUS)
                    _isError.postValue(false)
                    _responseMessage.postValue(Event("Berhasil mengirim permintaan"))

                }
                .addOnFailureListener {
                    _isLoading.postValue(false)
                    Log.e(TAG, it.message.toString())
                    _isError.postValue(true)
                    _responseMessage.postValue(Event(it.message.toString()))
                }
        }
    }

    fun observeRoleRequestList() {
        _isLoading.postValue(true)
        fireStore.collection(FirestoreUtils.TABLE_ROLE_REQUEST)
            .get()
            .addOnSuccessListener { documentList ->
                _isLoading.postValue(false)
                _isError.postValue(false)
                val entityList: ArrayList<RoleRequest> = ArrayList()
                for (document in documentList) {
                    val entity = document.toObject<RoleRequest>()
                    entityList.add(entity)
                }

                _roleRequestList.postValue(entityList)
            }
            .addOnFailureListener {
                _isLoading.postValue(false)
                _isError.postValue(true)
                _responseMessage.postValue(Event(it.message.toString()))
            }
    }

    fun updateRoleRequest(entity: RoleRequest?, department: String, division: String, position: String) {

        val firebaseUser = auth.currentUser

        val updateRequest = hashMapOf(
            FirestoreUtils.TABLE_RR_STATUS to EctroPreferences.COMPLETED_STATUS,
            FirestoreUtils.TABLE_RR_HANDLER_ID to firebaseUser?.uid,
            FirestoreUtils.TABLE_RR_UPDATED_AT to DateHelper.getCurrentDate()
        )

        val updateRole = hashMapOf(
            FirestoreUtils.TABLE_USER_REQUEST_STATUS to EctroPreferences.COMPLETED_STATUS,
            FirestoreUtils.TABLE_USER_ROLE to hashMapOf(
                FirestoreUtils.TABLE_USER_DEPARTMENT to department,
                FirestoreUtils.TABLE_USER_DIVISION to division,
                FirestoreUtils.TABLE_USER_POSITION to position,
            )
        )

        _isLoading.postValue(true)

        // update user data in database
        fireStore.collection(FirestoreUtils.TABLE_USER)
            .document(entity?.applicantId ?: "")
            .set(updateRole, SetOptions.merge())
            .addOnSuccessListener {

                // update request document in database
                fireStore.collection(FirestoreUtils.TABLE_ROLE_REQUEST)
                    .document(entity?.requestId ?: "")
                    .set(updateRequest, SetOptions.merge())
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
        private const val TAG = "RoleRequestViewModel"
    }
}
