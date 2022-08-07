package id.ac.unila.ee.himatro.ectro.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private val binding : ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.apply {
            Glide.with(this@EditProfileActivity)
                .load(R.drawable.ic_default_profile)
                .into(ivUserPhoto)


            btnBack.setOnClickListener { onBackPressed() }
        }
    }
}