package id.ac.unila.ee.himatro.ectro.ui.member

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.model.RoleRequest
import id.ac.unila.ee.himatro.ectro.databinding.ActivityEditRoleBinding
import id.ac.unila.ee.himatro.ectro.utils.HimatroUtils
import id.ac.unila.ee.himatro.ectro.viewmodel.RoleRequestViewModel

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
                        department = HimatroUtils.KOMINFO
                        when (rgKominfoDivision.checkedRadioButtonId) {
                            R.id.rb_medin -> division = HimatroUtils.MEDIN
                            R.id.rb_humas -> division = HimatroUtils.HUMAS
                        }
                    }
                    R.id.rb_soswir -> {
                        department = HimatroUtils.SOSWIR
                        when (rgSoswirDivision.checkedRadioButtonId) {
                            R.id.rb_sosial -> division = HimatroUtils.SOSIAL
                            R.id.rb_kewirausahaan -> division = HimatroUtils.KEWIRAUSAHAAN
                        }
                    }
                    R.id.rb_bangtek -> {
                        department = HimatroUtils.BANGTEK
                        when (rgBangtekDivision.checkedRadioButtonId) {
                            R.id.rb_litbang -> division = HimatroUtils.LITBANG
                            R.id.rb_pengmas -> division = HimatroUtils.PENGMAS
                        }
                    }
                    R.id.rb_ppd -> {
                        department = HimatroUtils.PPD
                        when (rgKominfoDivision.checkedRadioButtonId) {
                            R.id.rb_pendidikan -> division = HimatroUtils.PENDIDIKAN
                            R.id.rb_kerohanian -> division = HimatroUtils.KEROHANIAN
                            R.id.rb_minat_bakat -> division = HimatroUtils.MINAT_BAKAT
                        }
                    }
                    R.id.rb_kpo -> {
                        department = HimatroUtils.KPO
                    }
                    else -> department = ""
                }

                val position = when (rgOtherPosition.checkedRadioButtonId) {
                    R.id.rb_kadep -> HimatroUtils.KADEP
                    R.id.rb_sekdep -> HimatroUtils.SEKDEP
                    R.id.rb_kadiv -> HimatroUtils.KADIV
                    R.id.rb_anggota -> HimatroUtils.ANGGOTA
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