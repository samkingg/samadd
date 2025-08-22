package com.arabicrossword.game.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arabicrossword.game.data.model.*
import com.arabicrossword.game.ui.theme.CrosswordCellTextStyle
import com.arabicrossword.game.ui.theme.ClueNumberTextStyle

@Composable
fun CrosswordGrid(
    puzzle: CrosswordPuzzle,
    gameState: GameState,
    onCellClick: (row: Int, col: Int) -> Unit,
    onCellValueChange: (row: Int, col: Int, value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridSize = puzzle.gridSize
    val cellSize = 35.dp
    
    // Create grid data structure
    val grid = remember(puzzle) {
        createGridFromPuzzle(puzzle)
    }
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                repeat(gridSize) { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(gridSize) { col ->
                            CrosswordCell(
                                cell = grid[row][col],
                                isSelected = gameState.selectedCell == Pair(row, col),
                                isHighlighted = isPartOfSelectedWord(
                                    row, col, gameState, puzzle
                                ),
                                currentValue = gameState.currentGrid.getOrNull(row)
                                    ?.getOrNull(col) ?: "",
                                onCellClick = { onCellClick(row, col) },
                                onValueChange = { value ->
                                    onCellValueChange(row, col, value)
                                },
                                modifier = Modifier.size(cellSize)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CrosswordCell(
    cell: Cell,
    isSelected: Boolean,
    isHighlighted: Boolean,
    currentValue: String,
    onCellClick: () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cell_scale"
    )
    
    val backgroundColor = when {
        cell.isBlocked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        isHighlighted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        cell.isCorrect -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }
    
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .background(
                backgroundColor,
                RoundedCornerShape(4.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .pointerInput(Unit) {
                if (!cell.isBlocked) {
                    detectTapGestures {
                        onCellClick()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (!cell.isBlocked) {
            // Clue number in top-left corner
            cell.clueNumber?.let { number ->
                Text(
                    text = number.toString(),
                    style = ClueNumberTextStyle,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(2.dp)
                )
            }
            
            // Cell letter
            Text(
                text = currentValue,
                style = CrosswordCellTextStyle.copy(
                    color = if (cell.isCorrect) 
                        MaterialTheme.colorScheme.tertiary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize().wrapContentHeight()
            )
        }
    }
}

private fun createGridFromPuzzle(puzzle: CrosswordPuzzle): List<List<Cell>> {
    val gridSize = puzzle.gridSize
    val grid = MutableList(gridSize) { row ->
        MutableList(gridSize) { col ->
            Cell(row = row, col = col, isBlocked = true)
        }
    }
    
    // Place words in the grid
    puzzle.words.forEach { word ->
        val clueNumber = word.clueNumber
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
                    clueNumber = if (letterIndex == 0) clueNumber else existingCell.clueNumber,
                    belongsToWords = existingCell.belongsToWords + word.id
                )
            }
        }
    }
    
    return grid
}

private fun isPartOfSelectedWord(
    row: Int,
    col: Int,
    gameState: GameState,
    puzzle: CrosswordPuzzle
): Boolean {
    val selectedCell = gameState.selectedCell ?: return false
    val selectedDirection = gameState.selectedDirection
    
    // Find the word that contains the selected cell
    val selectedWord = puzzle.words.find { word ->
        val wordCells = getWordCells(word)
        wordCells.contains(Pair(selectedCell.first, selectedCell.second))
    } ?: return false
    
    // Check if current cell is part of the same word
    val wordCells = getWordCells(selectedWord)
    return wordCells.contains(Pair(row, col))
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