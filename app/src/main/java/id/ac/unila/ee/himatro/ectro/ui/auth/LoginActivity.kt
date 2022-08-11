package id.ac.unila.ee.himatro.ectro.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.DARK_MODE_PREF
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.LOGGED_IN
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.LOGIN_STATUS
import id.ac.unila.ee.himatro.ectro.databinding.ActivityLoginBinding
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var preferences: EctroPreferences

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeIsLoading()

        // check preference for dark mode
        val isDark = preferences.getBooleanValues(DARK_MODE_PREF)
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val loginStatus = preferences.getValues(LOGIN_STATUS)
        if (loginStatus == LOGGED_IN) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }

        binding.apply {
            btnLogin.setOnClickListener {
                var isValid = true

                val email = edtLoginEmail.text.toString().trim()
                val password = edtLoginPassword.text.toString().trim()

                if (email.isEmpty()) {
                    edtLoginEmail.error = getString(R.string.empty_email)
                    isValid = false
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtLoginEmail.error = getString(R.string.invalid_email)
                    isValid = false
                }

                if (password.isEmpty()) {
                    edtLoginPassword.error = getString(R.string.empty_password)
                    isValid = false
                }

                if (isValid) {
                    viewModel.loginUser(email, password)
                    viewModel.isError.observe(this@LoginActivity) { isError ->
                        if (isError){
                            viewModel.responseMessage.observe(this@LoginActivity){
                                if (it != null) {
                                    it.getContentIfNotHandled()?.let { msg ->
                                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            // if not error, then
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finishAffinity()
                        }
                    }
                }
            }

            btnRegisterNow.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }
        }
    }

    private fun observeIsLoading() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading){
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.loadingIndicator.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}