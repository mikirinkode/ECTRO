package id.unila.himatro.ectro.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import id.unila.himatro.ectro.R
import id.unila.himatro.ectro.databinding.ActivityMainBinding
import id.unila.himatro.ectro.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            Glide.with(this@MainActivity)
                .load(R.drawable.ic_default_profile)
                .into(ivUserPhoto)

            Glide.with(this@MainActivity)
                .load(R.drawable.sample_announcement_image)
                .into(announcement)

            if (tvUserName.text.isNotEmpty()) {
                tvGreeting.visibility = View.VISIBLE
                tvNotLoginYet.visibility = View.GONE
            } else {
                tvNotLoginYet.visibility = View.VISIBLE
            }

            ivUserPhoto.setOnClickListener {

            }

            tvNotLoginYet.setOnClickListener {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        }

    }
}