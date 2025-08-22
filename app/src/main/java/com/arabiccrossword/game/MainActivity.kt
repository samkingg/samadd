package com.arabiccrossword.game

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arabiccrossword.game.databinding.ActivityMainBinding
import com.arabiccrossword.game.ui.GameActivity
import com.arabiccrossword.game.ui.SettingsActivity
import com.arabiccrossword.game.utils.GamePreferences

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var gamePreferences: GamePreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        gamePreferences = GamePreferences(this)
        setupUI()
        setupClickListeners()
    }
    
    private fun setupUI() {
        // Check if there's a saved game to continue
        val hasSavedGame = gamePreferences.hasSavedGame()
        binding.continueGameButton.isEnabled = hasSavedGame
        binding.continueGameButton.alpha = if (hasSavedGame) 1.0f else 0.5f
    }
    
    private fun setupClickListeners() {
        binding.startGameButton.setOnClickListener {
            startNewGame()
        }
        
        binding.continueGameButton.setOnClickListener {
            if (gamePreferences.hasSavedGame()) {
                continueSavedGame()
            } else {
                Toast.makeText(this, "لا توجد لعبة محفوظة", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.settingsButton.setOnClickListener {
            openSettings()
        }
        
        binding.exitButton.setOnClickListener {
            finish()
        }
    }
    
    private fun startNewGame() {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra(GameActivity.EXTRA_NEW_GAME, true)
        }
        startActivity(intent)
    }
    
    private fun continueSavedGame() {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra(GameActivity.EXTRA_NEW_GAME, false)
        }
        startActivity(intent)
    }
    
    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
    
    override fun onResume() {
        super.onResume()
        setupUI() // Refresh UI when returning from other activities
    }
}