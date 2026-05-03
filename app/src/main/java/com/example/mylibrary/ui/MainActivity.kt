package com.example.mylibrary.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.preference.PreferenceManager
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mylibrary.R
import com.example.mylibrary.data.repository.LibraryRepository
import com.example.mylibrary.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfig: AppBarConfiguration
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var libraryRepository: LibraryRepository
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
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

        lifecycleScope.launch {
            libraryRepository.claimLegacyItemsForAdmin()
        }

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                lifecycleScope.launch {
                    libraryRepository.claimLegacyItemsForAdmin()
                }
            }
        }
        auth.addAuthStateListener(authStateListener)

        binding.root.post {
            try {
                val navController = findNavController(R.id.nav_host_fragment)
                val graph = navController.navInflater.inflate(R.navigation.nav_graph).apply {
                    setStartDestination(if (auth.currentUser == null) {
                        R.id.loginFragment
                    } else {
                        R.id.libraryFragment
                    })
                }
                navController.graph = graph
                appBarConfig = AppBarConfiguration(topLevelDestinations, binding.drawerLayout)
                setupActionBarWithNavController(navController, appBarConfig)
                binding.navView.setupWithNavController(navController)
                binding.bottomNav.setupWithNavController(navController)

                navController.addOnDestinationChangedListener { _, destination, _ ->
                    val isTopLevel = destination.id in topLevelDestinations
                    binding.bottomNav.visibility = if (isTopLevel) View.VISIBLE else View.GONE
                    binding.toolbar.navigationIcon?.setTint(getColor(R.color.gray_toolbar_icon))
                }

                binding.toolbar.navigationIcon?.setTint(getColor(R.color.gray_toolbar_icon))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        auth.removeAuthStateListener(authStateListener)
        super.onDestroy()
    }
}
