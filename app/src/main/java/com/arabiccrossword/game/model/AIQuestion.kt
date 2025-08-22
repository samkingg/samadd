package com.arabiccrossword.game.model

data class AIQuestion(
    val id: String,
    val question: String,
    val answer: String,
    val category: String,
    val difficulty: QuestionDifficulty,
    val hints: List<String>,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class QuestionDifficulty {
    EASY,
    MEDIUM,
    HARD
}

data class GameState(
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,
    val currentLevel: Int = 1,
    val score: Int = 0,
    val hintsRemaining: Int = 3
)

sealed class GameState {
    object Loading : GameState()
    object Playing : GameState()
    object Paused : GameState()
    object GameOver : GameState()
}

data class GameData(
    val puzzle: CrosswordPuzzle,
    val score: Int,
    val level: Int,
    val hintsRemaining: Int,
    val timestamp: Long = System.currentTimeMillis()
)