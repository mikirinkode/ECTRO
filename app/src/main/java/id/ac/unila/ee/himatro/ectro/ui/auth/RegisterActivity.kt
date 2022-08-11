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
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.databinding.ActivityRegisterBinding
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.utils.DateHelper
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_ACTIVE_PERIOD
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_DEPARTMENT
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_DIVISION
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_EMAIL
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_INSTAGRAM
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_LAST_LOGIN
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_LINKEDIN
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_NAME
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_NPM
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_PHOTO_URL
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_POSITION
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_ROLE
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var fireStore: FirebaseFirestore

    @Inject
    lateinit var preferences: EctroPreferences

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

        // create user entity for fireStore
        val user = hashMapOf(
            TABLE_USER_NAME to name,
            TABLE_USER_EMAIL to email,
            TABLE_USER_NPM to "",
            TABLE_USER_PHOTO_URL to "",
            TABLE_USER_LINKEDIN to "",
            TABLE_USER_INSTAGRAM to "",
            TABLE_USER_ROLE to hashMapOf(
                TABLE_USER_DEPARTMENT to "",
                TABLE_USER_DIVISION to "",
                TABLE_USER_POSITION to "",
                TABLE_USER_ACTIVE_PERIOD to ""
            ),
            TABLE_USER_LAST_LOGIN to DateHelper.getCurrentDate()
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
                        fireStore.collection(TABLE_USER).document(firebaseUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    getString(R.string.successfully_create_account),
                                    Toast.LENGTH_SHORT
                                ).show()

                                binding.loadingIndicator.visibility = View.GONE

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
                + "(?=\\S+$).{8,}$")            // password must not have whitespace and have length more than 8
        val pattern = Pattern.compile(regex)

        return if (password == null) false else pattern.matcher(password).matches()
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}