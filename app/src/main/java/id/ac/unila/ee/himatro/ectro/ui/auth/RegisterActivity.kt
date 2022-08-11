package id.ac.unila.ee.himatro.ectro.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.databinding.ActivityRegisterBinding
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.viewmodel.AuthViewModel
import java.util.regex.Pattern

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeIsLoading()
        binding.apply {
            btnRegister.setOnClickListener {
                val name = edtRegisterName.text.toString().trim()
                val email = edtRegisterEmail.text.toString().trim()
                val password = edtRegisterPassword.text.toString().trim()
                val passwordConf = edtRegisterPasswordConf.text.toString().trim()

                val isValid = checkInputValidation(name, email, password, passwordConf)

                if (isValid) {
                    viewModel.registerUser(name, email, password)
                    viewModel.isError.observe(this@RegisterActivity) { isError ->
                        if (isError){
                            viewModel.responseMessage.observe(this@RegisterActivity){
                                if (it != null) {
                                    it.getContentIfNotHandled()?.let { msg ->
                                        Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            // if not error, then
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            finishAffinity()
                        }
                    }
                }
            }

            btnLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun checkInputValidation(
        name: String,
        email: String,
        password: String,
        passwordConf: String
    ): Boolean {
        binding.apply {
            var isValid = true
            if (TextUtils.isEmpty(name)) {
                edtRegisterName.error = getString(R.string.empty_name)
                isValid = false
            } else if (name.length < 3) {
                edtRegisterName.error = getString(R.string.name_too_short)
                isValid = false
            }

            if (email.isEmpty()) {
                edtRegisterEmail.error = getString(R.string.empty_email)
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtRegisterEmail.error = getString(R.string.invalid_email)
                isValid = false
            }

            if (password.isEmpty()) {
                edtRegisterPassword.error = getString(R.string.empty_password)
                isValid = false
            } else if (!isValidPassword(password)) {
                edtRegisterPassword.error = getString(R.string.invalid_password)
                isValid = false
            }

            if (passwordConf != password) {
                edtRegisterPasswordConf.error = getString(R.string.pass_not_match)
                isValid = false
            }

            return isValid
        }
    }

    private fun isValidPassword(password: CharSequence?): Boolean {
        // password pattern using Regular Expression
        val regex = ("^(?=.*[0-9])"             // password must contain number
                + "(?=.*[a-z])(?=.*[A-Z])"      // password must contain uppercase and lowercase
                + "(?=\\S+$).{8,}$")            // password must not have whitespace and have length more than 8
        val pattern = Pattern.compile(regex)

        return if (password == null) false else pattern.matcher(password).matches()
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
        private const val TAG = "RegisterActivity"
    }
}