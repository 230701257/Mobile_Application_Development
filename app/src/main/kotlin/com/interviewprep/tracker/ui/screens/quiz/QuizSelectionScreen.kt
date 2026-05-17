package com.interviewprep.tracker.ui.screens.quiz

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
import com.interviewprep.tracker.model.QuizDifficulty
import com.interviewprep.tracker.model.QuizTopic
import com.interviewprep.tracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSelectionScreen(onStartQuiz: (topic: String, difficulty: String) -> Unit) {
    var selectedTopic by remember { mutableStateOf<QuizTopic?>(null) }
    var selectedDifficulty by remember { mutableStateOf<QuizDifficulty?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Quiz Center", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Header banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(
                        Modifier.background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(BrandPrimary, Color(0xFF9C59D1))
                            )
                        ).fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Test Your Knowledge", style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Choose a topic and difficulty to begin",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f))
                            }
                            Text("🧠", style = MaterialTheme.typography.displaySmall)
                        }
                    }
                }
            }

            // Topics
            item {
                Text("Select Topic", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
            }

            items(QuizTopic.values().toList().chunked(2)) { rowTopics ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowTopics.forEach { topic ->
                        TopicCard(
                            topic = topic,
                            isSelected = selectedTopic == topic,
                            onClick = { selectedTopic = topic },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowTopics.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            // Difficulty
            item {
                Spacer(Modifier.height(4.dp))
                Text("Select Difficulty", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuizDifficulty.values().forEach { diff ->
                        DifficultyCard(
                            difficulty = diff,
                            isSelected = selectedDifficulty == diff,
                            onClick = { selectedDifficulty = diff },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Start button
            item {
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        selectedTopic?.let { t ->
                            selectedDifficulty?.let { d ->
                                onStartQuiz(t.name, d.name)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = selectedTopic != null && selectedDifficulty != null,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (selectedTopic != null && selectedDifficulty != null)
                            "Start ${selectedTopic!!.label} Quiz"
                        else "Select Topic & Difficulty",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TopicCard(
    topic: QuizTopic,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = getTopicColor(topic)
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(color), width = 2.dp
        ) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape)
                    .background(color.copy(alpha = if (isSelected) 0.3f else 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(getTopicEmoji(topic), style = MaterialTheme.typography.titleLarge)
            }
            Text(topic.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun DifficultyCard(
    difficulty: QuizDifficulty,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (color, emoji) = when (difficulty) {
        QuizDifficulty.EASY -> BrandSecondary to "🟢"
        QuizDifficulty.MEDIUM -> BrandAccent to "🟡"
        QuizDifficulty.HARD -> BrandError to "🔴"
    }
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(color), width = 2.dp
        ) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, style = MaterialTheme.typography.titleLarge)
            Text(difficulty.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface)
        }
    }
}

private fun getTopicColor(topic: QuizTopic): Color = when (topic) {
    QuizTopic.KOTLIN -> KotlinColor
    QuizTopic.JAVA -> JavaColor
    QuizTopic.PYTHON -> PythonColor
    QuizTopic.DBMS -> DBMSColor
    QuizTopic.OOPS -> OOPSColor
    QuizTopic.OS -> OSColor
    QuizTopic.CN -> CNColor
    QuizTopic.APTITUDE -> AptitudeColor
    QuizTopic.DSA -> DSAColor
}

private fun getTopicEmoji(topic: QuizTopic): String = when (topic) {
    QuizTopic.KOTLIN -> "🎯"
    QuizTopic.JAVA -> "☕"
    QuizTopic.PYTHON -> "🐍"
    QuizTopic.DBMS -> "🗄️"
    QuizTopic.OOPS -> "🧩"
    QuizTopic.OS -> "💻"
    QuizTopic.CN -> "🌐"
    QuizTopic.APTITUDE -> "🧮"
    QuizTopic.DSA -> "🌳"
}
