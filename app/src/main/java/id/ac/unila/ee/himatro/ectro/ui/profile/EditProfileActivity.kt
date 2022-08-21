package id.ac.unila.ee.himatro.ectro.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.databinding.ActivityEditProfileBinding
import id.ac.unila.ee.himatro.ectro.ui.auth.RegisterActivity
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.viewmodel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }

    private val viewModel: UserViewModel by viewModels()

    @Inject
    lateinit var preferences: EctroPreferences

    @Inject
    lateinit var fireStore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

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

                    viewModel.updateUserProfile(name, npm, instagram, linkedin)

                    viewModel.isUpdateSuccess.observe(this@EditProfileActivity){ isUpdateSuccess ->
                        if (!isUpdateSuccess){
                            viewModel.responseMessage.observe(this@EditProfileActivity) {
                                if (it != null) {
                                    it.getContentIfNotHandled()?.let { msg ->
                                        Toast.makeText(this@EditProfileActivity, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@EditProfileActivity,
                                getString(R.string.successfully_update_data),
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@EditProfileActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            finishAffinity()
                        }
                    }
                }
            }

            btnBack.setOnClickListener { onBackPressed() }
        }
    }


    private fun observeUserData() {
        viewModel.observeLoggedUserData()
        viewModel.loggedUserData.observe(this) { user ->
            if (user != null) {
                binding.apply {
                    edtFullName.setText(user.name)
                    edtStudentId.setText(user.npm)
                    edtInstagram.setText(user.instagram)
                    edtLinkedin.setText(user.linkedin)

                    if (user.photoUrl.isEmpty()) {
                        Glide.with(this@EditProfileActivity)
                            .load(R.drawable.ic_default_profile)
                            .into(ivUserPhoto)
                    } else {
                        Glide.with(this@EditProfileActivity)
                            .load(user.photoUrl)
                            .into(ivUserPhoto)
                    }
                }
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

    companion object {
        private const val TAG = "EditProfileActivity"
    }
}