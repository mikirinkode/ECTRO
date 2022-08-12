package id.ac.unila.ee.himatro.ectro.ui.member

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
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
import id.ac.unila.ee.himatro.ectro.viewmodel.RoleRequestViewModel
import javax.inject.Inject

@AndroidEntryPoint
class EditRoleActivity : AppCompatActivity() {

    private val binding: ActivityEditRoleBinding by lazy {
        ActivityEditRoleBinding.inflate(layoutInflater)
    }

    private val viewModel: RoleRequestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeIsLoading()

        binding.apply {
            val entity = intent.getParcelableExtra<RoleRequest>(EXTRA_ENTITY)

            tvUserName.text = entity?.applicantName ?: ""
            tvUserNpm.text = entity?.applicantNpm ?: ""
            tvUserEmail.text = entity?.applicantEmail ?: ""

            rgDepartment.setOnCheckedChangeListener { _, departmentId ->
                when (departmentId) {
                    R.id.rb_kominfo -> {
                        layoutOtherPosition.visibility = View.VISIBLE
                        rbKadiv.visibility = View.VISIBLE
                        rbKadiv.isEnabled = true
                        rgOtherPosition.clearCheck()

                        rgOtherPosition.setOnCheckedChangeListener { _, positionId ->
                            if ((positionId == R.id.rb_kadiv || positionId == R.id.rb_anggota) && departmentId == R.id.rb_kominfo ) {
                                layoutDivision.visibility = View.VISIBLE
                                layoutKominfoDivision.visibility = View.VISIBLE
                                layoutSoswirDivision.visibility = View.GONE
                                layoutPpdDivision.visibility = View.GONE
                                layoutBangtekDivision.visibility = View.GONE
                            } else {
                                layoutDivision.visibility = View.GONE
                                layoutKominfoDivision.visibility = View.GONE
                            }
                        }

                    }
                    R.id.rb_soswir -> {
                        layoutOtherPosition.visibility = View.VISIBLE
                        rbKadiv.visibility = View.VISIBLE
                        rbKadiv.isEnabled = true
                        rgOtherPosition.clearCheck()

                        rgOtherPosition.setOnCheckedChangeListener { _, positionId ->
                            if ((positionId == R.id.rb_kadiv || positionId == R.id.rb_anggota) && departmentId == R.id.rb_soswir) {
                                layoutDivision.visibility = View.VISIBLE
                                layoutKominfoDivision.visibility = View.GONE
                                layoutSoswirDivision.visibility = View.VISIBLE
                                layoutPpdDivision.visibility = View.GONE
                                layoutBangtekDivision.visibility = View.GONE
                            } else {
                                layoutSoswirDivision.visibility = View.GONE
                                layoutDivision.visibility = View.GONE
                            }
                        }
                    }
                    R.id.rb_bangtek -> {
                        layoutOtherPosition.visibility = View.VISIBLE
                        rbKadiv.visibility = View.VISIBLE
                        rbKadiv.isEnabled = true
                        rgOtherPosition.clearCheck()

                        rgOtherPosition.setOnCheckedChangeListener { _, positionId ->
                            if ((positionId == R.id.rb_kadiv || positionId == R.id.rb_anggota) && departmentId == R.id.rb_bangtek) {
                                layoutDivision.visibility = View.VISIBLE
                                layoutKominfoDivision.visibility = View.GONE
                                layoutSoswirDivision.visibility = View.GONE
                                layoutPpdDivision.visibility = View.GONE
                                layoutBangtekDivision.visibility = View.VISIBLE
                            } else {
                                layoutBangtekDivision.visibility = View.GONE
                                layoutDivision.visibility = View.GONE
                            }
                        }
                    }
                    R.id.rb_ppd -> {
                        layoutOtherPosition.visibility = View.VISIBLE
                        rbKadiv.visibility = View.VISIBLE
                        rbKadiv.isEnabled = true
                        rgOtherPosition.clearCheck()

                        rgOtherPosition.setOnCheckedChangeListener { _, positionId ->
                            if ((positionId == R.id.rb_kadiv || positionId == R.id.rb_anggota) && departmentId == R.id.rb_ppd) {
                                layoutDivision.visibility = View.VISIBLE
                                layoutKominfoDivision.visibility = View.GONE
                                layoutSoswirDivision.visibility = View.GONE
                                layoutPpdDivision.visibility = View.VISIBLE
                                layoutBangtekDivision.visibility = View.GONE
                            } else {
                                layoutPpdDivision.visibility = View.GONE
                                layoutDivision.visibility = View.GONE
                            }
                        }
                    }
                    R.id.rb_kpo -> {
                        layoutOtherPosition.visibility = View.VISIBLE
                        rbKadiv.isEnabled = false
                        rgOtherPosition.clearCheck()
                        rgKominfoDivision.clearCheck()
                        rgSoswirDivision.clearCheck()
                        rgPpdDivision.clearCheck()
                        rgBangtekDivision.clearCheck()

                        rgOtherPosition.setOnCheckedChangeListener { _, _ ->
                            layoutDivision.visibility = View.GONE
                            layoutKominfoDivision.visibility = View.GONE
                            layoutSoswirDivision.visibility = View.GONE
                            layoutPpdDivision.visibility = View.GONE
                            layoutBangtekDivision.visibility = View.GONE
                        }
                    }
                }
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
                    viewModel.updateRoleRequest(entity, department, division, position)

                    viewModel.isError.observe(this@EditRoleActivity){ isError ->
                        if (isError){
                            viewModel.responseMessage.observe(this@EditRoleActivity){
                                if (it != null) {
                                    it.getContentIfNotHandled()?.let { msg ->
                                        Toast.makeText(this@EditRoleActivity, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else if (!isError) {
                            Toast.makeText(
                                this@EditRoleActivity,
                                getString(R.string.successfully_update_role),
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        }
                    }
                }
            }
            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    private fun observeIsLoading() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading){
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.loadingIndicator.visibility = View.GONE
            }
        }
    }

    companion object {
        const val EXTRA_ENTITY = "extra_entity"
        private const val TAG = "EditRoleActivity"
    }
}