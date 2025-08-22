package com.arabicrossword.game.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.arabicrossword.game.data.model.*
import com.arabicrossword.game.data.network.LocalAIService
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val localAIService: LocalAIService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
    
    fun loadGame(difficulty: DifficultyLevel) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val puzzle = localAIService.generateLocalCrossword(difficulty)
                val initialGrid = createEmptyGrid(puzzle.gridSize)
                
                val gameState = GameState(
                    puzzleId = puzzle.id,
                    currentGrid = initialGrid
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    puzzle = puzzle,
                    error = null
                )
                _gameState.value = gameState
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "خطأ في تحميل اللعبة"
                )
            }
        }
    }
    
    fun onCellClick(row: Int, col: Int) {
        val currentState = _gameState.value ?: return
        val puzzle = _uiState.value.puzzle ?: return
        
        // Check if cell is valid (not blocked)
        val grid = createGridFromPuzzle(puzzle)
        if (grid[row][col].isBlocked) return
        
        // Update selected cell and determine direction
        val newDirection = determineDirection(row, col, currentState, puzzle)
        
        _gameState.value = currentState.copy(
            selectedCell = Pair(row, col),
            selectedDirection = newDirection
        )
    }
    
    fun onCellValueChange(row: Int, col: Int, value: String) {
        val currentState = _gameState.value ?: return
        val puzzle = _uiState.value.puzzle ?: return
        
        // Only allow single Arabic character
        val cleanValue = value.take(1).filter { it.isArabicLetter() }
        
        // Update grid
        val newGrid = currentState.currentGrid.toMutableList().map { it.toMutableList() }
        if (row in newGrid.indices && col in newGrid[row].indices) {
            newGrid[row][col] = cleanValue
        }
        
        val updatedState = currentState.copy(currentGrid = newGrid)
        _gameState.value = updatedState
        
        // Check if puzzle is completed
        checkPuzzleCompletion(updatedState, puzzle)
        
        // Auto-advance to next cell if enabled
        if (cleanValue.isNotEmpty()) {
            autoAdvanceToNextCell(row, col, updatedState, puzzle)
        }
    }
    
    fun onClueClick(clue: Clue) {
        val puzzle = _uiState.value.puzzle ?: return
        val word = puzzle.words.find { it.id == clue.wordId } ?: return
        
        _gameState.value = _gameState.value?.copy(
            selectedCell = Pair(word.startRow, word.startCol),
            selectedDirection = clue.direction
        )
    }
    
    fun checkWord() {
        val currentState = _gameState.value ?: return
        val puzzle = _uiState.value.puzzle ?: return
        val selectedCell = currentState.selectedCell ?: return
        
        // Find the word at the selected cell
        val word = findWordAtCell(selectedCell, currentState.selectedDirection, puzzle) ?: return
        
        // Check if word is correct
        val isCorrect = checkWordCorrectness(word, currentState.currentGrid)
        
        if (isCorrect) {
            // Mark word as correct and update score
            val newScore = currentState.score + calculateWordScore(word, currentState)
            _gameState.value = currentState.copy(score = newScore)
            
            // Show success message
            _uiState.value = _uiState.value.copy(
                showMessage = "ممتاز! كلمة صحيحة"
            )
        } else {
            _uiState.value = _uiState.value.copy(
                showMessage = "الكلمة غير صحيحة، حاول مرة أخرى"
            )
        }
    }
    
    fun getHint() {
        val currentState = _gameState.value ?: return
        val puzzle = _uiState.value.puzzle ?: return
        val selectedCell = currentState.selectedCell ?: return
        
        // Find the word and reveal next letter
        val word = findWordAtCell(selectedCell, currentState.selectedDirection, puzzle) ?: return
        val wordCells = getWordCells(word)
        
        // Find first empty cell in word
        val emptyCell = wordCells.find { (row, col) ->
            currentState.currentGrid.getOrNull(row)?.getOrNull(col)?.isEmpty() == true
        }
        
        emptyCell?.let { (row, col) ->
            val correctLetter = word.text.getOrNull(
                if (word.direction == Direction.ACROSS) col - word.startCol 
                else row - word.startRow
            )?.toString() ?: return
            
            onCellValueChange(row, col, correctLetter)
            
            val newState = currentState.copy(hintsUsed = currentState.hintsUsed + 1)
            _gameState.value = newState
        }
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(showMessage = null)
    }
    
    private fun createEmptyGrid(size: Int): List<List<String>> {
        return List(size) { List(size) { "" } }
    }
    
    private fun createGridFromPuzzle(puzzle: CrosswordPuzzle): List<List<Cell>> {
        val gridSize = puzzle.gridSize
        val grid = MutableList(gridSize) { row ->
            MutableList(gridSize) { col ->
                Cell(row = row, col = col, isBlocked = true)
            }
        }
        
        puzzle.words.forEach { word ->
            repeat(word.text.length) { letterIndex ->
                val row = if (word.direction == Direction.DOWN) 
                    word.startRow + letterIndex else word.startRow
                val col = if (word.direction == Direction.ACROSS) 
                    word.startCol + letterIndex else word.startCol
                    
                if (row in 0 until gridSize && col in 0 until gridSize) {
                    val existingCell = grid[row][col]
                    grid[row][col] = existingCell.copy(
                        isBlocked = false,
                        letter = word.text.getOrNull(letterIndex)?.toString(),
                        clueNumber = if (letterIndex == 0) word.clueNumber else existingCell.clueNumber
                    )
                }
            }
        }
        
        return grid
    }
    
    private fun determineDirection(
        row: Int, 
        col: Int, 
        gameState: GameState, 
        puzzle: CrosswordPuzzle
    ): Direction {
        // If same cell clicked, toggle direction
        if (gameState.selectedCell == Pair(row, col)) {
            return if (gameState.selectedDirection == Direction.ACROSS) 
                Direction.DOWN else Direction.ACROSS
        }
        
        // Find words that contain this cell
        val wordsAtCell = puzzle.words.filter { word ->
            val wordCells = getWordCells(word)
            wordCells.contains(Pair(row, col))
        }
        
        // Prefer horizontal (across) by default
        return wordsAtCell.find { it.direction == Direction.ACROSS }?.direction 
            ?: wordsAtCell.firstOrNull()?.direction 
            ?: Direction.ACROSS
    }
    
    private fun getWordCells(word: CrosswordWord): List<Pair<Int, Int>> {
        return (0 until word.text.length).map { index ->
            val row = if (word.direction == Direction.DOWN) 
                word.startRow + index else word.startRow
            val col = if (word.direction == Direction.ACROSS) 
                word.startCol + index else word.startCol
            Pair(row, col)
        }
    }
    
    private fun findWordAtCell(
        cell: Pair<Int, Int>,
        direction: Direction,
        puzzle: CrosswordPuzzle
    ): CrosswordWord? {
        return puzzle.words.find { word ->
            word.direction == direction && getWordCells(word).contains(cell)
        }
    }
    
    private fun checkWordCorrectness(word: CrosswordWord, grid: List<List<String>>): Boolean {
        val wordCells = getWordCells(word)
        val enteredWord = wordCells.mapNotNull { (row, col) ->
            grid.getOrNull(row)?.getOrNull(col)
        }.joinToString("")
        
        return enteredWord == word.text
    }
    
    private fun calculateWordScore(word: CrosswordWord, gameState: GameState): Int {
        val baseScore = word.text.length * 10
        val hintsUsedPenalty = gameState.hintsUsed * 5
        return maxOf(baseScore - hintsUsedPenalty, 10)
    }
    
    private fun checkPuzzleCompletion(gameState: GameState, puzzle: CrosswordPuzzle) {
        val allWordsCorrect = puzzle.words.all { word ->
            checkWordCorrectness(word, gameState.currentGrid)
        }
        
        if (allWordsCorrect) {
            _gameState.value = gameState.copy(isCompleted = true)
            _uiState.value = _uiState.value.copy(
                showMessage = "تهانينا! لقد أكملت الأحجية بنجاح"
            )
        }
    }
    
    private fun autoAdvanceToNextCell(
        row: Int, 
        col: Int, 
        gameState: GameState, 
        puzzle: CrosswordPuzzle
    ) {
        val word = findWordAtCell(Pair(row, col), gameState.selectedDirection, puzzle) ?: return
        val wordCells = getWordCells(word)
        val currentIndex = wordCells.indexOf(Pair(row, col))
        
        if (currentIndex >= 0 && currentIndex < wordCells.size - 1) {
            val nextCell = wordCells[currentIndex + 1]
            _gameState.value = gameState.copy(selectedCell = nextCell)
        }
    }
    
    private fun Char.isArabicLetter(): Boolean {
        return this in '\u0600'..'\u06FF' || this in '\u0750'..'\u077F'
    }
}

data class GameUiState(
    val isLoading: Boolean = false,
    val puzzle: CrosswordPuzzle? = null,
    val error: String? = null,
    val showMessage: String? = null
)