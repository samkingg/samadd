package com.arabiccrossword.game.network

import com.arabiccrossword.game.model.AIQuestion
import com.arabiccrossword.game.model.QuestionDifficulty
import kotlinx.coroutines.delay
import java.util.*

class AIApiService {
    
    private var isAvailable = true
    private val random = Random()
    
    suspend fun isAvailable(): Boolean {
        // Simulate network check
        delay(100)
        return isAvailable
    }
    
    suspend fun enhanceQuestion(question: AIQuestion): AIQuestion? {
        return try {
            // Simulate AI enhancement
            delay(200)
            
            // Randomly enhance some questions
            if (random.nextBoolean()) {
                question.copy(
                    hints = question.hints + generateAdditionalHint(question.category)
                )
            } else {
                question
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun generateQuestions(
        level: Int,
        difficulty: QuestionDifficulty,
        category: String? = null
    ): List<AIQuestion> {
        return try {
            delay(300)
            
            val questions = mutableListOf<AIQuestion>()
            val numQuestions = when (difficulty) {
                QuestionDifficulty.EASY -> 3
                QuestionDifficulty.MEDIUM -> 4
                QuestionDifficulty.HARD -> 5
            }
            
            for (i in 1..numQuestions) {
                val question = generateAIQuestion(level, difficulty, category, i)
                questions.add(question)
            }
            
            questions
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getQuestionSuggestions(category: String): List<String> {
        return try {
            delay(150)
            
            when (category.lowercase()) {
                "حيوانات" -> listOf("أسد", "نمر", "فيل", "زرافة", "قرد")
                "فواكه" -> listOf("موز", "برتقال", "عنب", "فراولة", "مانجو")
                "ألوان" -> listOf("أحمر", "أصفر", "أخضر", "بنفسجي", "برتقالي")
                "جغرافيا" -> listOf("جبل", "بحر", "صحراء", "غابة", "جزيرة")
                "أدب" -> listOf("شاعر", "كاتب", "رواية", "قصيدة", "كتاب")
                else -> listOf("كلمة", "اسم", "شيء", "مكان", "شخص")
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun validateAnswer(question: String, answer: String): Boolean {
        return try {
            delay(100)
            
            // Simple validation logic
            val cleanAnswer = answer.trim().lowercase()
            val cleanQuestion = question.lowercase()
            
            when {
                cleanQuestion.contains("حيوان") -> {
                    cleanAnswer in listOf("أسد", "نمر", "فيل", "زرافة", "قرد", "بطة", "دجاجة")
                }
                cleanQuestion.contains("فاكهة") -> {
                    cleanAnswer in listOf("تفاح", "موز", "برتقال", "عنب", "فراولة")
                }
                cleanQuestion.contains("لون") -> {
                    cleanAnswer in listOf("أحمر", "أزرق", "أخضر", "أصفر", "أسود", "أبيض")
                }
                cleanQuestion.contains("عاصمة") -> {
                    cleanAnswer in listOf("القاهرة", "دمشق", "بغداد", "الرياض", "عمان")
                }
                else -> {
                    cleanAnswer.length >= 2 && cleanAnswer.all { it.isLetter() }
                }
            }
        } catch (e: Exception) {
            false
        }
    }
    
    private fun generateAIQuestion(
        level: Int,
        difficulty: QuestionDifficulty,
        category: String?,
        index: Int
    ): AIQuestion {
        val questionId = "ai_${difficulty.name.lowercase()}_${level}_$index"
        
        val (question, answer, hints) = when (difficulty) {
            QuestionDifficulty.EASY -> generateEasyQuestion(category)
            QuestionDifficulty.MEDIUM -> generateMediumQuestion(category)
            QuestionDifficulty.HARD -> generateHardQuestion(category)
        }
        
        return AIQuestion(
            id = questionId,
            question = question,
            answer = answer,
            category = category ?: "عام",
            difficulty = difficulty,
            hints = hints
        )
    }
    
    private fun generateEasyQuestion(category: String?): Triple<String, String, List<String>> {
        val questions = when (category?.lowercase()) {
            "حيوانات" -> listOf(
                Triple("حيوان يبدأ بحرف الألف", "أسد", listOf("ملك الغابة", "قوي"))
            )
            "فواكه" -> listOf(
                Triple("فاكهة صفراء", "موز", listOf("طويل", "أصفر"))
            )
            "ألوان" -> listOf(
                Triple("لون الدم", "أحمر", listOf("لون النار", "لون الورد"))
            )
            else -> listOf(
                Triple("اسم يبدأ بحرف الباء", "باب", listOf("في البيت", "يُفتح ويُغلق"))
            )
        }
        
        return questions.random()
    }
    
    private fun generateMediumQuestion(category: String?): Triple<String, String, List<String>> {
        val questions = when (category?.lowercase()) {
            "جغرافيا" -> listOf(
                Triple("أطول نهر في العالم", "النيل", listOf("في أفريقيا", "يمر بمصر"))
            )
            "أدب" -> listOf(
                Triple("شاعر المتنبي لقب بـ", "أبو الطيب", listOf("شاعر عربي", "مشهور"))
            )
            "تاريخ" -> listOf(
                Triple("أول خليفة في الإسلام", "أبو بكر", listOf("صديق النبي", "خليفة"))
            )
            else -> listOf(
                Triple("عاصمة سوريا", "دمشق", listOf("مدينة", "عربية", "قديمة"))
            )
        }
        
        return questions.random()
    }
    
    private fun generateHardQuestion(category: String?): Triple<String, String, List<String>> {
        val questions = when (category?.lowercase()) {
            "علوم" -> listOf(
                Triple("مخترع الصفر في الرياضيات", "الخوارزمي", listOf("عالم", "عربي", "رياضيات"))
            )
            "طب" -> listOf(
                Triple("كتاب القانون في الطب لـ", "ابن سينا", listOf("طبيب", "فيلسوف", "عربي"))
            )
            "فلك" -> listOf(
                Triple("أول من قال بدوران الأرض", "البيروني", listOf("عالم", "فلك", "عربي"))
            )
            else -> listOf(
                Triple("أول جامعة في العالم", "الأزهر", listOf("في مصر", "قديمة", "مشهورة"))
            )
        }
        
        return questions.random()
    }
    
    private fun generateAdditionalHint(category: String): String {
        return when (category.lowercase()) {
            "حيوانات" -> "حيوان أليف"
            "فواكه" -> "تؤكل نيئة"
            "ألوان" -> "لون أساسي"
            "جغرافيا" -> "في الوطن العربي"
            "أدب" -> "من العصر العباسي"
            else -> "شيء معروف"
        }
    }
    
    fun setAvailability(available: Boolean) {
        isAvailable = available
    }
}