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
    }
}