package com.app.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.R
import com.app.databinding.ActivityLoginBinding
import com.app.ui.main.MainActivity
import com.app.utils.SessionManager
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelObserver()
        actions()
    }

    private fun viewModelObserver() {
        viewModel.loginResponse.observe(viewLifecycleOwner) { authResponse ->
            if (authResponse?.success == true) {
                Snackbar.make(binding.root, "Login correcto", Snackbar.LENGTH_SHORT).show()
                val user = authResponse.user
                if (user != null) {
                    SessionManager.login(user.id, user.username, user.email)
                }
                (requireActivity() as? MainActivity)?.refreshDrawerHeader()
                findNavController().navigate(R.id.action_login_to_feed)
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun actions() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            viewModel.login(email, password)
        }

        binding.createAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
