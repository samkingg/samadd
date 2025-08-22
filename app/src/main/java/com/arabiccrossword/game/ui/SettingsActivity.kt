package com.arabiccrossword.game.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.arabiccrossword.game.R
import com.arabiccrossword.game.model.QuestionDifficulty
import com.arabiccrossword.game.utils.GamePreferences

class SettingsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
            
        supportActionBar?.apply {
            title = getString(R.string.settings)
            setDisplayHomeAsUpEnabled(true)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    
    class SettingsFragment : PreferenceFragmentCompat() {
        
        private lateinit var gamePreferences: GamePreferences
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            
            gamePreferences = GamePreferences(requireContext())
            setupPreferences()
        }
        
        private fun setupPreferences() {
            // Difficulty preference
            findPreference<ListPreference>("difficulty")?.apply {
                value = gamePreferences.getDifficulty().name
                setOnPreferenceChangeListener { _, newValue ->
                    val difficulty = QuestionDifficulty.valueOf(newValue.toString())
                    gamePreferences.setDifficulty(difficulty)
                    true
                }
            }
            
            // Sound preference
            findPreference<SwitchPreferenceCompat>("sound_effects")?.apply {
                isChecked = gamePreferences.isSoundEnabled()
                setOnPreferenceChangeListener { _, newValue ->
                    gamePreferences.setSoundEnabled(newValue as Boolean)
                    true
                }
            }
            
            // Vibration preference
            findPreference<SwitchPreferenceCompat>("vibration")?.apply {
                isChecked = gamePreferences.isVibrationEnabled()
                setOnPreferenceChangeListener { _, newValue ->
                    gamePreferences.setVibrationEnabled(newValue as Boolean)
                    true
                }
            }
            
            // Dark mode preference
            findPreference<SwitchPreferenceCompat>("dark_mode")?.apply {
                isChecked = gamePreferences.isDarkModeEnabled()
                setOnPreferenceChangeListener { _, newValue ->
                    gamePreferences.setDarkModeEnabled(newValue as Boolean)
                    // Note: In a real app, you'd need to restart the activity or recreate the theme
                    Toast.makeText(context, "يجب إعادة تشغيل التطبيق لتطبيق التغييرات", Toast.LENGTH_LONG).show()
                    true
                }
            }
            
            // Arabic font preference
            findPreference<SwitchPreferenceCompat>("arabic_font")?.apply {
                isChecked = gamePreferences.isArabicFontEnabled()
                setOnPreferenceChangeListener { _, newValue ->
                    gamePreferences.setArabicFontEnabled(newValue as Boolean)
                    true
                }
            }
            
            // Reset data preference
            findPreference<Preference>("reset_data")?.setOnPreferenceClickListener {
                showResetDataDialog()
                true
            }
            
            // Export data preference
            findPreference<Preference>("export_data")?.setOnPreferenceClickListener {
                exportGameData()
                true
            }
            
            // Import data preference
            findPreference<Preference>("import_data")?.setOnPreferenceClickListener {
                importGameData()
                true
            }
        }
        
        private fun showResetDataDialog() {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("إعادة تعيين البيانات")
                .setMessage("هل أنت متأكد من أنك تريد حذف جميع البيانات؟ لا يمكن التراجع عن هذا الإجراء.")
                .setPositiveButton("نعم") { _, _ ->
                    gamePreferences.resetAllData()
                    Toast.makeText(context, "تم حذف جميع البيانات", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("لا", null)
                .show()
        }
        
        private fun exportGameData() {
            val data = gamePreferences.exportData()
            // In a real app, you'd save this to a file or share it
            Toast.makeText(context, "تم تصدير البيانات", Toast.LENGTH_SHORT).show()
        }
        
        private fun importGameData() {
            // In a real app, you'd read from a file
            Toast.makeText(context, "يرجى اختيار ملف البيانات", Toast.LENGTH_SHORT).show()
        }
    }
}