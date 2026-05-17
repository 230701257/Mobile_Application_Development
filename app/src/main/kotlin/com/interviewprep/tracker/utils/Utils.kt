package com.interviewprep.tracker.utils

import java.text.SimpleDateFormat
import java.util.*

object Extensions {

    fun Long.toFormattedDate(pattern: String = "MMM dd, yyyy"): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
    }

    fun Long.toRelativeTime(): String {
        val now = System.currentTimeMillis()
        val diff = now - this
        return when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000}m ago"
            diff < 86_400_000 -> "${diff / 3_600_000}h ago"
            diff < 604_800_000 -> "${diff / 86_400_000}d ago"
            else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(this))
        }
    }

    fun Int.toOrdinal(): String {
        return when {
            this in 11..13 -> "${this}th"
            this % 10 == 1 -> "${this}st"
            this % 10 == 2 -> "${this}nd"
            this % 10 == 3 -> "${this}rd"
            else -> "${this}th"
        }
    }

    fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

    fun List<Int>.average(): Double = if (isEmpty()) 0.0 else sum().toDouble() / size
}

object Validators {
    fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean = password.length >= 6

    fun isValidSkillName(name: String): Boolean =
        name.trim().length in 1..50
}

object Constants {
    const val FIRESTORE_USERS = "users"
    const val FIRESTORE_SKILLS = "skills"
    const val FIRESTORE_QUIZ_RESULTS = "quizResults"
    const val MAX_SKILLS = 50
    const val QUIZ_QUESTIONS_PER_SESSION = 10
    const val QUIZ_TIMER_SECONDS = 30
}
