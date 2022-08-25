package id.ac.unila.ee.himatro.ectro.ui.member

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest
import id.ac.unila.ee.himatro.ectro.databinding.ActivityRoleRequestBinding
import id.ac.unila.ee.himatro.ectro.viewmodel.RoleRequestViewModel

@AndroidEntryPoint
class RoleRequestActivity : AppCompatActivity() {

    private val binding: ActivityRoleRequestBinding by lazy {
        ActivityRoleRequestBinding.inflate(layoutInflater)
    }

    private val viewModel: RoleRequestViewModel by viewModels()

    private val adapter: RoleRequestAdapter by lazy {
        RoleRequestAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeRoleRequest()

        binding.apply {
            rvRoleRequest.layoutManager = LinearLayoutManager(this@RoleRequestActivity)
            rvRoleRequest.adapter = adapter

            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    private fun observeRoleRequest() {
        viewModel.observeRoleRequestList()
        viewModel.roleRequestList.observe(this) { requestList ->
            if (requestList.isNotEmpty()) {
                val newList = ArrayList<RoleRequest>()
                requestList.forEach{ entity ->
                    if (entity.status != EctroPreferences.COMPLETED_STATUS) {
                        newList.add(entity)
                    }
                }
                adapter.setData(newList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
        observeRoleRequest()
    }

    companion object {
        private const val TAG = "RoleRequestActivity"
    }
}