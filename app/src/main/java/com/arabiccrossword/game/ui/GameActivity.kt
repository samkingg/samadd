package com.arabiccrossword.game.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arabiccrossword.game.R
import com.arabiccrossword.game.databinding.ActivityGameBinding
import com.arabiccrossword.game.model.CrosswordPuzzle
import com.arabiccrossword.game.model.GameState
import com.arabiccrossword.game.ui.custom.CrosswordGridView
import com.arabiccrossword.game.viewmodel.GameViewModel
import com.arabiccrossword.game.viewmodel.GameViewModelFactory

class GameActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_NEW_GAME = "extra_new_game"
    }
    
    private lateinit var binding: ActivityGameBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var crosswordGridView: CrosswordGridView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
        setupObservers()
        setupClickListeners()
        
        val isNewGame = intent.getBooleanExtra(EXTRA_NEW_GAME, true)
        if (isNewGame) {
            gameViewModel.startNewGame()
        } else {
            gameViewModel.loadSavedGame()
        }
    }
    
    private fun setupViewModel() {
        val factory = GameViewModelFactory(application)
        gameViewModel = ViewModelProvider(this, factory)[GameViewModel::class.java]
    }
    
    private fun setupUI() {
        crosswordGridView = binding.crosswordGridView
        crosswordGridView.setOnCellClickListener { row, col ->
            gameViewModel.selectCell(row, col)
        }
        
        crosswordGridView.setOnTextChangedListener { row, col, text ->
            gameViewModel.updateCell(row, col, text)
        }
    }
    
    private fun setupObservers() {
        gameViewModel.gameState.observe(this) { gameState ->
            updateUI(gameState)
        }
        
        gameViewModel.puzzle.observe(this) { puzzle ->
            updateCrosswordGrid(puzzle)
        }
        
        gameViewModel.score.observe(this) { score ->
            binding.scoreTextView.text = score.toString()
        }
        
        gameViewModel.currentLevel.observe(this) { level ->
            binding.levelTextView.text = getString(R.string.level) + " $level"
        }
        
        gameViewModel.clues.observe(this) { clues ->
            binding.cluesTextView.text = clues.joinToString("\n")
        }
        
        gameViewModel.showHint.observe(this) { showHint ->
            if (showHint) {
                showHintDialog()
            }
        }
        
        gameViewModel.gameCompleted.observe(this) { completed ->
            if (completed) {
                showLevelCompletedDialog()
            }
        }
        
        gameViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            showExitConfirmationDialog()
        }
        
        binding.hintButton.setOnClickListener {
            gameViewModel.requestHint()
        }
        
        binding.checkButton.setOnClickListener {
            gameViewModel.checkCurrentWord()
        }
    }
    
    private fun updateUI(gameState: GameState) {
        when (gameState) {
            is GameState.Loading -> {
                binding.gridScrollView.visibility = View.GONE
                binding.controlsLayout.visibility = View.GONE
                // Show loading indicator
            }
            is GameState.Playing -> {
                binding.gridScrollView.visibility = View.VISIBLE
                binding.controlsLayout.visibility = View.VISIBLE
                // Hide loading indicator
            }
            is GameState.Paused -> {
                // Handle pause state
            }
            is GameState.GameOver -> {
                showGameOverDialog()
            }
        }
    }
    
    private fun updateCrosswordGrid(puzzle: CrosswordPuzzle) {
        crosswordGridView.setPuzzle(puzzle)
        crosswordGridView.invalidate()
    }
    
    private fun showHintDialog() {
        val hint = gameViewModel.getCurrentHint()
        AlertDialog.Builder(this)
            .setTitle("تلميح")
            .setMessage(hint)
            .setPositiveButton("حسناً") { _, _ ->
                gameViewModel.hintUsed()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showLevelCompletedDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_completed))
            .setPositiveButton(getString(R.string.next_level)) { _, _ ->
                gameViewModel.nextLevel()
            }
            .setNegativeButton(getString(R.string.back_to_menu)) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.game_over))
            .setMessage("النقاط النهائية: ${gameViewModel.score.value}")
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                gameViewModel.startNewGame()
            }
            .setNegativeButton(getString(R.string.back_to_menu)) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("تأكيد الخروج")
            .setMessage("هل تريد حفظ اللعبة والخروج؟")
            .setPositiveButton("نعم") { _, _ ->
                gameViewModel.saveGame()
                finish()
            }
            .setNegativeButton("لا") { _, _ ->
                finish()
            }
            .setNeutralButton("إلغاء") { _, _ ->
                // Do nothing, dialog will dismiss
            }
            .show()
    }
    
    override fun onBackPressed() {
        showExitConfirmationDialog()
    }
    
    override fun onPause() {
        super.onPause()
        gameViewModel.pauseGame()
    }
    
    override fun onResume() {
        super.onResume()
        gameViewModel.resumeGame()
    }
}