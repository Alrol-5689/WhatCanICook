package com.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.databinding.ActivityMainBinding
import com.app.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var username: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        username = intent.getStringExtra("username")
        email = intent.getStringExtra("email")
        acciones()
    }

    private fun acciones(){

        binding.textUsername.text = "Eres: ${username ?: "desconocido"}"
        binding.textEmail.text = "Tu correo es: ${email ?: "desconocido"}"

        binding.logoutButton.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }

    }

}