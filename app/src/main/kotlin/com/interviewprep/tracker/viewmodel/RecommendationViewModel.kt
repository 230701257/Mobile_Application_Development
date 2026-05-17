package com.interviewprep.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.interviewprep.tracker.data.predefined.RoleMappings
import com.interviewprep.tracker.data.remote.SkillRepository
import com.interviewprep.tracker.model.RoleRecommendation
import com.interviewprep.tracker.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _recommendationsState = MutableStateFlow<UiState<List<RoleRecommendation>>>(UiState.Loading)
    val recommendationsState: StateFlow<UiState<List<RoleRecommendation>>> = _recommendationsState.asStateFlow()

    private val _selectedRole = MutableStateFlow<RoleRecommendation?>(null)
    val selectedRole: StateFlow<RoleRecommendation?> = _selectedRole.asStateFlow()

    init { loadRecommendations() }

    fun loadRecommendations() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            skillRepository.getSkillsFlow(userId)
                .onStart { _recommendationsState.value = UiState.Loading }
                .catch { e -> _recommendationsState.value = UiState.Error(e.message ?: "Error") }
                .collect { skills ->
                    val recommendations = RoleMappings.getRecommendations(skills.map { it.skillName })
                    _recommendationsState.value = if (recommendations.isEmpty()) UiState.Empty
                    else UiState.Success(recommendations)
                    _selectedRole.value = recommendations.firstOrNull()
                }
        }
    }

    fun selectRole(role: RoleRecommendation) { _selectedRole.value = role }
}
