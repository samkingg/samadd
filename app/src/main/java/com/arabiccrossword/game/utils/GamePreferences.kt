package com.arabiccrossword.game.utils

import android.content.Context
import android.content.SharedPreferences
import com.arabiccrossword.game.model.GameData
import com.arabiccrossword.game.model.QuestionDifficulty
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GamePreferences(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    companion object {
        private const val PREF_NAME = "arabic_crossword_prefs"
        private const val KEY_HIGH_SCORE = "high_score"
        private const val KEY_CURRENT_LEVEL = "current_level"
        private const val KEY_SAVED_GAME = "saved_game"
        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_ARABIC_FONT = "arabic_font"
        private const val KEY_TOTAL_PLAY_TIME = "total_play_time"
        private const val KEY_QUESTIONS_ANSWERED = "questions_answered"
        private const val KEY_HINTS_USED = "hints_used"
    }
    
    // High Score
    fun getHighScore(): Int {
        return sharedPreferences.getInt(KEY_HIGH_SCORE, 0)
    }
    
    fun setHighScore(score: Int) {
        sharedPreferences.edit().putInt(KEY_HIGH_SCORE, score).apply()
    }
    
    // Current Level
    fun getCurrentLevel(): Int {
        return sharedPreferences.getInt(KEY_CURRENT_LEVEL, 1)
    }
    
    fun setCurrentLevel(level: Int) {
        sharedPreferences.edit().putInt(KEY_CURRENT_LEVEL, level).apply()
    }
    
    // Saved Game
    fun saveGame(gameData: GameData) {
        val gameJson = gson.toJson(gameData)
        sharedPreferences.edit().putString(KEY_SAVED_GAME, gameJson).apply()
    }
    
    fun getSavedGame(): GameData? {
        val gameJson = sharedPreferences.getString(KEY_SAVED_GAME, null)
        return if (gameJson != null) {
            try {
                gson.fromJson(gameJson, GameData::class.java)
            } catch (e: Exception) {
                null
            }
        } else null
    }
    
    fun hasSavedGame(): Boolean {
        return getSavedGame() != null
    }
    
    fun clearSavedGame() {
        sharedPreferences.edit().remove(KEY_SAVED_GAME).apply()
    }
    
    // Difficulty
    fun getDifficulty(): QuestionDifficulty {
        val difficultyString = sharedPreferences.getString(KEY_DIFFICULTY, QuestionDifficulty.MEDIUM.name)
        return try {
            QuestionDifficulty.valueOf(difficultyString ?: QuestionDifficulty.MEDIUM.name)
        } catch (e: Exception) {
            QuestionDifficulty.MEDIUM
        }
    }
    
    fun setDifficulty(difficulty: QuestionDifficulty) {
        sharedPreferences.edit().putString(KEY_DIFFICULTY, difficulty.name).apply()
    }
    
    // Sound Settings
    fun isSoundEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true)
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }
    
    // Vibration Settings
    fun isVibrationEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, true)
    }
    
    fun setVibrationEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }
    
    // Dark Mode
    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }
    
    fun setDarkModeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }
    
    // Arabic Font
    fun isArabicFontEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_ARABIC_FONT, true)
    }
    
    fun setArabicFontEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ARABIC_FONT, enabled).apply()
    }
    
    // Progress Tracking
    fun saveProgress(level: Int, score: Int) {
        setCurrentLevel(level)
        if (score > getHighScore()) {
            setHighScore(score)
        }
    }
    
    // Statistics
    fun getTotalPlayTime(): Long {
        return sharedPreferences.getLong(KEY_TOTAL_PLAY_TIME, 0)
    }
    
    fun addPlayTime(timeMillis: Long) {
        val currentTotal = getTotalPlayTime()
        sharedPreferences.edit().putLong(KEY_TOTAL_PLAY_TIME, currentTotal + timeMillis).apply()
    }
    
    fun getQuestionsAnswered(): Int {
        return sharedPreferences.getInt(KEY_QUESTIONS_ANSWERED, 0)
    }
    
    fun incrementQuestionsAnswered() {
        val current = getQuestionsAnswered()
        sharedPreferences.edit().putInt(KEY_QUESTIONS_ANSWERED, current + 1).apply()
    }
    
    fun getHintsUsed(): Int {
        return sharedPreferences.getInt(KEY_HINTS_USED, 0)
    }
    
    fun incrementHintsUsed() {
        val current = getHintsUsed()
        sharedPreferences.edit().putInt(KEY_HINTS_USED, current + 1).apply()
    }
    
    // Reset all data
    fun resetAllData() {
        sharedPreferences.edit().clear().apply()
    }
    
    // Export/Import (for backup purposes)
    fun exportData(): String {
        val data = mapOf(
            "high_score" to getHighScore(),
            "current_level" to getCurrentLevel(),
            "difficulty" to getDifficulty().name,
            "sound_enabled" to isSoundEnabled(),
            "vibration_enabled" to isVibrationEnabled(),
            "dark_mode" to isDarkModeEnabled(),
            "arabic_font" to isArabicFontEnabled(),
            "total_play_time" to getTotalPlayTime(),
            "questions_answered" to getQuestionsAnswered(),
            "hints_used" to getHintsUsed()
        )
        return gson.toJson(data)
    }
    
    fun importData(jsonData: String): Boolean {
        return try {
            val data = gson.fromJson<Map<String, Any>>(jsonData, object : TypeToken<Map<String, Any>>() {}.type)
            
            data["high_score"]?.let { setHighScore((it as Double).toInt()) }
            data["current_level"]?.let { setCurrentLevel((it as Double).toInt()) }
            data["difficulty"]?.let { 
                try {
                    setDifficulty(QuestionDifficulty.valueOf(it as String))
                } catch (e: Exception) {
                    // Use default if invalid
                }
            }
            data["sound_enabled"]?.let { setSoundEnabled(it as Boolean) }
            data["vibration_enabled"]?.let { setVibrationEnabled(it as Boolean) }
            data["dark_mode"]?.let { setDarkModeEnabled(it as Boolean) }
            data["arabic_font"]?.let { setArabicFontEnabled(it as Boolean) }
            data["total_play_time"]?.let { addPlayTime((it as Double).toLong()) }
            data["questions_answered"]?.let { 
                val current = getQuestionsAnswered()
                sharedPreferences.edit().putInt(KEY_QUESTIONS_ANSWERED, (it as Double).toInt() + current).apply()
            }
            data["hints_used"]?.let { 
                val current = getHintsUsed()
                sharedPreferences.edit().putInt(KEY_HINTS_USED, (it as Double).toInt() + current).apply()
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
}