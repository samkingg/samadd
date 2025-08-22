package com.arabiccrossword.game.data.repository

import com.arabiccrossword.game.model.AIQuestion
import com.arabiccrossword.game.model.QuestionDifficulty
import com.arabiccrossword.game.network.AIApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AIQuestionRepository {
    
    private val aiApiService = AIApiService()
    
    suspend fun generateQuestions(
        level: Int,
        difficulty: QuestionDifficulty
    ): List<AIQuestion> = withContext(Dispatchers.IO) {
        try {
            // Generate questions based on level and difficulty
            val questions = mutableListOf<AIQuestion>()
            
            when (difficulty) {
                QuestionDifficulty.EASY -> {
                    questions.addAll(generateEasyQuestions(level))
                }
                QuestionDifficulty.MEDIUM -> {
                    questions.addAll(generateMediumQuestions(level))
                }
                QuestionDifficulty.HARD -> {
                    questions.addAll(generateHardQuestions(level))
                }
            }
            
            // Enhance questions with AI if available
            if (aiApiService.isAvailable()) {
                questions.forEach { question ->
                    val enhancedQuestion = aiApiService.enhanceQuestion(question)
                    if (enhancedQuestion != null) {
                        questions[questions.indexOf(question)] = enhancedQuestion
                    }
                }
            }
            
            questions
        } catch (e: Exception) {
            // Fallback to predefined questions if AI fails
            generateFallbackQuestions(level, difficulty)
        }
    }
    
    private fun generateEasyQuestions(level: Int): List<AIQuestion> {
        return listOf(
            AIQuestion(
                id = "easy_${level}_1",
                question = "اسم حيوان يبدأ بحرف الباء",
                answer = "بطة",
                category = "حيوانات",
                difficulty = QuestionDifficulty.EASY,
                hints = listOf("يعيش في الماء", "يسبح")
            ),
            AIQuestion(
                id = "easy_${level}_2",
                question = "اسم فاكهة حمراء",
                answer = "تفاح",
                category = "فواكه",
                difficulty = QuestionDifficulty.EASY,
                hints = listOf("شجرة", "أحمر")
            ),
            AIQuestion(
                id = "easy_${level}_3",
                question = "اسم لون السماء",
                answer = "أزرق",
                category = "ألوان",
                difficulty = QuestionDifficulty.EASY,
                hints = listOf("لون البحر", "لون النهار")
            )
        )
    }
    
    private fun generateMediumQuestions(level: Int): List<AIQuestion> {
        return listOf(
            AIQuestion(
                id = "medium_${level}_1",
                question = "عاصمة مصر",
                answer = "القاهرة",
                category = "جغرافيا",
                difficulty = QuestionDifficulty.MEDIUM,
                hints = listOf("مدينة الألف مئذنة", "عاصمة مصر")
            ),
            AIQuestion(
                id = "medium_${level}_2",
                question = "اسم شاعر عربي مشهور",
                answer = "المتنبي",
                category = "أدب",
                difficulty = QuestionDifficulty.MEDIUM,
                hints = listOf("شاعر", "عربي", "مشهور")
            ),
            AIQuestion(
                id = "medium_${level}_3",
                question = "اسم نهر في العراق",
                answer = "دجلة",
                category = "جغرافيا",
                difficulty = QuestionDifficulty.MEDIUM,
                hints = listOf("نهر", "في العراق", "يتقاطع مع الفرات")
            )
        )
    }
    
    private fun generateHardQuestions(level: Int): List<AIQuestion> {
        return listOf(
            AIQuestion(
                id = "hard_${level}_1",
                question = "اسم عالم رياضيات عربي",
                answer = "الخوارزمي",
                category = "علوم",
                difficulty = QuestionDifficulty.HARD,
                hints = listOf("عالم", "رياضيات", "عربي", "مشهور")
            ),
            AIQuestion(
                id = "hard_${level}_2",
                question = "اسم كتاب في الطب",
                answer = "القانون",
                category = "طب",
                difficulty = QuestionDifficulty.HARD,
                hints = listOf("كتاب", "طب", "ابن سينا")
            ),
            AIQuestion(
                id = "hard_${level}_3",
                question = "اسم مدينة أندلسية",
                answer = "قرطبة",
                category = "تاريخ",
                difficulty = QuestionDifficulty.HARD,
                hints = listOf("مدينة", "أندلسية", "مشهورة")
            )
        )
    }
    
    private fun generateFallbackQuestions(level: Int, difficulty: QuestionDifficulty): List<AIQuestion> {
        // Return a mix of questions when AI is not available
        return when (difficulty) {
            QuestionDifficulty.EASY -> generateEasyQuestions(level)
            QuestionDifficulty.MEDIUM -> generateMediumQuestions(level)
            QuestionDifficulty.HARD -> generateHardQuestions(level)
        }
    }
    
    suspend fun getQuestionSuggestions(category: String): List<String> = withContext(Dispatchers.IO) {
        try {
            aiApiService.getQuestionSuggestions(category)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun validateAnswer(question: String, answer: String): Boolean = withContext(Dispatchers.IO) {
        try {
            aiApiService.validateAnswer(question, answer)
        } catch (e: Exception) {
            // Fallback validation
            answer.length >= 2 && answer.all { it.isLetter() }
        }
    }
}