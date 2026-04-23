package com.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.app.R
import com.app.databinding.ActivityMainBinding
import com.app.ui.recipes.PantryActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isMainScreen = destination.id == R.id.feedFragment ||
                               destination.id == R.id.favoritesFragment ||
                               destination.id == R.id.profileFragment

            binding.bottomNav.visibility = if (isMainScreen) View.VISIBLE else View.GONE

            if (isMainScreen) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        // --- Navegación del Drawer Menu ---

        binding.searchRecipesButton.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            // Abrimos la nueva pantalla de Despensa
            val intent = Intent(this, PantryActivity::class.java)
            startActivity(intent)
        }

        binding.logoutButton.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            navController.navigate(R.id.loginFragment) 
        }
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
    }
}