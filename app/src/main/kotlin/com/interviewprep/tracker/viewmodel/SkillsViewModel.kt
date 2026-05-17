package com.interviewprep.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.interviewprep.tracker.data.remote.SkillRepository
import com.interviewprep.tracker.model.ProficiencyLevel
import com.interviewprep.tracker.model.Skill
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
class SkillsViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _skillsState = MutableStateFlow<UiState<List<Skill>>>(UiState.Loading)
    val skillsState: StateFlow<UiState<List<Skill>>> = _skillsState.asStateFlow()

    private val _operationState = MutableStateFlow<String?>(null)
    val operationState: StateFlow<String?> = _operationState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadSkills()
    }

    private fun loadSkills() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            skillRepository.getSkillsFlow(userId)
                .onStart { _skillsState.value = UiState.Loading }
                .catch { e -> _skillsState.value = UiState.Error(e.message ?: "Failed to load skills") }
                .collect { skills ->
                    _skillsState.value = if (skills.isEmpty()) UiState.Empty else UiState.Success(skills)
                }
        }
    }

    fun addSkill(skillName: String, proficiency: ProficiencyLevel) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            val skill = Skill(
                userId = userId,
                skillName = skillName.trim(),
                proficiencyLevel = proficiency.name
            )
            val result = skillRepository.addSkill(skill)
            result.onSuccess { _operationState.value = "Skill added successfully!" }
            result.onFailure { _operationState.value = "Error: ${it.message}" }
        }
    }

    fun updateSkill(skill: Skill, newName: String, newProficiency: ProficiencyLevel) {
        viewModelScope.launch {
            val updated = skill.copy(
                skillName = newName.trim(),
                proficiencyLevel = newProficiency.name
            )
            val result = skillRepository.updateSkill(updated)
            result.onSuccess { _operationState.value = "Skill updated!" }
            result.onFailure { _operationState.value = "Error: ${it.message}" }
        }
    }

    fun deleteSkill(skillId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = skillRepository.deleteSkill(userId, skillId)
            result.onSuccess { _operationState.value = "Skill deleted" }
            result.onFailure { _operationState.value = "Error: ${it.message}" }
        }
    }

    fun updateSearchQuery(query: String) { _searchQuery.value = query }

    fun clearOperationState() { _operationState.value = null }

    fun getFilteredSkills(skills: List<Skill>): List<Skill> {
        val query = _searchQuery.value
        return if (query.isBlank()) skills
        else skills.filter { it.skillName.contains(query, ignoreCase = true) }
    }
}
