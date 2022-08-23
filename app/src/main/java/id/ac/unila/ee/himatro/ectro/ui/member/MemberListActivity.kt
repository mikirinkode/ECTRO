package id.ac.unila.ee.himatro.ectro.ui.member

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest
import id.ac.unila.ee.himatro.ectro.databinding.ActivityMemberListBinding
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils.DEV_TEAM
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils.KADEP
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils.KADIV
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils.PH
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils.SEKDEP
import id.ac.unila.ee.himatro.ectro.viewmodel.RoleRequestViewModel
import id.ac.unila.ee.himatro.ectro.viewmodel.UserViewModel

@AndroidEntryPoint
class MemberListActivity : AppCompatActivity() {

    private val binding: ActivityMemberListBinding by lazy {
        ActivityMemberListBinding.inflate(layoutInflater)
    }

    private val roleRequestViewModel: RoleRequestViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private val adapter: MemberAdapter by lazy {
        MemberAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeIsLoading()
        observeRoleRequest()
        observeUserList()

        binding.apply {
            rvUser.layoutManager = LinearLayoutManager(this@MemberListActivity)
            rvUser.adapter = adapter

            userViewModel.observeLoggedUserData()
            userViewModel.loggedUserData.observe(this@MemberListActivity) { user ->
                if (user != null && (user.role.department == PH || user.role.position == KADIV || user.role.position == KADEP || user.role.position == SEKDEP || user.role.position == DEV_TEAM)) {
                    cardRoleReq.visibility = View.VISIBLE

                    cardRoleReq.setOnClickListener {
                        startActivity(
                            Intent(
                                this@MemberListActivity,
                                RoleRequestActivity::class.java
                            )
                        )
                    }
                }
            }

            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    private fun observeUserList() {
        userViewModel.getAllUser().observe(this){ userList ->
            binding.apply {
                adapter.setData(userList)
            }
        }
    }

    private fun observeRoleRequest() {
        roleRequestViewModel.observeRoleRequestList()
        roleRequestViewModel.roleRequestList.observe(this) {
            if (it.isNotEmpty()) {
                roleRequestViewModel.roleRequestList.observe(this) { requestList ->
                    if (requestList.isNotEmpty()) {
                        val newList = ArrayList<RoleRequest>()
                        requestList.forEach { entity ->
                            if (entity.status != EctroPreferences.COMPLETED_STATUS) {
                                newList.add(entity)
                            }
                        }
                        binding.tvNumRoleReq.text = "${newList.size} Permintaan Validasi Role"
                    }
                }
            } else {
                binding.cardRoleReq.visibility = View.GONE
            }
        }
    }

    // TODO: CREATE SHIMMER LOADING FOR USER LIST
    private fun observeIsLoading() {
        userViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading){
                binding.loadingUserList.visibility = View.VISIBLE
            } else {
                binding.loadingUserList.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        observeRoleRequest()
    }
}