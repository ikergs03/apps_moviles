package com.example.mylibrary.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceFragmentCompat
import com.example.mylibrary.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(findViewById(R.id.toolbar_settings))

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    companion object {
        const val PREF_SHOW_COVERS_KEY = "pref_show_covers"
        const val PREF_DEFAULT_FILTER_KEY = "pref_default_filter"
        const val PREF_DEFAULT_FILTER_DEFAULT = "all"
        const val PREF_CONFIRM_DELETE_KEY = "pref_confirm_delete"
        const val PREF_CONFIRM_DELETE_DEFAULT = true
        const val PREF_DEFAULT_TAGS_KEY = "pref_default_tags"
        const val PREF_DEFAULT_TAGS_DEFAULT = ""
        const val LOGGED_IN_KEY = "logged_in_key"

        fun isShowCoversEnabled(context: Context): Boolean {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(PREF_SHOW_COVERS_KEY, true)
        }

        fun getDefaultFilter(context: Context): String {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(PREF_DEFAULT_FILTER_KEY, PREF_DEFAULT_FILTER_DEFAULT)
                ?: PREF_DEFAULT_FILTER_DEFAULT
        }

        fun isConfirmDeleteEnabled(context: Context): Boolean {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(PREF_CONFIRM_DELETE_KEY, PREF_CONFIRM_DELETE_DEFAULT)
        }

        fun getDefaultTags(context: Context): String {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(PREF_DEFAULT_TAGS_KEY, PREF_DEFAULT_TAGS_DEFAULT)
                ?: PREF_DEFAULT_TAGS_DEFAULT
        }

        fun setLoggedIn(context: Context, loggedIn: Boolean) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putBoolean(LOGGED_IN_KEY, loggedIn)
            editor.apply()
        }

        fun isLoggedIn(context: Context): Boolean {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(LOGGED_IN_KEY, false)
        }
    }
}
