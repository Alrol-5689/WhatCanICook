package com.app.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observarViewModel()
        acciones()
    }

    private fun observarViewModel() {
        viewModel.registerResponse.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, "Usuario registrado", Snackbar.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun acciones() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.registerButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            viewModel.register(username, email, password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
