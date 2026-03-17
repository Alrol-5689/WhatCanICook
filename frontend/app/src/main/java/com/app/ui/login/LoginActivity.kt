package com.app.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.databinding.ActivityLoginBinding
import com.app.ui.main.MainActivity
import com.app.ui.register.RegisterActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observarViewModel()
        acciones()
    }

    private fun observarViewModel() {
        viewModel.loginResponse.observe(this) { authResponse ->
            // observe -> se espera a que LoginViewModel -> _loginResponse.value = authResponse
            // MutableLiveData -> "CAJA OBSERVABLE"
            Snackbar.make(binding.root, "Login correcto", Snackbar.LENGTH_SHORT).show()
            val user = authResponse.user
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("userId", user?.id)
            intent.putExtra("username", user?.username)
            intent.putExtra("email", user?.email)
            startActivity(intent)
            finish()
        }
        viewModel.errorMessage.observe(this) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun acciones() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            viewModel.login(email, password)
        }

        binding.createAccountButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}