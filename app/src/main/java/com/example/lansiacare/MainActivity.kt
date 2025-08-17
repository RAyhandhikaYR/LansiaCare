package com.example.lansiacare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.lansiacare.utils.UserPreferences
import android.view.View
import com.example.lansiacare.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityMainBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)

        setupNavigation()

        // Check if user is logged in
        if (!userPreferences.isLoggedIn()) {
            navigateToLogin()
        }
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Setup bottom navigation dengan fragment yang akan kita buat
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_medication,
                R.id.navigation_emergency,
                R.id.navigation_medical_notes
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun navigateToLogin() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.loginFragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    internal fun showBottomNavigation() {
        binding.navView.visibility = View.VISIBLE
    }
}