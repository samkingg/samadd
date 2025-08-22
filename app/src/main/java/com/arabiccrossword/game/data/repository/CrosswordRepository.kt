package com.arabiccrossword.game.data.repository

import android.content.Context
import com.arabiccrossword.game.model.*
import kotlin.random.Random

class CrosswordRepository(private val context: Context) {
    
    fun createPuzzleFromQuestions(questions: List<AIQuestion>): CrosswordPuzzle {
        // Create a simple grid layout based on the questions
        val gridSize = calculateGridSize(questions)
        val grid = createEmptyGrid(gridSize.first, gridSize.second)
        
        // Place words in the grid
        val crosswordWords = placeWordsInGrid(grid, questions)
        
        // Update clues
        val clues = crosswordWords.map { it.hint }
        
        return CrosswordPuzzle(
            id = generatePuzzleId(),
            grid = grid,
            words = crosswordWords,
            clues = clues,
            difficulty = questions.firstOrNull()?.difficulty ?: QuestionDifficulty.MEDIUM,
            level = 1
        )
    }
    
    private fun calculateGridSize(questions: List<AIQuestion>): Pair<Int, Int> {
        val maxWordLength = questions.maxOfOrNull { it.answer.length } ?: 5
        val numWords = questions.size
        
        // Calculate grid dimensions
        val cols = (maxWordLength + 2).coerceAtLeast(8)
        val rows = (numWords * 2 + 2).coerceAtLeast(8)
        
        return Pair(rows, cols)
    }
    
    private fun createEmptyGrid(rows: Int, cols: Int): Array<Array<CrosswordCell>> {
        val grid = Array(rows) { row ->
            Array(cols) { col ->
                CrosswordCell(
                    row = row,
                    col = col,
                    type = CellType.BLANK
                )
            }
        }
        return grid
    }
    
    private fun placeWordsInGrid(grid: Array<Array<CrosswordCell>>, questions: List<AIQuestion>): List<CrosswordWord> {
        val crosswordWords = mutableListOf<CrosswordWord>()
        var wordNumber = 1
        
        for (question in questions) {
            val word = question.answer
            val placement = findWordPlacement(grid, word)
            
            if (placement != null) {
                val (row, col, direction) = placement
                val cells = placeWordInGrid(grid, word, row, col, direction, wordNumber)
                
                val crosswordWord = CrosswordWord(
                    id = "word_${wordNumber}",
                    answer = word,
                    hint = question.question,
                    cells = cells,
                    direction = direction
                )
                
                crosswordWords.add(crosswordWord)
                wordNumber++
            }
        }
        
        return crosswordWords
    }
    
    private fun findWordPlacement(
        grid: Array<Array<CrosswordCell>>,
        word: String
    ): Triple<Int, Int, WordDirection>? {
        val rows = grid.size
        val cols = grid[0].size
        
        // Try horizontal placement first
        for (row in 0 until rows) {
            for (col in 0 until cols - word.length + 1) {
                if (canPlaceWordHorizontally(grid, word, row, col)) {
                    return Triple(row, col, WordDirection.HORIZONTAL)
                }
            }
        }
        
        // Try vertical placement
        for (row in 0 until rows - word.length + 1) {
            for (col in 0 until cols) {
                if (canPlaceWordVertically(grid, word, row, col)) {
                    return Triple(row, col, WordDirection.VERTICAL)
                }
            }
        }
        
        return null
    }
    
    private fun canPlaceWordHorizontally(
        grid: Array<Array<CrosswordCell>>,
        word: String,
        row: Int,
        col: Int
    ): Boolean {
        for (i in word.indices) {
            val cell = grid[row][col + i]
            if (cell.type != CellType.BLANK) {
                return false
            }
        }
        return true
    }
    
    private fun canPlaceWordVertically(
        grid: Array<Array<CrosswordCell>>,
        word: String,
        row: Int,
        col: Int
    ): Boolean {
        for (i in word.indices) {
            val cell = grid[row + i][col]
            if (cell.type != CellType.BLANK) {
                return false
            }
        }
        return true
    }
    
    private fun placeWordInGrid(
        grid: Array<Array<CrosswordCell>>,
        word: String,
        row: Int,
        col: Int,
        direction: WordDirection,
        wordNumber: Int
    ): List<CrosswordCell> {
        val cells = mutableListOf<CrosswordCell>()
        
        for (i in word.indices) {
            val currentRow = if (direction == WordDirection.HORIZONTAL) row else row + i
            val currentCol = if (direction == WordDirection.HORIZONTAL) col + i else col
            
            val cell = CrosswordCell(
                row = currentRow,
                col = currentCol,
                type = CellType.PLAYABLE,
                number = if (i == 0) wordNumber else null
            )
            
            grid[currentRow][currentCol] = cell
            cells.add(cell)
        }
        
        return cells
    }
    
    private fun generatePuzzleId(): String {
        return "puzzle_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
    }
    
    fun createCustomPuzzle(
        words: List<String>,
        clues: List<String>,
        difficulty: QuestionDifficulty
    ): CrosswordPuzzle {
        // Create a custom puzzle with specific words and clues
        val aiQuestions = words.mapIndexed { index, word ->
            AIQuestion(
                id = "custom_$index",
                question = clues.getOrNull(index) ?: "اكتب الكلمة",
                answer = word,
                category = "مخصص",
                difficulty = difficulty,
                hints = emptyList()
            )
        }
        
        return createPuzzleFromQuestions(aiQuestions)
    }
    
    fun validatePuzzle(puzzle: CrosswordPuzzle): Boolean {
        // Check if all words can be placed
        val allWordsPlaced = puzzle.words.all { word ->
            word.cells.isNotEmpty() && word.cells.all { cell ->
                puzzle.isValidCell(cell.row, cell.col)
            }
        }
        
        // Check for overlapping words
        val noOverlaps = !hasOverlappingWords(puzzle)
        
        // Check if puzzle is solvable
        val isSolvable = puzzle.words.isNotEmpty()
        
        return allWordsPlaced && noOverlaps && isSolvable
    }
    
    private fun hasOverlappingWords(puzzle: CrosswordPuzzle): Boolean {
        val allCells = puzzle.words.flatMap { it.cells }
        val cellPositions = allCells.map { Pair(it.row, it.col) }
        return cellPositions.size != cellPositions.toSet().size
    }
    
    fun getPuzzleStatistics(puzzle: CrosswordPuzzle): Map<String, Any> {
        return mapOf(
            "total_words" to puzzle.words.size,
            "grid_size" to "${puzzle.grid.size}x${puzzle.grid[0].size}",
            "difficulty" to puzzle.difficulty.name,
            "completion_percentage" to puzzle.getCompletionPercentage(),
            "playable_cells" to puzzle.grid.flatten().count { it.type == CellType.PLAYABLE }
        )
    }
}