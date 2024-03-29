package id.ac.unila.ee.himatro.ectro.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.DARK_MODE_PREF
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_EMAIL
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences.Companion.USER_NAME
import id.ac.unila.ee.himatro.ectro.databinding.ActivitySettingsBinding
import id.ac.unila.ee.himatro.ectro.ui.auth.LoginActivity
import id.ac.unila.ee.himatro.ectro.ui.profile.EditProfileActivity
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val binding: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var preferences: EctroPreferences

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val userEmail = preferences.getValues(USER_EMAIL)
        val userName = preferences.getValues(USER_NAME)
        val loggedUser = auth.currentUser

        binding.apply {

            btnEditProfile.setOnClickListener{
                startActivity(Intent(this@SettingsActivity, EditProfileActivity::class.java))
            }

            val isDark = preferences.getBooleanValues(DARK_MODE_PREF)
            switchDarkMode.isChecked = isDark == true
            switchDarkMode.setOnCheckedChangeListener { _, checked ->
                preferences.setValues(DARK_MODE_PREF, checked)
                if (checked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

            tvUserEmail.text = userEmail
            tvUserName.text = userName
            if (loggedUser != null){
                if (!loggedUser.isEmailVerified){
                    tvEmailStatus.text = getString(R.string.email_not_verified)
                    btnVerifyEmail.visibility = View.GONE
                } else {
                    tvEmailStatus.text = getString(R.string.email_is_verified)
                    btnVerifyEmail.visibility = View.GONE
                }
            }

            btnChangeLanguage.setOnClickListener {
                Toast.makeText(this@SettingsActivity, "Under Development", Toast.LENGTH_SHORT)
                    .show()
            }

            btnVerifyEmail.setOnClickListener {
                if (loggedUser != null) {
                    loggedUser.sendEmailVerification()
                    Toast.makeText(this@SettingsActivity, getString(R.string.verification_email_sent), Toast.LENGTH_LONG).show()
                }
            }

            btnChangePassword.setOnClickListener {
                Toast.makeText(this@SettingsActivity, "Under Development", Toast.LENGTH_SHORT)
                    .show()
            }
            btnBack.setOnClickListener { onBackPressed() }

            btnLogout.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(this@SettingsActivity)
                builder.setTitle(getString(R.string.logout))
                builder.setMessage(getString(R.string.are_you_sure_want_to_logout))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.sure)) { _, _ ->
                        // clear the user data
                        FirebaseAuth.getInstance().signOut()
                        preferences.endSession()
                        startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                    .setNegativeButton(getString(R.string.no)) { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }

                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
            }
        }
    }
}