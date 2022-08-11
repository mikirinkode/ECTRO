package id.ac.unila.ee.himatro.ectro.ui.member

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.EctroPreferences
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest
import id.ac.unila.ee.himatro.ectro.databinding.ActivityEditRoleBinding
import id.ac.unila.ee.himatro.ectro.utils.DateHelper
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_ROLE_REQUEST
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_RR_HANDLER_UID
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_RR_STATUS
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_RR_UPDATED_AT
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_DEPARTMENT
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_DIVISION
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_POSITION
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_REQUEST_STATUS
import id.ac.unila.ee.himatro.ectro.utils.FirestoreUtils.TABLE_USER_ROLE

class EditRoleActivity : AppCompatActivity() {

    private val binding: ActivityEditRoleBinding by lazy {
        ActivityEditRoleBinding.inflate(layoutInflater)
    }
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val fireStore: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private val firebaseUser: FirebaseUser? by lazy {
        auth.currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val entity = intent.getParcelableExtra<RoleRequest>(EXTRA_ENTITY)

            tvUserName.text = entity?.applicantName ?: ""
            tvUserNpm.text = entity?.applicantNpm ?: ""
            tvUserEmail.text = entity?.applicantEmail ?: ""

            rgDepartment.setOnCheckedChangeListener { _, buttonId ->
                when (buttonId) {
                    R.id.rb_kominfo -> {
                        layoutKominfoDivision.visibility = View.VISIBLE

                        layoutSoswirDivision.visibility = View.GONE
                        layoutBangtekDivision.visibility = View.GONE
                        layoutPpdDivision.visibility = View.GONE
                        layoutOtherPosition.visibility = View.GONE
                    }
                    R.id.rb_soswir -> {
                        layoutSoswirDivision.visibility = View.VISIBLE

                        layoutKominfoDivision.visibility = View.GONE
                        layoutBangtekDivision.visibility = View.GONE
                        layoutPpdDivision.visibility = View.GONE
                        layoutOtherPosition.visibility = View.GONE
                    }
                    R.id.rb_bangtek -> {
                        layoutBangtekDivision.visibility = View.VISIBLE

                        layoutKominfoDivision.visibility = View.GONE
                        layoutSoswirDivision.visibility = View.GONE
                        layoutPpdDivision.visibility = View.GONE
                        layoutOtherPosition.visibility = View.GONE
                    }
                    R.id.rb_ppd -> {
                        layoutPpdDivision.visibility = View.VISIBLE

                        layoutKominfoDivision.visibility = View.GONE
                        layoutSoswirDivision.visibility = View.GONE
                        layoutBangtekDivision.visibility = View.GONE
                        layoutOtherPosition.visibility = View.GONE
                    }
                    R.id.rb_kpo -> {
                        rbKadiv.visibility = View.GONE
                        layoutOtherPosition.visibility = View.VISIBLE

                        layoutKominfoDivision.visibility = View.GONE
                        layoutSoswirDivision.visibility = View.GONE
                        layoutBangtekDivision.visibility = View.GONE
                        layoutPpdDivision.visibility = View.GONE
                    }
                }
            }

            rgKominfoDivision.setOnCheckedChangeListener { _, _ ->
                layoutOtherPosition.visibility = View.VISIBLE
            }
            rgSoswirDivision.setOnCheckedChangeListener { _, _ ->
                layoutOtherPosition.visibility = View.VISIBLE
            }
            rgBangtekDivision.setOnCheckedChangeListener { _, _ ->
                layoutOtherPosition.visibility = View.VISIBLE
            }
            rgPpdDivision.setOnCheckedChangeListener { _, _ ->
                layoutOtherPosition.visibility = View.VISIBLE
            }

            btnUpdateRole.setOnClickListener {
                var isValid = true
                var department = ""
                var division = ""

                when (rgDepartment.checkedRadioButtonId) {
                    R.id.rb_kominfo -> {
                        department = getString(R.string.kominfo)
                        when (rgKominfoDivision.checkedRadioButtonId) {
                            R.id.rb_medin -> division = getString(R.string.medin)
                            R.id.rb_humas -> division = getString(R.string.humas)
                        }
                    }
                    R.id.rb_soswir -> {
                        department = getString(R.string.soswir)
                        when (rgSoswirDivision.checkedRadioButtonId) {
                            R.id.rb_sosial -> division = getString(R.string.sosial)
                            R.id.rb_kewirausahaan -> division = getString(R.string.kewirausahaan)
                        }
                    }
                    R.id.rb_bangtek -> {
                        department = getString(R.string.bangtek)
                        when (rgBangtekDivision.checkedRadioButtonId) {
                            R.id.rb_litbang -> division = getString(R.string.litbang)
                            R.id.rb_pengmas -> division = getString(R.string.pengmas)
                        }
                    }
                    R.id.rb_ppd -> {
                        department = getString(R.string.ppd)
                        when (rgKominfoDivision.checkedRadioButtonId) {
                            R.id.rb_pendidikan -> division = getString(R.string.pendidikan)
                            R.id.rb_kerohanian -> division = getString(R.string.kerohanian)
                            R.id.rb_minat_bakat -> division = getString(R.string.minat_bakat)
                        }
                    }
                    R.id.rb_kpo -> {
                        department = getString(R.string.kpo)
                    }
                    else -> department = ""
                }

                val position = when (rgOtherPosition.checkedRadioButtonId) {
                    R.id.rb_kadep -> getString(R.string.kadep)
                    R.id.rb_sekdep -> getString(R.string.sekdep)
                    R.id.rb_kadiv -> getString(R.string.kadiv)
                    R.id.rb_anggota -> getString(R.string.anggota)
                    else -> ""
                }

                if (department == "") {
                    isValid = false
                    Toast.makeText(
                        this@EditRoleActivity,
                        getString(R.string.please_choose_department),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (position == "") {
                    isValid = false
                    Toast.makeText(
                        this@EditRoleActivity,
                        getString(R.string.please_choose_position),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (isValid) {
                    val updateRequest = hashMapOf(
                        TABLE_RR_STATUS to EctroPreferences.COMPLETED_STATUS,
                        TABLE_RR_HANDLER_UID to firebaseUser?.uid,
                        TABLE_RR_UPDATED_AT to DateHelper.getCurrentDate()
                    )

                    val updateRole = hashMapOf(
                        TABLE_USER_REQUEST_STATUS to EctroPreferences.COMPLETED_STATUS,
                        TABLE_USER_ROLE to hashMapOf(
                            TABLE_USER_DEPARTMENT to department,
                            TABLE_USER_DIVISION to division,
                            TABLE_USER_POSITION to position,
                        )
                    )
                    binding.loadingIndicator.visibility = View.VISIBLE

                    // update user data in database
                    fireStore.collection(TABLE_USER)
                        .document(firebaseUser?.uid ?: "")
                        .set(updateRole, SetOptions.merge())
                        .addOnSuccessListener {

                            // update request document in database
                            fireStore.collection(TABLE_ROLE_REQUEST)
                                .document(entity?.requestId ?: "")
                                .set(updateRequest, SetOptions.merge())
                                .addOnSuccessListener {
                                    binding.loadingIndicator.visibility = View.GONE
                                    Toast.makeText(
                                        this@EditRoleActivity,
                                        getString(R.string.successfully_update_role),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    onBackPressed()
                                }
                                .addOnFailureListener {
                                    binding.loadingIndicator.visibility = View.GONE
                                    Toast.makeText(
                                        this@EditRoleActivity,
                                        it.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e(TAG, it.message.toString())
                                }
                        }
                }
            }

            // set Dropdown for department
//            val departmentList: Array<String> = resources.getStringArray(R.array.department_list)
//            val departmentAdapter: ArrayAdapter<String> =
//                ArrayAdapter(
//                    this@EditRoleActivity,
//                    android.R.layout.simple_list_item_1,
//                    departmentList
//                )
//            actDepartment.setAdapter(departmentAdapter)
//
//            // set Dropdown for division
//            val department = actDepartment.text.toString().trim()
//
//
//            if (department == departmentList[0]) {
//                tilDivision.visibility = View.GONE
//                tilPosition.visibility = View.VISIBLE
//
//                // dropdown for PH position
//                val phPositionList: Array<String> = resources.getStringArray(R.array.ph_position_list)
//                val phPositionAdapter: ArrayAdapter<String> =
//                    ArrayAdapter(
//                        this@EditRoleActivity,
//                        android.R.layout.simple_list_item_1,
//                        phPositionList
//                    )
//                actPosition.setAdapter(phPositionAdapter)
//
//            } else if (department == departmentList[5]) {
//                tilDivision.visibility = View.GONE
//                tilPosition.visibility = View.VISIBLE
//                // dropdown for KPO position
//                val positionList: Array<String> = resources.getStringArray(R.array.kpo_position_list)
//                val positionAdapter: ArrayAdapter<String> =
//                    ArrayAdapter(
//                        this@EditRoleActivity,
//                        android.R.layout.simple_list_item_1,
//                        positionList
//                    )
//                actPosition.setAdapter(positionAdapter)
//            } else {
//                tilDivision.visibility = View.VISIBLE
//                tilPosition.visibility = View.VISIBLE
//                val divisionList: Array<String> = when (department) {
//                    departmentList[1] -> {
//                        resources.getStringArray(R.array.ppd_division_list)
//                    }
//                    departmentList[2] -> {
//                        resources.getStringArray(R.array.soswir_division_list)
//                    }
//                    departmentList[3] -> {
//                        resources.getStringArray(R.array.bangtek_division_list)
//                    }
//                    departmentList[4] -> {
//                        resources.getStringArray(R.array.ppd_division_list)
//                    }
//                    else -> resources.getStringArray(R.array.empty)
//                }
//
//                // set dropdown for division
//                val divisionAdapter: ArrayAdapter<String> =
//                    ArrayAdapter(
//                        this@EditRoleActivity,
//                        android.R.layout.simple_list_item_1,
//                        divisionList
//                    )
//                actDivision.setAdapter(divisionAdapter)
//
//                // set Dropdown for position
//                val otherPositionList: Array<String> = resources.getStringArray(R.array.other_position_list)
//                val otherPositionAdapter: ArrayAdapter<String> =
//                    ArrayAdapter(
//                        this@EditRoleActivity,
//                        android.R.layout.simple_list_item_1,
//                        otherPositionList
//                    )
//                actPosition.setAdapter(otherPositionAdapter)
//            }


            btnBack.setOnClickListener { onBackPressed() }
        }
    }


    companion object {
        const val EXTRA_ENTITY = "extra_entity"
        private const val TAG = "EditRoleActivity"
    }
}