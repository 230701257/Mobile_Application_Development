package com.interviewprep.tracker.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interviewprep.tracker.model.QuizResult
import com.interviewprep.tracker.ui.components.AnimatedProgressBar
import com.interviewprep.tracker.ui.components.SectionHeader
import com.interviewprep.tracker.ui.components.StatCard
import com.interviewprep.tracker.ui.theme.*
import com.interviewprep.tracker.viewmodel.DashboardViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToSkills: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToRoadmap: () -> Unit,
    onNavigateToRecommendation: () -> Unit,
    onSignOut: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val data by viewModel.dashboardData.collectAsStateWithLifecycle()
    var showMenu by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Interview Prep", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Track your progress", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode, "Toggle theme")
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, "Menu")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Sign Out") },
                                leadingIcon = { Icon(Icons.Filled.Logout, null) },
                                onClick = { showMenu = false; onSignOut() }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.loadDashboard()
                    delay(1000)
                    isRefreshing = false
                }
            },
            modifier = Modifier.padding(padding)
        ) {
            if (data.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Greeting card
                    item {
                        AnimatedVisibility(visible = true, enter = fadeIn(tween(300)) + slideInVertically { -50 }) {
                            GreetingBanner(
                                streak = data.streak,
                                totalQuizzes = data.totalQuizzes,
                                averageScore = data.averageScore
                            )
                        }
                    }

                    // Stats row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Skills",
                                value = data.skills.size.toString(),
                                icon = Icons.Filled.Star,
                                tint = BrandPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Quizzes",
                                value = data.totalQuizzes.toString(),
                                icon = Icons.Filled.Quiz,
                                tint = BrandSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Avg Score",
                                value = "${data.averageScore}%",
                                icon = Icons.Filled.BarChart,
                                tint = BrandAccent,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Recommended role
                    if (data.topRecommendation != null) {
                        item {
                            SectionHeader(title = "Recommended Role", subtitle = "Based on your skills")
                            Spacer(Modifier.height(8.dp))
                            RecommendedRoleCard(
                                role = data.topRecommendation!!,
                                onClick = onNavigateToRecommendation
                            )
                        }
                    }

                    // Quick actions
                    item {
                        SectionHeader(title = "Quick Actions")
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            item {
                                QuickActionCard("Add Skills", "📝", BrandPrimary, onNavigateToSkills)
                            }
                            item {
                                QuickActionCard("Take Quiz", "🧠", BrandSecondary, onNavigateToQuiz)
                            }
                            item {
                                QuickActionCard("Roadmap", "🗺️", BrandAccent, onNavigateToRoadmap)
                            }
                            item {
                                QuickActionCard("Insights", "📊", Color(0xFF9C59D1), onNavigateToRecommendation)
                            }
                        }
                    }

                    // Topic performance
                    if (data.topicBreakdown.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Topic Performance", subtitle = "Average scores by topic")
                            Spacer(Modifier.height(8.dp))
                            TopicPerformanceCard(data.topicBreakdown)
                        }
                    }

                    // Recent activity
                    if (data.recentResults.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Recent Activity", subtitle = "Your latest quiz results")
                        }
                        items(data.recentResults.take(5)) { result ->
                            RecentResultCard(result)
                        }
                    }

                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun GreetingBanner(streak: Int, totalQuizzes: Int, averageScore: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(listOf(BrandPrimary, Color(0xFF9C59D1)))
            ).fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            getGreeting(),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            "Keep up the momentum!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    if (streak > 0) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🔥", style = MaterialTheme.typography.headlineMedium)
                                Text("$streak day streak",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                if (totalQuizzes > 0) {
                    Text(
                        "You've completed $totalQuizzes quizzes with ${averageScore}% average score",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                } else {
                    Text(
                        "Take your first quiz to see your progress here!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendedRoleCard(
    role: com.interviewprep.tracker.model.RoleRecommendation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(role.icon, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(role.roleName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${role.matchPercentage}% match",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(role.color))
                }
                Icon(Icons.Filled.ChevronRight, null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            AnimatedProgressBar(
                progress = role.matchPercentage / 100f,
                color = Color(role.color)
            )
            if (role.missingSkills.isNotEmpty()) {
                Text("Missing: ${role.missingSkills.take(3).joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun QuickActionCard(label: String, emoji: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, style = MaterialTheme.typography.titleLarge)
            }
            Text(label, style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun TopicPerformanceCard(topicBreakdown: Map<String, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            topicBreakdown.entries.sortedByDescending { it.value }.take(5).forEach { (topic, score) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(topic, modifier = Modifier.width(100.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium)
                    AnimatedProgressBar(
                        progress = score / 100f,
                        modifier = Modifier.weight(1f),
                        color = getTopicColor(topic)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("$score%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RecentResultCard(result: QuizResult) {
    val percentage = result.percentage
    val color = when {
        percentage >= 80 -> BrandSecondary
        percentage >= 60 -> BrandAccent
        else -> BrandError
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("$percentage%", style = MaterialTheme.typography.labelMedium,
                    color = color, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(result.topic.replace("_", " "),
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("${result.difficulty} · ${result.score}/${result.totalQuestions} correct",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Text(
                formatDate(result.timestamp.seconds * 1000),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

private fun getGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good Morning! ☀️"
        in 12..16 -> "Good Afternoon! 🌤️"
        in 17..20 -> "Good Evening! 🌅"
        else -> "Good Night! 🌙"
    }
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun getTopicColor(topic: String): Color {
    return when (topic.uppercase()) {
        "KOTLIN" -> KotlinColor
        "JAVA" -> JavaColor
        "PYTHON" -> PythonColor
        "DBMS" -> DBMSColor
        "OOPS" -> OOPSColor
        "OS" -> OSColor
        "CN" -> CNColor
        "APTITUDE" -> AptitudeColor
        "DSA" -> DSAColor
        else -> BrandPrimary
    }
}
