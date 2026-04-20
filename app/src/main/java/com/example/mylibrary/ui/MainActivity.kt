package com.example.mylibrary.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mylibrary.R
import com.example.mylibrary.databinding.ActivityMainBinding
import com.example.mylibrary.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfig: AppBarConfiguration
    private val topLevelDestinations = setOf(
        R.id.libraryFragment,
        R.id.addEditFragment,
        R.id.statisticsFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        SettingsActivity.setLoggedIn(this, true)

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfig = AppBarConfiguration(topLevelDestinations, binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfig)
        binding.navView.setupWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTopLevel = destination.id in topLevelDestinations
            binding.bottomNav.visibility = if (isTopLevel) View.VISIBLE else View.GONE
            binding.toolbar.navigationIcon?.setTint(getColor(R.color.gray_toolbar_icon))
        }

        binding.toolbar.post {
            binding.toolbar.navigationIcon?.setTint(getColor(R.color.gray_toolbar_icon))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }
}
