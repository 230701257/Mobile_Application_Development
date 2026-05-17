package com.interviewprep.tracker.ui.screens.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interviewprep.tracker.model.QuizQuestion
import com.interviewprep.tracker.model.QuizSession
import com.interviewprep.tracker.ui.theme.BrandError
import com.interviewprep.tracker.ui.theme.BrandPrimary
import com.interviewprep.tracker.ui.theme.BrandSecondary
import com.interviewprep.tracker.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    topic: String,
    difficulty: String,
    onQuizComplete: (score: Int, total: Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val selectedAnswer by viewModel.selectedAnswerIndex.collectAsStateWithLifecycle()
    val showExplanation by viewModel.showExplanation.collectAsStateWithLifecycle()
    val timerSeconds by viewModel.timerSeconds.collectAsStateWithLifecycle()

    LaunchedEffect(topic, difficulty) {
        viewModel.startQuiz(topic, difficulty)
    }

    LaunchedEffect(session) {
        val s = session
        if (s != null && s.isCompleted) {
            onQuizComplete(s.score, s.questions.size)
        }
    }

    val currentSession = session
    if (currentSession == null || currentSession.questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Loading questions...")
            }
        }
        return
    }

    val currentQuestion = currentSession.questions[currentSession.currentIndex]
    val progress = (currentSession.currentIndex + 1).toFloat() / currentSession.questions.size
    val isLastQuestion = currentSession.currentIndex == currentSession.questions.size - 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            topic.replace("_", " "),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            difficulty,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.resetQuiz(); onNavigateBack() }) {
                        Icon(Icons.Filled.Close, "Close")
                    }
                },
                actions = {
                    // Timer
                    TimerDisplay(seconds = timerSeconds, hasAnswered = selectedAnswer != null)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Progress section
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Question ${currentSession.currentIndex + 1} of ${currentSession.questions.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        "Score: ${currentSession.score}",
                        style = MaterialTheme.typography.labelMedium,
                        color = BrandPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(6.dp))
                val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(400), label = "")
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                    color = BrandPrimary,
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(Modifier.height(8.dp))

            // Question card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DifficultyBadge(currentQuestion.difficulty.label)
                        TopicBadge(currentQuestion.topic.label)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        currentQuestion.question,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Options
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                currentQuestion.options.forEachIndexed { index, option ->
                    OptionCard(
                        index = index,
                        option = option,
                        selectedIndex = selectedAnswer,
                        correctIndex = currentQuestion.correctAnswerIndex,
                        showResult = showExplanation,
                        onClick = { viewModel.selectAnswer(index) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Explanation
            if (showExplanation) {
                ExplanationCard(
                    explanation = currentQuestion.explanation,
                    isCorrect = selectedAnswer == currentQuestion.correctAnswerIndex,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
            }

            // Next/Finish button
            if (showExplanation) {
                Button(
                    onClick = { viewModel.nextQuestion() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        if (isLastQuestion) "See Results" else "Next Question",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        if (isLastQuestion) Icons.Filled.EmojiEvents else Icons.Filled.ArrowForward,
                        null
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TimerDisplay(seconds: Int, hasAnswered: Boolean) {
    val color by animateColorAsState(
        targetValue = when {
            hasAnswered -> MaterialTheme.colorScheme.outline
            seconds > 15 -> BrandSecondary
            seconds > 8 -> BrandError.copy(alpha = 0.7f)
            else -> BrandError
        },
        label = "timer_color"
    )
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$seconds",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun OptionCard(
    index: Int,
    option: String,
    selectedIndex: Int?,
    correctIndex: Int,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val optionLabel = listOf("A", "B", "C", "D")[index]

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !showResult -> MaterialTheme.colorScheme.surface
            index == correctIndex -> BrandSecondary.copy(alpha = 0.15f)
            index == selectedIndex && selectedIndex != correctIndex -> BrandError.copy(alpha = 0.15f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300),
        label = "option_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !showResult && index == selectedIndex -> BrandPrimary
            !showResult -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            index == correctIndex -> BrandSecondary
            index == selectedIndex && selectedIndex != correctIndex -> BrandError
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        },
        animationSpec = tween(300),
        label = "option_border"
    )

    val labelBgColor by animateColorAsState(
        targetValue = when {
            !showResult && index == selectedIndex -> BrandPrimary
            !showResult -> MaterialTheme.colorScheme.surfaceVariant
            index == correctIndex -> BrandSecondary
            index == selectedIndex -> BrandError
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(300),
        label = "label_bg"
    )

    val isEnabled = selectedIndex == null && !showResult

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(
                width = if (index == selectedIndex || (showResult && index == correctIndex)) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(labelBgColor),
                contentAlignment = Alignment.Center
            ) {
                if (showResult && index == correctIndex) {
                    Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                } else if (showResult && index == selectedIndex && selectedIndex != correctIndex) {
                    Icon(Icons.Filled.Close, null, tint = Color.White, modifier = Modifier.size(18.dp))
                } else {
                    Text(
                        optionLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (index == selectedIndex) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                option,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ExplanationCard(
    explanation: String,
    isCorrect: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isCorrect) BrandSecondary.copy(alpha = 0.1f) else BrandError.copy(alpha = 0.1f)
    val borderColor = if (isCorrect) BrandSecondary else BrandError
    val icon = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel
    val title = if (isCorrect) "Correct! 🎉" else "Incorrect 😔"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(borderColor.copy(alpha = 0.5f))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = borderColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold, color = borderColor)
            }
            HorizontalDivider(color = borderColor.copy(alpha = 0.2f))
            Text(
                "💡 $explanation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun DifficultyBadge(label: String) {
    val color = when (label.uppercase()) {
        "EASY" -> BrandSecondary
        "MEDIUM" -> BrandError.copy(alpha = 0.7f)
        "HARD" -> BrandError
        else -> BrandPrimary
    }
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.15f)) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TopicBadge(label: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = BrandPrimary.copy(alpha = 0.1f)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = BrandPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
