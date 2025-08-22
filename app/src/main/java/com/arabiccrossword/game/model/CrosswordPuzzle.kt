package com.arabiccrossword.game.model

data class CrosswordPuzzle(
    val id: String,
    val grid: Array<Array<CrosswordCell>>,
    val words: List<CrosswordWord>,
    val clues: List<String>,
    val difficulty: QuestionDifficulty,
    val level: Int
) {
    
    private var selectedRow: Int = -1
    private var selectedCol: Int = -1
    
    fun getGridSize(): Pair<Int, Int> {
        return Pair(grid.size, grid[0].size)
    }
    
    fun isValidCell(row: Int, col: Int): Boolean {
        return row >= 0 && row < grid.size && 
               col >= 0 && col < grid[0].size && 
               grid[row][col].type != CellType.BLANK
    }
    
    fun isPlayableCell(row: Int, col: Int): Boolean {
        return isValidCell(row, col) && grid[row][col].type == CellType.PLAYABLE
    }
    
    fun setCellValue(row: Int, col: Int, value: String) {
        if (isValidCell(row, col) && grid[row][col].type == CellType.PLAYABLE) {
            grid[row][col].value = value.uppercase()
            grid[row][col].isFilled = value.isNotEmpty()
        }
    }
    
    fun getCellValue(row: Int, col: Int): String {
        return if (isValidCell(row, col)) grid[row][col].value else ""
    }
    
    fun selectCell(row: Int, col: Int) {
        if (isValidCell(row, col)) {
            // Clear previous selection
            if (selectedRow >= 0 && selectedCol >= 0) {
                grid[selectedRow][selectedCol].isSelected = false
            }
            
            // Set new selection
            selectedRow = row
            selectedCol = col
            grid[row][col].isSelected = true
        }
    }
    
    fun getSelectedCell(): Pair<Int, Int>? {
        return if (selectedRow >= 0 && selectedCol >= 0) {
            Pair(selectedRow, selectedCol)
        } else null
    }
    
    fun getWordAt(row: Int, col: Int): String {
        val word = words.find { word ->
            word.cells.any { it.row == row && it.col == col }
        }
        
        return word?.let { crosswordWord ->
            val sortedCells = crosswordWord.cells.sortedBy { it.col }
            sortedCells.joinToString("") { cell ->
                grid[cell.row][cell.col].value.ifEmpty { " " }
            }
        } ?: ""
    }
    
    fun getHintForCell(row: Int, col: Int): String {
        val word = words.find { word ->
            word.cells.any { it.row == row && it.col == col }
        }
        
        return word?.hint ?: "لا يوجد تلميح متاح"
    }
    
    fun isCorrectWord(word: String): Boolean {
        return words.any { crosswordWord ->
            crosswordWord.answer.equals(word, ignoreCase = true)
        }
    }
    
    fun markWordAsCompleted(word: String) {
        words.find { it.answer.equals(word, ignoreCase = true) }?.let { crosswordWord ->
            crosswordWord.isCompleted = true
            crosswordWord.cells.forEach { cell ->
                grid[cell.row][cell.col].isCompleted = true
            }
        }
    }
    
    fun isCompleted(): Boolean {
        return words.all { it.isCompleted }
    }
    
    fun getCompletionPercentage(): Float {
        val completedWords = words.count { it.isCompleted }
        return completedWords.toFloat() / words.size
    }
    
    fun getWordByPosition(row: Int, col: Int): CrosswordWord? {
        return words.find { word ->
            word.cells.any { it.row == row && it.col == col }
        }
    }
    
    fun getAdjacentCells(row: Int, col: Int): List<Pair<Int, Int>> {
        val adjacent = mutableListOf<Pair<Int, Int>>()
        
        // Check all 8 directions
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                
                val newRow = row + dr
                val newCol = col + dc
                
                if (isValidCell(newRow, newCol)) {
                    adjacent.add(Pair(newRow, newCol))
                }
            }
        }
        
        return adjacent
    }
    
    fun reset() {
        grid.forEach { row ->
            row.forEach { cell ->
                if (cell.type == CellType.PLAYABLE) {
                    cell.value = ""
                    cell.isFilled = false
                    cell.isSelected = false
                    cell.isCompleted = false
                }
            }
        }
        
        words.forEach { word ->
            word.isCompleted = false
        }
        
        selectedRow = -1
        selectedCol = -1
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as CrosswordPuzzle
        
        if (id != other.id) return false
        if (!grid.contentDeepEquals(other.grid)) return false
        if (words != other.words) return false
        if (clues != other.clues) return false
        if (difficulty != other.difficulty) return false
        if (level != other.level) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + grid.contentDeepHashCode()
        result = 31 * result + words.hashCode()
        result = 31 * result + clues.hashCode()
        result = 31 * result + difficulty.hashCode()
        result = 31 * result + level
        return result
    }
}

data class CrosswordCell(
    val row: Int,
    val col: Int,
    val type: CellType,
    var value: String = "",
    var isFilled: Boolean = false,
    var isSelected: Boolean = false,
    var isCompleted: Boolean = false,
    val number: Int? = null
)

data class CrosswordWord(
    val id: String,
    val answer: String,
    val hint: String,
    val cells: List<CrosswordCell>,
    val direction: WordDirection,
    var isCompleted: Boolean = false
)

enum class CellType {
    PLAYABLE,    // Cell where user can input letters
    FILLED,      // Cell with fixed letters (part of the puzzle)
    BLANK        // Empty cell (not part of any word)
}

enum class WordDirection {
    HORIZONTAL,
    VERTICAL
}