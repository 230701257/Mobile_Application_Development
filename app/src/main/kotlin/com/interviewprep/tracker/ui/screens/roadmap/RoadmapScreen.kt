package com.interviewprep.tracker.ui.screens.roadmap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.interviewprep.tracker.model.RoadmapStep
import com.interviewprep.tracker.ui.theme.BrandPrimary
import com.interviewprep.tracker.ui.theme.BrandSecondary
import com.interviewprep.tracker.viewmodel.RoadmapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapScreen(viewModel: RoadmapViewModel = hiltViewModel()) {
    val selectedRoadmap by viewModel.selectedRoadmap.collectAsStateWithLifecycle()
    val allRoles by viewModel.allRoles.collectAsStateWithLifecycle()
    val selectedRole by viewModel.selectedRole.collectAsStateWithLifecycle()
    val expandedSteps by viewModel.expandedSteps.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learning Roadmap", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Role selector
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allRoles) { role ->
                    FilterChip(
                        selected = role == selectedRole,
                        onClick = { viewModel.selectRole(role) },
                        label = { Text(role, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            val roadmap = selectedRoadmap
            if (roadmap == null) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Header card
                    item {
                        RoadmapHeader(
                            role = roadmap.role,
                            description = roadmap.description,
                            totalWeeks = roadmap.totalWeeks,
                            stepsCount = roadmap.steps.size
                        )
                        Spacer(Modifier.height(24.dp))
                    }

                    // Timeline steps
                    itemsIndexed(roadmap.steps) { index, step ->
                        TimelineStep(
                            step = step,
                            isLast = index == roadmap.steps.size - 1,
                            isExpanded = expandedSteps.contains(step.id),
                            onToggle = { viewModel.toggleStep(step.id) }
                        )
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun RoadmapHeader(
    role: String,
    description: String,
    totalWeeks: Int,
    stepsCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(BrandPrimary, BrandSecondary)
                )
            ).fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("🗺️ $role", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f))
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RoadmapStat("$stepsCount Steps", Icons.Filled.FormatListNumbered)
                    RoadmapStat("~$totalWeeks Weeks", Icons.Filled.CalendarMonth)
                }
            }
        }
    }
}

@Composable
private fun RoadmapStat(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.9f))
    }
}

@Composable
private fun TimelineStep(
    step: RoadmapStep,
    isLast: Boolean,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    step.id.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (isExpanded) 200.dp else 40.dp)
                        .background(BrandPrimary.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Step card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 8.dp)
                .clickable(onClick = onToggle),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(step.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold)
                        Text("~${step.estimatedWeeks} week${if (step.estimatedWeeks > 1) "s" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandPrimary)
                    }
                    Icon(
                        if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
                        Text(step.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))

                        if (step.resources.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            Text("📚 Resources",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            step.resources.forEach { resource ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Link, null,
                                        modifier = Modifier.size(14.dp),
                                        tint = BrandPrimary)
                                    Spacer(Modifier.width(6.dp))
                                    Text(resource,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BrandPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
