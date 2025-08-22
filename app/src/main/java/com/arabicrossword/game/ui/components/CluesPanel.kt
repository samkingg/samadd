package com.arabicrossword.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.arabicrossword.game.R
import com.arabicrossword.game.data.model.*

@Composable
fun CluesPanel(
    puzzle: CrosswordPuzzle,
    gameState: GameState,
    onClueClick: (Clue) -> Unit,
    modifier: Modifier = Modifier
) {
    val acrossClues = puzzle.clues.filter { it.direction == Direction.ACROSS }
        .sortedBy { it.number }
    val downClues = puzzle.clues.filter { it.direction == Direction.DOWN }
        .sortedBy { it.number }
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = modifier
                .fillMaxHeight()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with tabs
                var selectedTab by remember { mutableIntStateOf(0) }
                
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = stringResource(R.string.clues_across),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = stringResource(R.string.clues_down),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Clues list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val clues = if (selectedTab == 0) acrossClues else downClues
                    
                    items(clues) { clue ->
                        ClueItem(
                            clue = clue,
                            isSelected = isClueSelected(clue, gameState, puzzle),
                            onClick = { onClueClick(clue) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClueItem(
    clue: Clue,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Clue number
            Box(
                modifier = Modifier
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = clue.number.toString(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondary
                )
            }
            
            // Clue text
            Text(
                text = clue.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun isClueSelected(
    clue: Clue,
    gameState: GameState,
    puzzle: CrosswordPuzzle
): Boolean {
    val selectedCell = gameState.selectedCell ?: return false
    
    // Find the word associated with this clue
    val word = puzzle.words.find { it.id == clue.wordId } ?: return false
    
    // Check if the selected cell is part of this word
    val wordCells = getWordCells(word)
    return wordCells.contains(Pair(selectedCell.first, selectedCell.second))
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