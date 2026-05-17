package com.interviewprep.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.interviewprep.tracker.data.local.UserPreferencesRepository
import com.interviewprep.tracker.data.predefined.RoleMappings
import com.interviewprep.tracker.data.remote.QuizResultRepository
import com.interviewprep.tracker.data.remote.SkillRepository
import com.interviewprep.tracker.model.QuizResult
import com.interviewprep.tracker.model.RoleRecommendation
import com.interviewprep.tracker.model.Skill
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class DashboardData(
    val skills: List<Skill> = emptyList(),
    val recentResults: List<QuizResult> = emptyList(),
    val topRecommendation: RoleRecommendation? = null,
    val averageScore: Int = 0,
    val totalQuizzes: Int = 0,
    val streak: Int = 0,
    val topicBreakdown: Map<String, Int> = emptyMap(),
    val difficultyBreakdown: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val quizResultRepository: QuizResultRepository,
    private val prefsRepository: UserPreferencesRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _dashboardData = MutableStateFlow(DashboardData())
    val dashboardData: StateFlow<DashboardData> = _dashboardData.asStateFlow()

    val isDarkMode = prefsRepository.isDarkMode
    val streak = prefsRepository.quizStreak

    init { loadDashboard() }

    fun loadDashboard() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                combine(
                    skillRepository.getSkillsFlow(userId),
                    quizResultRepository.getResultsFlow(userId),
                    prefsRepository.quizStreak
                ) { skills, results, streak ->
                    val topRec = RoleMappings.getTopRecommendation(skills.map { it.skillName })
                    val avgScore = if (results.isEmpty()) 0
                    else results.map { it.percentage }.average().toInt()

                    val topicBreakdown = results
                        .groupBy { it.topic }
                        .mapValues { (_, v) -> v.map { it.percentage }.average().toInt() }

                    val difficultyBreakdown = results
                        .groupBy { it.difficulty }
                        .mapValues { (_, v) -> v.map { it.percentage }.average().toInt() }

                    // Calculate streak
                    val computedStreak = computeStreak(results)

                    DashboardData(
                        skills = skills,
                        recentResults = results.take(10),
                        topRecommendation = topRec,
                        averageScore = avgScore,
                        totalQuizzes = results.size,
                        streak = computedStreak,
                        topicBreakdown = topicBreakdown,
                        difficultyBreakdown = difficultyBreakdown,
                        isLoading = false
                    )
                }.collect { data ->
                    _dashboardData.value = data
                    data.streak.let { prefsRepository.updateQuizStreak(it) }
                }
            } catch (e: Exception) {
                _dashboardData.value = _dashboardData.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard"
                )
            }
        }
    }

    private fun computeStreak(results: List<QuizResult>): Int {
        if (results.isEmpty()) return 0
        val sorted = results.sortedByDescending { it.timestamp.seconds }
        var streak = 0
        var checkDay = Calendar.getInstance()
        checkDay.set(Calendar.HOUR_OF_DAY, 0)
        checkDay.set(Calendar.MINUTE, 0)
        checkDay.set(Calendar.SECOND, 0)
        checkDay.set(Calendar.MILLISECOND, 0)

        for (result in sorted) {
            val resultCal = Calendar.getInstance()
            resultCal.timeInMillis = result.timestamp.seconds * 1000
            resultCal.set(Calendar.HOUR_OF_DAY, 0)
            resultCal.set(Calendar.MINUTE, 0)
            resultCal.set(Calendar.SECOND, 0)
            resultCal.set(Calendar.MILLISECOND, 0)

            when {
                resultCal.timeInMillis == checkDay.timeInMillis -> {
                    streak++
                    checkDay.add(Calendar.DAY_OF_YEAR, -1)
                }
                resultCal.timeInMillis > checkDay.timeInMillis -> { /* same day, skip */ }
                else -> break
            }
        }
        return streak
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setDarkMode(enabled) }
    }
}
