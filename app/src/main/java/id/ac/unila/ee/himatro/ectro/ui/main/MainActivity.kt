package id.ac.unila.ee.himatro.ectro.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.databinding.ActivityMainBinding
import id.ac.unila.ee.himatro.ectro.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
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

        // save user info to preferences
        saveUserDataToPreference()

        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        binding.apply {
            val bottomNavView: BottomNavigationView = bottomNavView
            bottomNavView.background = null
            bottomNavView.menu.getItem(1).isEnabled = false


            bottomNavView.setupWithNavController(navController)

            fabScan.setOnClickListener {
                Toast.makeText(this@MainActivity, "Scan Button", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserDataToPreference() {
        val loggedUser = auth.currentUser

        if (loggedUser != null) {
            val userInDB: DocumentReference = fireStore.collection("users").document(loggedUser.uid)

            userInDB.get()
                .addOnSuccessListener { document ->
                    val user: User? = document.toObject<User>()

                    if (user != null) {
                        preferences.startSession(user)


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

    companion object {
        private const val TAG = "MainActivity"
    }
}