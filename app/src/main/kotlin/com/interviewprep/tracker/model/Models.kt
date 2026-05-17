package com.interviewprep.tracker.model

import com.google.firebase.Timestamp

// ─── User ───────────────────────────────────────────────────────────────────

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

// ─── Skill ──────────────────────────────────────────────────────────────────

enum class ProficiencyLevel(val label: String, val value: Int) {
    BEGINNER("Beginner", 1),
    INTERMEDIATE("Intermediate", 2),
    ADVANCED("Advanced", 3),
    EXPERT("Expert", 4)
}

data class Skill(
    val id: String = "",
    val userId: String = "",
    val skillName: String = "",
    val proficiencyLevel: String = ProficiencyLevel.BEGINNER.name,
    val timestamp: Timestamp = Timestamp.now()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "userId" to userId,
        "skillName" to skillName,
        "proficiencyLevel" to proficiencyLevel,
        "timestamp" to timestamp
    )

    val proficiency: ProficiencyLevel
        get() = try { ProficiencyLevel.valueOf(proficiencyLevel) } catch (e: Exception) { ProficiencyLevel.BEGINNER }
}

// ─── Role Recommendation ────────────────────────────────────────────────────

data class RoleRecommendation(
    val roleName: String,
    val matchPercentage: Int,
    val matchReasons: List<String>,
    val missingSkills: List<String>,
    val icon: String,
    val color: Long
)

// ─── Roadmap ─────────────────────────────────────────────────────────────────

data class RoadmapStep(
    val id: Int,
    val title: String,
    val description: String,
    val resources: List<String>,
    val estimatedWeeks: Int,
    val isCompleted: Boolean = false
)

data class Roadmap(
    val role: String,
    val description: String,
    val totalWeeks: Int,
    val steps: List<RoadmapStep>
)

// ─── Quiz ─────────────────────────────────────────────────────────────────────

enum class QuizDifficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

enum class QuizTopic(val label: String) {
    KOTLIN("Kotlin"),
    JAVA("Java"),
    PYTHON("Python"),
    DBMS("DBMS"),
    OOPS("OOPs"),
    OS("Operating Systems"),
    CN("Computer Networks"),
    APTITUDE("Aptitude"),
    DSA("DSA")
}

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val difficulty: QuizDifficulty,
    val topic: QuizTopic
)

data class QuizSession(
    val topic: QuizTopic,
    val difficulty: QuizDifficulty,
    val questions: List<QuizQuestion>,
    val answers: MutableMap<Int, Int> = mutableMapOf(), // questionId -> selectedIndex
    var currentIndex: Int = 0,
    var isCompleted: Boolean = false
) {
    val score: Int get() = answers.count { (id, ans) ->
        questions.find { it.id == id }?.correctAnswerIndex == ans
    }
    val percentage: Int get() = if (questions.isEmpty()) 0 else (score * 100) / questions.size
    val correctCount: Int get() = score
    val wrongCount: Int get() = answers.size - score
}

// ─── Quiz Result (Firestore) ──────────────────────────────────────────────────

data class QuizResult(
    val id: String = "",
    val userId: String = "",
    val topic: String = "",
    val difficulty: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val percentage: Int = 0,
    val timestamp: Timestamp = Timestamp.now()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "userId" to userId,
        "topic" to topic,
        "difficulty" to difficulty,
        "score" to score,
        "totalQuestions" to totalQuestions,
        "percentage" to percentage,
        "timestamp" to timestamp
    )
}

// ─── UI State Wrappers ────────────────────────────────────────────────────────

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}
