package id.ac.unila.ee.himatro.ectro.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.LOGGED_IN
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.LOGIN_STATUS
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_EMAIL
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_NAME
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.data.model.UserRole
import id.ac.unila.ee.himatro.ectro.databinding.ActivityRegisterBinding
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val fireStore: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private val preferences: EctroPreferences by lazy {
        EctroPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {

            btnRegister.setOnClickListener {
                val name = edtRegisterName.text.toString().trim()
                val email = edtRegisterEmail.text.toString().trim()
                val password = edtRegisterPassword.text.toString().trim()
                val passwordConf = edtRegisterPasswordConf.text.toString().trim()

                val isValid = checkInputValidation(name, email, password, passwordConf)

                if (isValid) {
                    registerUser(name, email, password)
                }
            }

            btnLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        binding.loadingIndicator.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser? = task.result.user
                    if (firebaseUser != null) {
                        firebaseUser.sendEmailVerification()

                        val lastLogin = System.currentTimeMillis().toString()

                        // create user
                        val user = hashMapOf(
                            "email" to email,
                            "name" to name,
                            "npm" to "",
                            "userPhotoUrl" to "",
                            "linkedin" to "",
                            "instagram" to "",
                            "role" to hashMapOf(
                                "department" to "",
                                "division" to "",
                                "position" to ""
                            ),
                            "lastLoginAt" to lastLogin
                        )

                        // add new user document to fireStore
                        fireStore.collection("users").document(firebaseUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                binding.loadingIndicator.visibility = View.GONE
                                Toast.makeText(
                                    this,
                                    getString(R.string.successfully_create_account),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                                val userEntity = User(
                                    email,
                                    name,
                                    "",
                                    "",
                                    "",
                                    "",
                                    UserRole("", "", ""),
                                    lastLogin
                                )
                                // save user info to preferences
                                preferences.startSession(userEntity)

                                startActivity(
                                    Intent(
                                        this@RegisterActivity,
                                        MainActivity::class.java
                                    )
                                )
                                finishAffinity()
                            }
                            .addOnFailureListener {
                                binding.loadingIndicator.visibility = View.GONE
                                Toast.makeText(
                                    this,
                                    getString(R.string.failed_create_account),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                Log.e(TAG, it.message.toString())
                            }
                    }
                } else {
                    Log.e(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@RegisterActivity,
                        getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.loadingIndicator.visibility = View.GONE
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
                + "(?=\\S+$).{6,}$")            // password must not have whitespace and have length more than 6
        val pattern = Pattern.compile(regex)

        return if (password == null) false else pattern.matcher(password).matches()
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}