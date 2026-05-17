package com.interviewprep.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.interviewprep.tracker.data.predefined.RoadmapData
import com.interviewprep.tracker.data.predefined.RoleMappings
import com.interviewprep.tracker.data.remote.SkillRepository
import com.interviewprep.tracker.model.Roadmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _selectedRoadmap = MutableStateFlow<Roadmap?>(null)
    val selectedRoadmap: StateFlow<Roadmap?> = _selectedRoadmap.asStateFlow()

    private val _allRoles = MutableStateFlow(RoadmapData.getAllRoles())
    val allRoles: StateFlow<List<String>> = _allRoles.asStateFlow()

    private val _selectedRole = MutableStateFlow(RoadmapData.getAllRoles().firstOrNull() ?: "")
    val selectedRole: StateFlow<String> = _selectedRole.asStateFlow()

    private val _expandedSteps = MutableStateFlow<Set<Int>>(emptySet())
    val expandedSteps: StateFlow<Set<Int>> = _expandedSteps.asStateFlow()

    init { loadRecommendedRoadmap() }

    private fun loadRecommendedRoadmap() {
        val userId = firebaseAuth.currentUser?.uid ?: run {
            selectRole(RoadmapData.getAllRoles().first())
            return
        }
        viewModelScope.launch {
            skillRepository.getSkillsFlow(userId).collect { skills ->
                val topRole = RoleMappings.getTopRecommendation(skills.map { it.skillName })
                val role = topRole?.roleName ?: RoadmapData.getAllRoles().first()
                selectRole(role)
            }
        }
    }

    fun selectRole(role: String) {
        _selectedRole.value = role
        _selectedRoadmap.value = RoadmapData.getRoadmapForRole(role)
        _expandedSteps.value = emptySet()
    }

    fun toggleStep(stepId: Int) {
        _expandedSteps.value = if (_expandedSteps.value.contains(stepId)) {
            _expandedSteps.value - stepId
        } else {
            _expandedSteps.value + stepId
        }
    }
}
