package id.ac.unila.ee.himatro.ectro.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.unila.himatro.ectro.R
import id.unila.himatro.ectro.databinding.ActivityMainBinding
import id.ac.unila.ee.himatro.ectro.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


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
}