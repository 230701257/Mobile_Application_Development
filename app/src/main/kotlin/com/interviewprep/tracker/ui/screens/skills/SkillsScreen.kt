package com.interviewprep.tracker.ui.screens.skills

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interviewprep.tracker.model.ProficiencyLevel
import com.interviewprep.tracker.model.Skill
import com.interviewprep.tracker.model.UiState
import com.interviewprep.tracker.ui.components.EmptyStateView
import com.interviewprep.tracker.ui.components.LoadingScreen
import com.interviewprep.tracker.ui.components.ProficiencyChip
import com.interviewprep.tracker.ui.components.SnackbarHost
import com.interviewprep.tracker.ui.theme.*
import com.interviewprep.tracker.viewmodel.SkillsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SkillsScreen(viewModel: SkillsViewModel = hiltViewModel()) {
    val skillsState by viewModel.skillsState.collectAsStateWithLifecycle()
    val operationState by viewModel.operationState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSkill by remember { mutableStateOf<Skill?>(null) }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Skills", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Filled.Add, "Add skill")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = BrandPrimary
                ) {
                    Icon(Icons.Filled.Add, "Add skill", tint = Color.White)
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search skills...") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Filled.Clear, null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(16.dp))

                when (val state = skillsState) {
                    is UiState.Loading -> LoadingScreen()
                    is UiState.Empty -> EmptyStateView(
                        icon = Icons.Filled.Star,
                        title = "No Skills Yet",
                        subtitle = "Add your programming skills to get role recommendations",
                        action = {
                            Button(onClick = { showAddDialog = true }) {
                                Icon(Icons.Filled.Add, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Add Your First Skill")
                            }
                        }
                    )
                    is UiState.Error -> ErrorView(state.message)
                    is UiState.Success -> {
                        val filtered = viewModel.getFilteredSkills(state.data)
                        SkillList(
                            skills = filtered,
                            onEdit = { editingSkill = it },
                            onDelete = { viewModel.deleteSkill(it.id) }
                        )
                    }
                }
            }
        }

        SnackbarHost(message = operationState, onDismiss = { viewModel.clearOperationState() })
    }

    // Add Dialog
    if (showAddDialog) {
        SkillDialog(
            title = "Add Skill",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, proficiency ->
                viewModel.addSkill(name, proficiency)
                showAddDialog = false
            }
        )
    }

    // Edit Dialog
    editingSkill?.let { skill ->
        SkillDialog(
            title = "Edit Skill",
            initialName = skill.skillName,
            initialProficiency = skill.proficiency,
            onDismiss = { editingSkill = null },
            onConfirm = { name, proficiency ->
                viewModel.updateSkill(skill, name, proficiency)
                editingSkill = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun SkillList(
    skills: List<Skill>,
    onEdit: (Skill) -> Unit,
    onDelete: (Skill) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(skills, key = { it.id }) { skill ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        onDelete(skill)
                        true
                    } else false
                }
            )
            SwipeToDismissBox(
                state = dismissState,
                modifier = Modifier.animateItemPlacement(),
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandError),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 20.dp)
                        )
                    }
                }
            ) {
                SkillCard(skill = skill, onEdit = { onEdit(skill) })
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun SkillCard(skill: Skill, onEdit: () -> Unit) {
    val proficiencyColor = when (skill.proficiency) {
        ProficiencyLevel.BEGINNER -> Color(0xFF43D9AD)
        ProficiencyLevel.INTERMEDIATE -> BrandAccent
        ProficiencyLevel.ADVANCED -> BrandPrimary
        ProficiencyLevel.EXPERT -> Color(0xFF9C59D1)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(proficiencyColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    skill.skillName.take(2).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = proficiencyColor
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(skill.skillName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                ProficiencyChip(skill.proficiency.label, proficiencyColor)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, "Edit", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun SkillDialog(
    title: String,
    initialName: String = "",
    initialProficiency: ProficiencyLevel = ProficiencyLevel.BEGINNER,
    onDismiss: () -> Unit,
    onConfirm: (String, ProficiencyLevel) -> Unit
) {
    var skillName by remember { mutableStateOf(initialName) }
    var selectedProficiency by remember { mutableStateOf(initialProficiency) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = skillName,
                    onValueChange = { skillName = it },
                    label = { Text("Skill Name") },
                    placeholder = { Text("e.g. Kotlin, Python, React") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Text("Proficiency Level", style = MaterialTheme.typography.labelLarge)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ProficiencyLevel.values().forEach { level ->
                        val selected = selectedProficiency == level
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = { selectedProficiency = level }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(level.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (skillName.isNotBlank()) onConfirm(skillName, selectedProficiency) },
                enabled = skillName.isNotBlank()
            ) {
                Text(if (initialName.isEmpty()) "Add Skill" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun ErrorView(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Error, null, tint = BrandError, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
