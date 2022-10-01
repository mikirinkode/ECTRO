package id.ac.unila.ee.himatro.ectro.ui.auth.login

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.databinding.DialogResetLinkSentBinding
import id.ac.unila.ee.himatro.ectro.databinding.FragmentProfileBinding
import id.ac.unila.ee.himatro.ectro.databinding.FragmentResetPasswordBinding
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var auth: FirebaseAuth

    private val dialogBinding: DialogResetLinkSentBinding by lazy {
        DialogResetLinkSentBinding.inflate(layoutInflater)
    }

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set success dialog
        dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(MATCH_PARENT, WRAP_CONTENT)

        dialogBinding.apply {
            btnLogin.setOnClickListener {
                activity?.onBackPressed()
                dialog.dismiss()
            }
        }

        binding.apply {

            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }

            btnSendReset.setOnClickListener {
                val email = edtResetEmail.text.toString().trim()

                if (email.isEmpty()){
                    edtResetEmail.error = getString(R.string.empty_email)
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtResetEmail.error = getString(R.string.invalid_email)
                } else {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "Email berhasil dikirim", Toast.LENGTH_SHORT).show()
                                btnSendReset.isEnabled = false

                                // open the success dialog
                                dialog.show()
                            }
                        }
                        .addOnFailureListener {
                            btnSendReset.isEnabled = true
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                            Log.e(TAG, it.message.toString())
                        }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "ResetPasswordFragment"
    }
}