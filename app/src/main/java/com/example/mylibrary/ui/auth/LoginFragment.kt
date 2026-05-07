package com.example.mylibrary.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.mylibrary.R
import com.example.mylibrary.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvStatus.text = if (user != null) {
                getString(R.string.auth_logged_in, user.email ?: "")
            } else {
                getString(R.string.auth_logged_out)
            }

            if (user != null && findNavController().currentDestination?.id == R.id.loginFragment) {
                findNavController().navigate(
                    R.id.libraryFragment,
                    null,
                    navOptions {
                        popUpTo(R.id.loginFragment) { inclusive = true }
                    }
                )
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            getLoginData()?.let { (email, password) ->
                viewModel.register(email, password)
            }
        }

        binding.btnLogin.setOnClickListener {
            getLoginData()?.let { (email, password) ->
                viewModel.login(email, password)
            }
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun getLoginData(): Pair<String, String>? {
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty().trim()

        binding.tilEmail.error = null
        binding.tilPassword.error = null

        var valid = true
        if (email.isBlank()) {
            binding.tilEmail.error = getString(R.string.auth_error_email)
            valid = false
        }
        if (password.isBlank()) {
            binding.tilPassword.error = getString(R.string.auth_error_password)
            valid = false
        }

        return if (valid) email to password else null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
