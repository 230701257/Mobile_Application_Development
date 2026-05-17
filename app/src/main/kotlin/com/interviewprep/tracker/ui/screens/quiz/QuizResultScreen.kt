package com.interviewprep.tracker.ui.screens.quiz

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.interviewprep.tracker.ui.theme.BrandError
import com.interviewprep.tracker.ui.theme.BrandPrimary
import com.interviewprep.tracker.ui.theme.BrandSecondary
import com.interviewprep.tracker.ui.theme.BrandAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    score: Int,
    total: Int,
    topic: String,
    difficulty: String,
    onRetakeQuiz: () -> Unit,
    onGoHome: () -> Unit
) {
    val percentage = if (total > 0) (score * 100) / total else 0
    val wrong = total - score

    val (grade, gradeColor, emoji, message) = when {
        percentage >= 90 -> Quadruple("A+", BrandSecondary, "🏆", "Outstanding! You're a master!")
        percentage >= 80 -> Quadruple("A", BrandSecondary, "🎉", "Excellent work! Keep it up!")
        percentage >= 70 -> Quadruple("B", BrandPrimary, "👍", "Good job! A bit more practice needed.")
        percentage >= 60 -> Quadruple("C", BrandAccent, "📚", "Fair attempt. Review the weak areas.")
        percentage >= 50 -> Quadruple("D", BrandAccent, "💪", "Keep practicing! You'll improve.")
        else -> Quadruple("F", BrandError, "😅", "Don't give up! Practice makes perfect.")
    }

    // Animate progress circle
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(percentage / 100f, tween(1200))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Results", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onGoHome) {
                        Icon(Icons.Filled.Home, "Home")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Score circle card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(gradeColor.copy(0.15f), Color.Transparent)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(emoji, fontSize = 48.sp)

                        // Circular progress
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { animatedProgress.value },
                                modifier = Modifier.size(140.dp),
                                strokeWidth = 12.dp,
                                color = gradeColor,
                                trackColor = gradeColor.copy(alpha = 0.15f),
                                strokeCap = StrokeCap.Round
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "$percentage%",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = gradeColor
                                )
                                Text(
                                    "Grade $grade",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Text(
                            message,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            "${topic.replace("_", " ")} · $difficulty",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultStatCard(
                    label = "Correct",
                    value = score.toString(),
                    icon = Icons.Filled.CheckCircle,
                    color = BrandSecondary,
                    modifier = Modifier.weight(1f)
                )
                ResultStatCard(
                    label = "Wrong",
                    value = wrong.toString(),
                    icon = Icons.Filled.Cancel,
                    color = BrandError,
                    modifier = Modifier.weight(1f)
                )
                ResultStatCard(
                    label = "Total",
                    value = total.toString(),
                    icon = Icons.Filled.Quiz,
                    color = BrandPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Performance breakdown
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Performance Analysis",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    PerformanceRow("Accuracy", "$percentage%",
                        progress = percentage / 100f, color = gradeColor)
                    PerformanceRow("Correct Answers", "$score / $total",
                        progress = if (total > 0) score.toFloat() / total else 0f, color = BrandSecondary)
                }
            }

            // Feedback card
            val feedbackItems = when {
                percentage >= 80 -> listOf(
                    "🌟 Excellent grasp of the concepts!",
                    "📈 You're ready for advanced topics",
                    "💡 Try a harder difficulty next"
                )
                percentage >= 60 -> listOf(
                    "📚 Review the incorrect answers",
                    "🔄 Practice similar questions",
                    "💪 You're making progress!"
                )
                else -> listOf(
                    "📖 Go through the study material again",
                    "🎯 Focus on fundamentals first",
                    "⏰ Take your time, don't rush"
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "💬 Suggestions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    feedbackItems.forEach { item ->
                        Text(item, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRetakeQuiz,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Retake Quiz", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onGoHome,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Home, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Go to Dashboard")
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ResultStatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun PerformanceRow(label: String, value: String, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold, color = color)
        }
        val animP = remember { Animatable(0f) }
        LaunchedEffect(progress) { animP.animateTo(progress, tween(800)) }
        LinearProgressIndicator(
            progress = { animP.value },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
            color = color,
            trackColor = color.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round
        )
    }
}

// Utility data class for destructuring
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component1() = first
private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component2() = second
private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component3() = third
private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component4() = fourth
