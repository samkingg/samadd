package com.arabicrossword.game.data.network

import com.arabicrossword.game.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AIService {
    @POST("generate-crossword")
    suspend fun generateCrossword(
        @Body request: CrosswordGenerationRequest
    ): Response<CrosswordGenerationResponse>
    
    @POST("generate-clue")
    suspend fun generateClue(
        @Body request: ClueGenerationRequest
    ): Response<ClueGenerationResponse>
}

data class CrosswordGenerationRequest(
    val difficulty: DifficultyLevel,
    val gridSize: Int,
    val theme: String? = null,
    val language: String = "ar",
    val wordCount: Int = 15
)

data class CrosswordGenerationResponse(
    val puzzle: CrosswordPuzzle,
    val success: Boolean,
    val message: String?
)

data class ClueGenerationRequest(
    val word: String,
    val difficulty: DifficultyLevel,
    val context: String? = null,
    val language: String = "ar"
)

data class ClueGenerationResponse(
    val clue: String,
    val success: Boolean,
    val message: String?
)

// Fallback AI service that generates content locally for demo purposes
class LocalAIService {
    private val arabicWords = listOf(
        "كتاب" to "مجموعة من الأوراق المطبوعة",
        "قلم" to "أداة للكتابة",
        "بيت" to "مكان للسكن",
        "سماء" to "الفضاء فوق الأرض",
        "شمس" to "النجم المضيء في النهار",
        "قمر" to "القمر الصناعي الطبيعي للأرض",
        "ماء" to "السائل الشفاف الضروري للحياة",
        "نار" to "اللهب والحرارة",
        "ورد" to "زهرة جميلة ذات رائحة عطرة",
        "حب" to "المشاعر العميقة بين الناس",
        "علم" to "المعرفة والدراسة",
        "حكمة" to "الفهم العميق للحياة",
        "صبر" to "التحمل والانتظار",
        "أمل" to "التطلع للمستقبل الأفضل",
        "سلام" to "حالة الهدوء والأمان"
    )
    
    suspend fun generateLocalCrossword(
        difficulty: DifficultyLevel,
        gridSize: Int = 10
    ): CrosswordPuzzle {
        val selectedWords = arabicWords.shuffled().take(
            when(difficulty) {
                DifficultyLevel.EASY -> 8
                DifficultyLevel.MEDIUM -> 12
                DifficultyLevel.HARD -> 15
                DifficultyLevel.EXPERT -> 18
            }
        )
        
        val words = mutableListOf<CrosswordWord>()
        val clues = mutableListOf<Clue>()
        
        selectedWords.forEachIndexed { index, (word, clueText) ->
            val direction = if (index % 2 == 0) Direction.ACROSS else Direction.DOWN
            val startRow = (1..gridSize - word.length).random()
            val startCol = (1..gridSize - word.length).random()
            
            val crosswordWord = CrosswordWord(
                id = "word_$index",
                text = word,
                startRow = startRow,
                startCol = startCol,
                direction = direction,
                clueNumber = index + 1
            )
            
            val clue = Clue(
                id = "clue_$index",
                number = index + 1,
                text = clueText,
                direction = direction,
                wordId = "word_$index"
            )
            
            words.add(crosswordWord)
            clues.add(clue)
        }
        
        return CrosswordPuzzle(
            id = "puzzle_${System.currentTimeMillis()}",
            title = "كلمات متقاطعة ${difficulty.name}",
            difficulty = difficulty,
            gridSize = gridSize,
            words = words,
            clues = clues
        )
    }
}