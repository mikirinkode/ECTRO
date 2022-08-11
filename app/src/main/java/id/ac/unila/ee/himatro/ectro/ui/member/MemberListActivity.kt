package id.ac.unila.ee.himatro.ectro.ui.member

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.databinding.ActivityMemberListBinding
import id.ac.unila.ee.himatro.ectro.databinding.ActivityRoleRequestBinding
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils.DEV_TEAM
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils.KADEP
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils.KADIV
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils.PH
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils.SEKDEP
import javax.inject.Inject

@AndroidEntryPoint
class MemberListActivity : AppCompatActivity() {

    private val binding: ActivityMemberListBinding by lazy {
        ActivityMemberListBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var fireStore: FirebaseFirestore

    private val firebaseUser: FirebaseUser? by lazy {
        auth.currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            fireStore.collection("users")
                .document(firebaseUser?.uid ?: "")
                .get()
                .addOnSuccessListener {
                    val user: User? = it.toObject<User>()
                    if (user != null && (user.role.department == PH || user.role.position == KADIV || user.role.position == KADEP || user.role.position == SEKDEP || user.role.position == DEV_TEAM)) {
                        cardRoleReq.visibility = View.VISIBLE
                    }
                }
            fireStore.collection("roleRequest")
                .get()
                .addOnSuccessListener { documentList ->
                    tvNumRoleReq.text = "${documentList.size()} Permintaan Validasi Role"
                }

            cardRoleReq.setOnClickListener {
                startActivity(Intent(this@MemberListActivity, RoleRequestActivity::class.java))
            }

            btnBack.setOnClickListener { onBackPressed() }
        }
    }
}