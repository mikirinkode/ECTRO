package id.ac.unila.ee.himatro.ectro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val preferences: EctroPreferences
) : ViewModel() {


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun createFeedback(content: String): LiveData<Boolean> {
        val _isSuccess = MutableLiveData<Boolean>()
        val isSuccess: LiveData<Boolean> = _isSuccess

        val docRef = fireStore.collection("feedbacks").document()
        val user = auth.currentUser
        val userId = user?.uid
        val userName = preferences.getValues(EctroPreferences.USER_NAME)

        val feedbackItem = hashMapOf(
            "content" to content,
            "userId" to userId,
            "name" to userName
        )

        _isLoading.postValue(true)
        docRef.set(feedbackItem)
            .addOnSuccessListener {
                _isLoading.postValue(false)
                _isSuccess.postValue(true)
            }
            .addOnFailureListener {
                _isSuccess.postValue(false)
                _isLoading.postValue(false)

            }

        return isSuccess
    }
}