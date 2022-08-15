package id.ac.unila.ee.himatro.ectro.ui.event.participant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.databinding.ActivityParticipantListBinding
import id.ac.unila.ee.himatro.ectro.viewmodel.AttendanceViewModel

@AndroidEntryPoint
class ParticipantListActivity : AppCompatActivity() {

    private val binding: ActivityParticipantListBinding by lazy {
        ActivityParticipantListBinding.inflate(layoutInflater)
    }

    private val adapter: ParticipantAdapter by lazy {
        ParticipantAdapter()
    }

    private val viewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        binding.apply {
            rvAttendance.layoutManager = LinearLayoutManager(this@ParticipantListActivity)
            rvAttendance.adapter = adapter

            viewModel.observeAttendanceByEventId(eventId).observe(this@ParticipantListActivity){ list ->
                if (list.isNotEmpty()){
                    adapter.setData(list)

                    observeAttendees()
                    viewModel.getTotalAttendees(eventId ?: "").observe(this@ParticipantListActivity){
                        tvTotalParticipant.text = it.toString()
                    }
                } else {
                    // TODO: CREATE EMPTY MESSAGE
                }
            }

            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    private fun observeAttendees() {
        viewModel.totalKominfoAttendees.observe(this){
            binding.tvKominfoAttendees.text = it.toString()
        }
        viewModel.totalPhAttendees.observe(this){
            binding.tvPhAttendees.text = it.toString()
        }
        viewModel.totalPddAttendees.observe(this){
            binding.tvPddAttendees.text = it.toString()
        }
        viewModel.totalSoswirAttendees.observe(this){
            binding.tvSoswirAttendees.text = it.toString()
        }
        viewModel.totalKpoAttendees.observe(this){
            binding.tvKpoAttendees.text = it.toString()
        }
        viewModel.totalBangtekAttendees.observe(this){
            binding.tvBangtekAttendees.text = it.toString()
        }


    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }
}