package id.ac.unila.ee.himatro.ectro.ui.member

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.model.Event
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest
import id.ac.unila.ee.himatro.ectro.databinding.ActivityEditRoleBinding
import id.ac.unila.ee.himatro.ectro.databinding.ActivityRoleRequestBinding
import id.ac.unila.ee.himatro.ectro.ui.main.home.HomeFragment
import javax.inject.Inject

@AndroidEntryPoint
class RoleRequestActivity : AppCompatActivity() {

    private val binding: ActivityRoleRequestBinding by lazy {
        ActivityRoleRequestBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var fireStore: FirebaseFirestore

    private val adapter: RoleRequestAdapter by lazy {
        RoleRequestAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            rvRoleRequest.layoutManager = LinearLayoutManager(this@RoleRequestActivity)
            rvRoleRequest.adapter = adapter

            btnBack.setOnClickListener { onBackPressed() }
        }

        fireStore.collection("roleRequest")
            .get()
            .addOnSuccessListener { documentList ->
                val requestList: ArrayList<RoleRequest> = ArrayList()
                for (document in documentList) {
                    if (document != null) {
                        requestList.add(
                            document.toObject()
                        )
                    }
                }

                if (requestList.isNotEmpty()) {
                    adapter.setData(requestList)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    companion object{
        private const val TAG = "RoleRequestActivity"
    }
}