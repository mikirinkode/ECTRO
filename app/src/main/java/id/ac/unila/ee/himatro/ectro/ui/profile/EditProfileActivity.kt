package id.ac.unila.ee.himatro.ectro.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.databinding.ActivityEditProfileBinding
import id.ac.unila.ee.himatro.ectro.ui.auth.RegisterActivity
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity

class EditProfileActivity : AppCompatActivity() {

    private val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
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

        observeUserData()

        binding.apply {

            btnSave.setOnClickListener {
                val name = edtFullName.text.toString().trim()
                val npm = edtStudentId.text.toString().trim()
                val instagram = edtInstagram.text.toString().trim()
                val linkedin = edtLinkedin.text.toString().trim()

                val isValid = checkInputValidation(name, npm)

                if (isValid) {
                    loadingIndicator.visibility = View.VISIBLE
                    val user = auth.currentUser
                    if (user != null) {
                        val newData = hashMapOf(
                            "name" to name,
                            "npm" to npm,
                            "instagram" to instagram,
                            "linkedin" to linkedin,
                        )

                        fireStore.collection("users")
                            .document(user.uid)
                            .set(newData, SetOptions.merge())
                            .addOnSuccessListener {
                                loadingIndicator.visibility = View.GONE
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    getString(R.string.successfully_update_data),
                                    Toast.LENGTH_SHORT
                                ).show()

                                // if success, update data on local preferences then back to Main
                                updateLocalData(name, npm, instagram, linkedin)

                                startActivity(
                                    Intent(
                                        this@EditProfileActivity,
                                        MainActivity::class.java
                                    )
                                )
                                finishAffinity()
                            }
                            .addOnFailureListener {
                                loadingIndicator.visibility = View.GONE
                                Log.e(TAG, it.message.toString())
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    }else {
                        Toast.makeText(
                            baseContext, getString(R.string.update_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                        loadingIndicator.visibility = View.GONE
                    }
                }
            }

            btnBack.setOnClickListener { onBackPressed() }
        }
    }


    private fun observeUserData() {
        val loggedUser = auth.currentUser

        if (loggedUser != null) {
            val userInDB: DocumentReference = fireStore.collection("users").document(loggedUser.uid)

            userInDB.get()
                .addOnSuccessListener { document ->
                    val user: User? = document.toObject<User>()

                    if (user != null) {
                        binding.apply {
                            edtFullName.setText(user.name)
                            edtStudentId.setText(user.npm)
                            edtInstagram.setText(user.instagram)
                            edtLinkedin.setText(user.linkedin)

                            if (user.userPhotoUrl.isEmpty()) {
                                Glide.with(this@EditProfileActivity)
                                    .load(R.drawable.ic_default_profile)
                                    .into(ivUserPhoto)
                            } else {
                                Glide.with(this@EditProfileActivity)
                                    .load(user.userPhotoUrl)
                                    .into(ivUserPhoto)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                    Toast.makeText(
                        this,
                        getString(R.string.database_connection_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun checkInputValidation(name: String, npm: String): Boolean {
        binding.apply {
            var isValid = true

            if (TextUtils.isEmpty(name)) {
                edtFullName.error = getString(R.string.empty_name)
                isValid = false
            } else if (name.length < 3) {
                edtFullName.error = getString(R.string.name_too_short)
                isValid = false
            }

            if (!validStudentId(npm)) {
                isValid = false
                edtStudentId.error = getString(R.string.invalid_id)
            }

            return isValid
        }
    }

    private fun validStudentId(npm: String): Boolean {
        // example of valid NPM: 2015061057
        val validDepartment = "15"
        val electricalPrograms = "031"
        val informaticsPrograms = "061"

        val isValid: Boolean = when {
            npm.length > 10 -> false
            npm.length < 10 -> false
            npm.length == 10 -> {
                val currentDepartment = npm.substring(2, 4)
                val currentPrograms = npm.substring(4, 7)

                if (currentDepartment == validDepartment) {
                    currentPrograms == electricalPrograms || currentPrograms == informaticsPrograms
                } else {
                    false
                }
            }
            else -> true
        }

        return isValid
    }

    private fun updateLocalData(name: String, npm: String, instagram: String, linkedin: String) {
        preferences.setValues(EctroPreferences.USER_NAME, name)
        preferences.setValues(EctroPreferences.USER_NPM, npm)
        preferences.setValues(EctroPreferences.USER_INSTAGRAM_ACCOUNT, instagram)
        preferences.setValues(EctroPreferences.USER_LINKEDIN_ACCOUNT, linkedin)
    }

    companion object {
        private const val TAG = "EditProfileActivity"
    }
}