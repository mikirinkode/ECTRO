package id.ac.unila.ee.himatro.ectro.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.databinding.FragmentLoginBinding
import id.ac.unila.ee.himatro.ectro.ui.auth.RegisterActivity
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.viewmodel.AuthViewModel


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() =  _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeIsLoading()

        binding.apply {
            btnLogin.setOnClickListener {
                var isValid = true

                val email = edtLoginEmail.text.toString().trim()
                val password = edtLoginPassword.text.toString().trim()

                if (email.isEmpty()) {
                    edtLoginEmail.error = getString(R.string.empty_email)
                    isValid = false
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtLoginEmail.error = getString(R.string.invalid_email)
                    isValid = false
                }

                if (password.isEmpty()) {
                    edtLoginPassword.error = getString(R.string.empty_password)
                    isValid = false
                }

                if (isValid) {
                    viewModel.loginUser(email, password)
                    viewModel.isError.observe(viewLifecycleOwner) { isError ->
                        if (isError){
                            viewModel.responseMessage.observe(viewLifecycleOwner){
                                if (it != null) {
                                    it.getContentIfNotHandled()?.let { msg ->
                                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            // if not error, then
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            activity?.finishAffinity()
                        }
                    }
                }
            }

            btnRegisterNow.setOnClickListener {
                startActivity(Intent(requireContext(), RegisterActivity::class.java))
                activity?.finish()
            }

            btnForgotPassword.setOnClickListener {
                Navigation.findNavController(view).navigate(R.id.login_to_reset_password)

            }
        }
    }

    private fun observeIsLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading){
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.loadingIndicator.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}