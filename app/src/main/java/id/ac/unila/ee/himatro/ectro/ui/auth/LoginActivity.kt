package id.ac.unila.ee.himatro.ectro.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.DARK_MODE_PREF
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.LOGGED_IN
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.LOGIN_STATUS
import id.ac.unila.ee.himatro.ectro.databinding.ActivityLoginBinding
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.utils.DateHelper
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_LAST_LOGIN
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var preferences: EctroPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
                    signInUser(email, password)
                }
            }

            btnRegisterNow.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        binding.loadingIndicator.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser

                    // set last login
                    val lastLogin = hashMapOf(
                        TABLE_USER_LAST_LOGIN to DateHelper.getCurrentDate()
                    )

                    val db = Firebase.firestore

                    if (user != null) {
                        binding.loadingIndicator.visibility = View.GONE

                        db.collection(TABLE_USER)
                            .document(user.uid)
                            .set(lastLogin, SetOptions.merge())

                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    } else {
                        Toast.makeText(
                            baseContext, getString(R.string.authentication_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.loadingIndicator.visibility = View.GONE
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.loadingIndicator.visibility = View.GONE
                }
            }
    }



    companion object {
        private const val TAG = "LoginActivity"
    }
}