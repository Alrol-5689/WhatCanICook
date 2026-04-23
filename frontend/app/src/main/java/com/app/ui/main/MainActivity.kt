package com.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.app.R
import com.app.databinding.ActivityMainBinding
import com.app.ui.recipes.PantryActivity
import com.app.utils.SessionManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        refreshDrawerHeader()

        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isAuthScreen =
                destination.id == R.id.loginFragment || destination.id == R.id.registerFragment

            val isMainScreen =
                destination.id == R.id.feedFragment ||
                    destination.id == R.id.favoritesFragment ||
                    destination.id == R.id.profileFragment

            binding.bottomNav.visibility = if (isMainScreen) View.VISIBLE else View.GONE
            binding.drawerLayout.setDrawerLockMode(
                if (isAuthScreen) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED
            )
        }

        // --- Navegación del Drawer Menu ---

        binding.searchRecipesButton.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            val intent = Intent(this, PantryActivity::class.java)
            startActivity(intent)
        }

        binding.myRecipesButton.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            navController.navigate(R.id.myRecipesFragment)
        }

        binding.favoritesButton.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            navController.navigate(R.id.favoritesFragment)
        }

        binding.friendsButton.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            navController.navigate(R.id.friendsFragment)
        }

        binding.uploadRecipeButton.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            navController.navigate(R.id.uploadRecipeFragment)
        }

        binding.logoutButton.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            SessionManager.logout()
            navController.navigate(
                R.id.loginFragment,
                null,
                navOptions {
                    popUpTo(R.id.nav_graph) { inclusive = true }
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        refreshDrawerHeader()
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
    }

    private fun refreshDrawerHeader() {
        binding.drawerUsername.text = SessionManager.username ?: getString(R.string.unknown_user)
        binding.drawerEmail.text = SessionManager.email ?: ""
    }
}
