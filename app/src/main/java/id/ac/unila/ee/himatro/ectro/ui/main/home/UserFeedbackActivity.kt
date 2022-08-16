package id.ac.unila.ee.himatro.ectro.ui.main.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.databinding.ActivityUserFeedbackBinding
import javax.inject.Inject

@AndroidEntryPoint
class UserFeedbackActivity : AppCompatActivity() {

    private val binding: ActivityUserFeedbackBinding by lazy {
        ActivityUserFeedbackBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var fireStore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            btnBack.setOnClickListener { onBackPressed() }
            btnSubmit.setOnClickListener {
                var isValid = true
                val feedback = edtUserExperience.text.toString().trim()

                if (feedback.isEmpty()){
                    edtUserExperience.error = ""
                    isValid = false
                }

                if (isValid){
                    val docRef = fireStore.collection("feedbacks").document()
                    val user = auth.currentUser
                    val userId = user?.uid

                    val feedbackItem = hashMapOf(
                        "content" to feedback,
                        "userId" to userId
                    )

                    docRef.set(feedbackItem)
                        .addOnSuccessListener {
                            finish()
                        }
                        .addOnFailureListener {

                        }
                }
            }
        }
    }
}