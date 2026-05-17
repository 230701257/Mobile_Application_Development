package com.interviewprep.tracker.ui.screens.recommendation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interviewprep.tracker.model.RoleRecommendation
import com.interviewprep.tracker.model.UiState
import com.interviewprep.tracker.ui.components.AnimatedProgressBar
import com.interviewprep.tracker.ui.components.EmptyStateView
import com.interviewprep.tracker.ui.components.LoadingScreen
import com.interviewprep.tracker.ui.theme.BrandPrimary
import com.interviewprep.tracker.viewmodel.RecommendationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    onNavigateToRoadmap: () -> Unit,
    viewModel: RecommendationViewModel = hiltViewModel()
) {
    val state by viewModel.recommendationsState.collectAsStateWithLifecycle()
    val selectedRole by viewModel.selectedRole.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Role Insights", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadRecommendations() }) {
                        Icon(Icons.Filled.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is UiState.Loading -> LoadingScreen("Analyzing your skills...")
            is UiState.Empty -> EmptyStateView(
                icon = Icons.Filled.Work,
                title = "No Skills Found",
                subtitle = "Add skills in the Skills tab to see role recommendations",
                action = null
            )
            is UiState.Error -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(s.message, color = MaterialTheme.colorScheme.error)
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top recommendation highlight
                    item {
                        s.data.firstOrNull()?.let { top ->
                            TopRecommendationBanner(top, onNavigateToRoadmap)
                        }
                    }

                    item {
                        Text("All Matched Roles",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                        Text("${s.data.size} roles match your skill set",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }

                    items(s.data) { role ->
                        val isSelected = selectedRole?.roleName == role.roleName
                        RoleCard(
                            role = role,
                            isExpanded = isSelected,
                            onClick = { viewModel.selectRole(role) }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TopRecommendationBanner(role: RoleRecommendation, onRoadmapClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(Color(role.color), Color(role.color).copy(alpha = 0.6f))
                )
            ).fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(role.icon, style = MaterialTheme.typography.displaySmall)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Best Match", style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f))
                        Text(role.roleName, style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Surface(
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("${role.matchPercentage}%",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White)
                    }
                }

                Spacer(Modifier.height(16.dp))
                AnimatedProgressBar(
                    progress = role.matchPercentage / 100f,
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    height = 6
                )
                Spacer(Modifier.height(12.dp))

                role.matchReasons.take(2).forEach { reason ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(reason, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                    }
                    Spacer(Modifier.height(4.dp))
                }

                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onRoadmapClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color.White)
                    )
                ) {
                    Icon(Icons.Filled.Map, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("View Roadmap")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoleCard(
    role: RoleRecommendation,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 2.dp),
        border = if (isExpanded) CardDefaults.outlinedCardBorder() else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(role.icon, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(role.roleName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    AnimatedProgressBar(progress = role.matchPercentage / 100f, color = Color(role.color), height = 6)
                    Text("${role.matchPercentage}% match",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(role.color))
                }
                Icon(
                    if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            AnimatedVisibility(visible = isExpanded, enter = fadeIn() + expandVertically()) {
                Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HorizontalDivider()

                    if (role.matchReasons.isNotEmpty()) {
                        Text("Why you match:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                        role.matchReasons.forEach { reason ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, null,
                                    tint = Color(0xFF43D9AD), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(reason, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    if (role.missingSkills.isNotEmpty()) {
                        Text("Skills to add:", style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            role.missingSkills.forEach { skill ->
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(skill,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
