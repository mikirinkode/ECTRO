package id.ac.unila.ee.himatro.ectro.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.databinding.ActivityUserFeedbackBinding
import id.ac.unila.ee.himatro.ectro.viewmodel.FeedbackViewModel

@AndroidEntryPoint
class UserFeedbackActivity : AppCompatActivity() {

    private val binding: ActivityUserFeedbackBinding by lazy {
        ActivityUserFeedbackBinding.inflate(layoutInflater)
    }

    private val viewModel: FeedbackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeIsLoading()

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
                    viewModel.createFeedback(feedback).observe(this@UserFeedbackActivity) { isSuccess ->
                        if (isSuccess){
                            Toast.makeText(this@UserFeedbackActivity, getString(R.string.successfully_sent_feedback), Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@UserFeedbackActivity, getString(R.string.failed_sent_feedback), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }

    private fun observeIsLoading() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.loadingIndicator.visibility = View.GONE
            }
        }
    }
}