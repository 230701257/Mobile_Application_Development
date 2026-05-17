package com.interviewprep.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.interviewprep.tracker.data.local.UserPreferencesRepository
import com.interviewprep.tracker.data.predefined.QuizData
import com.interviewprep.tracker.data.remote.QuizResultRepository
import com.interviewprep.tracker.model.QuizDifficulty
import com.interviewprep.tracker.model.QuizResult
import com.interviewprep.tracker.model.QuizSession
import com.interviewprep.tracker.model.QuizTopic
import com.interviewprep.tracker.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizResultRepository: QuizResultRepository,
    private val prefsRepository: UserPreferencesRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _session = MutableStateFlow<QuizSession?>(null)
    val session: StateFlow<QuizSession?> = _session.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    private val _showExplanation = MutableStateFlow(false)
    val showExplanation: StateFlow<Boolean> = _showExplanation.asStateFlow()

    private val _timerSeconds = MutableStateFlow(30)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _resultsState = MutableStateFlow<UiState<List<QuizResult>>>(UiState.Loading)
    val resultsState: StateFlow<UiState<List<QuizResult>>> = _resultsState.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private var timerJob: Job? = null

    init { loadResults() }

    fun startQuiz(topicString: String, difficultyString: String) {
        val topic = QuizTopic.values().find { it.name == topicString } ?: QuizTopic.KOTLIN
        val difficulty = QuizDifficulty.values().find { it.name == difficultyString } ?: QuizDifficulty.EASY
        val questions = QuizData.getQuestions(topic, difficulty, 10)
        if (questions.isEmpty()) return
        _session.value = QuizSession(topic = topic, difficulty = difficulty, questions = questions)
        _selectedAnswerIndex.value = null
        _showExplanation.value = false
        startTimer()
    }

    fun selectAnswer(index: Int) {
        val session = _session.value ?: return
        if (_selectedAnswerIndex.value != null) return // Already answered
        timerJob?.cancel()
        _selectedAnswerIndex.value = index
        _showExplanation.value = true
        val currentQuestion = session.questions[session.currentIndex]
        session.answers[currentQuestion.id] = index
        _session.value = session.copy() // Trigger recompose
    }

    fun nextQuestion() {
        val session = _session.value ?: return
        if (session.currentIndex < session.questions.size - 1) {
            _session.value = session.copy(currentIndex = session.currentIndex + 1)
            _selectedAnswerIndex.value = null
            _showExplanation.value = false
            startTimer()
        } else {
            completeQuiz()
        }
    }

    private fun completeQuiz() {
        val session = _session.value ?: return
        _session.value = session.copy(isCompleted = true)
        timerJob?.cancel()
        saveResult(session)
    }

    private fun saveResult(session: QuizSession) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isSaving.value = true
            val result = QuizResult(
                userId = userId,
                topic = session.topic.name,
                difficulty = session.difficulty.name,
                score = session.score,
                totalQuestions = session.questions.size,
                percentage = session.percentage
            )
            quizResultRepository.saveResult(result)
            prefsRepository.incrementTotalQuizzes()
            prefsRepository.updateLastQuizDate(System.currentTimeMillis())
            _isSaving.value = false
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _timerSeconds.value = 30
        timerJob = viewModelScope.launch {
            while (_timerSeconds.value > 0) {
                delay(1000)
                _timerSeconds.value -= 1
            }
            // Time's up — auto-select wrong answer if not answered
            if (_selectedAnswerIndex.value == null) {
                selectAnswer(-1)
            }
        }
    }

    private fun loadResults() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            quizResultRepository.getResultsFlow(userId)
                .onStart { _resultsState.value = UiState.Loading }
                .catch { e -> _resultsState.value = UiState.Error(e.message ?: "Failed to load results") }
                .collect { results ->
                    _resultsState.value = if (results.isEmpty()) UiState.Empty else UiState.Success(results)
                }
        }
    }

    fun resetQuiz() {
        _session.value = null
        _selectedAnswerIndex.value = null
        _showExplanation.value = false
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
