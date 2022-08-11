package id.ac.unila.ee.himatro.ectro.ui.member

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest
import id.ac.unila.ee.himatro.ectro.databinding.ActivityRoleRequestBinding
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_ROLE_REQUEST
import id.ac.unila.ee.himatro.ectro.viewmodel.RoleRequestViewModel
import javax.inject.Inject

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