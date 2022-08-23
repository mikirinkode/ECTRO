package id.ac.unila.ee.himatro.ectro.ui.event

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.databinding.ActivityEventListBinding
import id.ac.unila.ee.himatro.ectro.ui.main.home.EventAdapter
import id.ac.unila.ee.himatro.ectro.ui.main.home.HomeFragment
import id.ac.unila.ee.himatro.ectro.viewmodel.EventViewModel

@AndroidEntryPoint
class EventListActivity : AppCompatActivity() {

    private val binding: ActivityEventListBinding by lazy {
        ActivityEventListBinding.inflate(layoutInflater)
    }
    private val eventViewModel: EventViewModel by viewModels()

    private val adapter: EventAdapter by lazy {
        EventAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            // set recyclerview layout
            rvEvent.layoutManager = LinearLayoutManager(this@EventListActivity)
            rvEvent.adapter = adapter

            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
        observeIsLoading()
        observeEvent()
    }

    private fun observeEvent() {
        eventViewModel.getAllEventList()
            .observe(this) { list ->
                if (list.isEmpty()) {
                    binding.emptyMessage.visibility = View.VISIBLE
                } else {
                    binding.emptyMessage.visibility = View.GONE
                    adapter.setData(list)
                }
            }
    }

    private fun observeIsLoading() {
        eventViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingEventList.visibility = View.VISIBLE
            } else {
                binding.loadingEventList.visibility = View.GONE
            }
        }
    }
}