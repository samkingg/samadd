package com.arabicrossword.game.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(tableName = "crossword_puzzles")
@Parcelize
data class CrosswordPuzzle(
    @PrimaryKey val id: String,
    val title: String,
    val difficulty: DifficultyLevel,
    val gridSize: Int,
    val words: List<CrosswordWord>,
    val clues: List<Clue>,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val currentProgress: List<List<String>> = emptyList()
) : Parcelable

@Parcelize
data class CrosswordWord(
    val id: String,
    val text: String,
    val startRow: Int,
    val startCol: Int,
    val direction: Direction,
    val clueNumber: Int
) : Parcelable

@Parcelize
data class Clue(
    val id: String,
    val number: Int,
    val text: String,
    val direction: Direction,
    val wordId: String
) : Parcelable

@Parcelize
data class Cell(
    val row: Int,
    val col: Int,
    val letter: String? = null,
    val isBlocked: Boolean = false,
    val clueNumber: Int? = null,
    val isCorrect: Boolean = false,
    val belongsToWords: List<String> = emptyList()
) : Parcelable

enum class Direction {
    ACROSS, DOWN
}

enum class DifficultyLevel {
    EASY, MEDIUM, HARD, EXPERT
}

@Parcelize
data class GameState(
    val puzzleId: String,
    val currentGrid: List<List<String>>,
    val selectedCell: Pair<Int, Int>? = null,
    val selectedDirection: Direction = Direction.ACROSS,
    val hintsUsed: Int = 0,
    val timeElapsed: Long = 0,
    val isCompleted: Boolean = false,
    val score: Int = 0
) : Parcelable