package com.arabiccrossword.game.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arabiccrossword.game.data.repository.CrosswordRepository
import com.arabiccrossword.game.data.repository.AIQuestionRepository
import com.arabiccrossword.game.model.*
import com.arabiccrossword.game.utils.GamePreferences
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class GameViewModel(application: Application) : AndroidViewModel(application) {
    
    private val crosswordRepository = CrosswordRepository(application)
    private val aiQuestionRepository = AIQuestionRepository()
    private val gamePreferences = GamePreferences(application)
    
    // LiveData for UI updates
    private val _gameState = MutableLiveData<GameState>(GameState.Loading)
    val gameState: LiveData<GameState> = _gameState
    
    private val _puzzle = MutableLiveData<CrosswordPuzzle>()
    val puzzle: LiveData<CrosswordPuzzle> = _puzzle
    
    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score
    
    private val _currentLevel = MutableLiveData(1)
    val currentLevel: LiveData<Int> = _currentLevel
    
    private val _clues = MutableLiveData<List<String>>()
    val clues: LiveData<List<String>> = _clues
    
    private val _showHint = MutableLiveData<Boolean>()
    val showHint: LiveData<Boolean> = _showHint
    
    private val _gameCompleted = MutableLiveData<Boolean>()
    val gameCompleted: LiveData<Boolean> = _gameCompleted
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Game state variables
    private var currentPuzzle: CrosswordPuzzle? = null
    private var selectedCell: Pair<Int, Int>? = null
    private var hintsRemaining = 3
    private var currentWord = ""
    
    fun startNewGame() {
        viewModelScope.launch {
            try {
                _gameState.value = GameState.Loading
                
                // Get AI-generated questions for the current level
                val aiQuestions = aiQuestionRepository.generateQuestions(
                    level = _currentLevel.value ?: 1,
                    difficulty = gamePreferences.getDifficulty()
                )
                
                // Create crossword puzzle from AI questions
                val puzzle = crosswordRepository.createPuzzleFromQuestions(aiQuestions)
                currentPuzzle = puzzle
                
                _puzzle.value = puzzle
                _clues.value = puzzle.clues
                _score.value = 0
                hintsRemaining = 3
                
                _gameState.value = GameState.Playing
                _error.value = null
                
            } catch (e: Exception) {
                _error.value = "خطأ في بدء اللعبة: ${e.message}"
                _gameState.value = GameState.GameOver
            }
        }
    }
    
    fun loadSavedGame() {
        viewModelScope.launch {
            try {
                _gameState.value = GameState.Loading
                
                val savedGame = gamePreferences.getSavedGame()
                if (savedGame != null) {
                    currentPuzzle = savedGame.puzzle
                    _puzzle.value = savedGame.puzzle
                    _clues.value = savedGame.puzzle.clues
                    _score.value = savedGame.score
                    _currentLevel.value = savedGame.level
                    hintsRemaining = savedGame.hintsRemaining
                    
                    _gameState.value = GameState.Playing
                } else {
                    startNewGame()
                }
                
            } catch (e: Exception) {
                _error.value = "خطأ في تحميل اللعبة المحفوظة: ${e.message}"
                startNewGame()
            }
        }
    }
    
    fun selectCell(row: Int, col: Int) {
        selectedCell = Pair(row, col)
        currentWord = getCurrentWord(row, col)
        
        // Highlight the selected cell in the grid
        currentPuzzle?.let { puzzle ->
            puzzle.selectCell(row, col)
            _puzzle.value = puzzle
        }
    }
    
    fun updateCell(row: Int, col: Int, text: String) {
        currentPuzzle?.let { puzzle ->
            if (puzzle.isValidCell(row, col)) {
                puzzle.setCellValue(row, col, text)
                _puzzle.value = puzzle
                
                // Check if the current word is complete
                currentWord = getCurrentWord(row, col)
                if (isWordComplete(currentWord)) {
                    checkWord(currentWord)
                }
            }
        }
    }
    
    fun requestHint() {
        if (hintsRemaining > 0) {
            selectedCell?.let { (row, col) ->
                val hint = getHintForCell(row, col)
                _showHint.value = true
                hintsRemaining--
            }
        } else {
            _error.value = "لا توجد تلميحات متبقية"
        }
    }
    
    fun hintUsed() {
        _showHint.value = false
    }
    
    fun checkCurrentWord() {
        if (currentWord.isNotEmpty()) {
            checkWord(currentWord)
        }
    }
    
    private fun checkWord(word: String) {
        currentPuzzle?.let { puzzle ->
            if (puzzle.isCorrectWord(word)) {
                // Word is correct
                _score.value = (_score.value ?: 0) + calculateWordScore(word)
                puzzle.markWordAsCompleted(word)
                _puzzle.value = puzzle
                
                // Check if puzzle is completed
                if (puzzle.isCompleted()) {
                    levelCompleted()
                }
            } else {
                // Word is incorrect
                _error.value = "كلمة غير صحيحة، حاول مرة أخرى"
            }
        }
    }
    
    private fun levelCompleted() {
        _gameCompleted.value = true
        
        // Save progress
        gamePreferences.saveProgress(
            level = _currentLevel.value ?: 1,
            score = _score.value ?: 0
        )
        
        // Update high score if applicable
        val currentHighScore = gamePreferences.getHighScore()
        if ((_score.value ?: 0) > currentHighScore) {
            gamePreferences.setHighScore(_score.value ?: 0)
        }
    }
    
    fun nextLevel() {
        _currentLevel.value = (_currentLevel.value ?: 1) + 1
        _gameCompleted.value = false
        startNewGame()
    }
    
    fun pauseGame() {
        _gameState.value = GameState.Paused
        saveGame()
    }
    
    fun resumeGame() {
        if (_gameState.value == GameState.Paused) {
            _gameState.value = GameState.Playing
        }
    }
    
    fun saveGame() {
        currentPuzzle?.let { puzzle ->
            val gameData = GameData(
                puzzle = puzzle,
                score = _score.value ?: 0,
                level = _currentLevel.value ?: 1,
                hintsRemaining = hintsRemaining
            )
            gamePreferences.saveGame(gameData)
        }
    }
    
    fun getCurrentHint(): String {
        return selectedCell?.let { (row, col) ->
            getHintForCell(row, col)
        } ?: "اختر خلية أولاً"
    }
    
    private fun getCurrentWord(row: Int, col: Int): String {
        currentPuzzle?.let { puzzle ->
            return puzzle.getWordAt(row, col)
        }
        return ""
    }
    
    private fun isWordComplete(word: String): Boolean {
        return word.length >= 3 && !word.contains(" ")
    }
    
    private fun getHintForCell(row: Int, col: Int): String {
        currentPuzzle?.let { puzzle ->
            return puzzle.getHintForCell(row, col)
        }
        return "لا يوجد تلميح متاح"
    }
    
    private fun calculateWordScore(word: String): Int {
        return word.length * 10
    }
}