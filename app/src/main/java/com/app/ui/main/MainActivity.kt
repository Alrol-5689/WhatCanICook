package com.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.databinding.ActivityMainBinding
import com.app.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        acciones()
    }

    private fun acciones(){

        binding.logoutButton.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }

    }

}