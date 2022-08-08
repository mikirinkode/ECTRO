package id.ac.unila.ee.himatro.ectro.ui.event

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.databinding.ActivityCreateEventBinding
import id.ac.unila.ee.himatro.ectro.databinding.ActivitySettingsBinding

class CreateEventActivity : AppCompatActivity() {

    private val binding: ActivityCreateEventBinding by lazy {
        ActivityCreateEventBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            btnBack.setOnClickListener { onBackPressed() }
        }
    }
}