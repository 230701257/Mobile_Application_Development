package com.interviewprep.tracker.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.interviewprep.tracker.ui.screens.auth.LoginScreen
import com.interviewprep.tracker.ui.screens.auth.RegisterScreen
import com.interviewprep.tracker.ui.screens.dashboard.DashboardScreen
import com.interviewprep.tracker.ui.screens.quiz.QuizScreen
import com.interviewprep.tracker.ui.screens.quiz.QuizSelectionScreen
import com.interviewprep.tracker.ui.screens.quiz.QuizResultScreen
import com.interviewprep.tracker.ui.screens.recommendation.RecommendationScreen
import com.interviewprep.tracker.ui.screens.roadmap.RoadmapScreen
import com.interviewprep.tracker.ui.screens.skills.SkillsScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Skills : Screen("skills")
    object Recommendation : Screen("recommendation")
    object Roadmap : Screen("roadmap")
    object QuizSelection : Screen("quiz_selection")
    object Quiz : Screen("quiz/{topic}/{difficulty}") {
        fun createRoute(topic: String, difficulty: String) = "quiz/$topic/$difficulty"
    }
    object QuizResult : Screen("quiz_result/{score}/{total}/{topic}/{difficulty}") {
        fun createRoute(score: Int, total: Int, topic: String, difficulty: String) =
            "quiz_result/$score/$total/$topic/$difficulty"
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Dashboard", Icons.Filled.Dashboard, Screen.Dashboard.route),
    BottomNavItem("Skills", Icons.Filled.Star, Screen.Skills.route),
    BottomNavItem("Quiz", Icons.Filled.Quiz, Screen.QuizSelection.route),
    BottomNavItem("Roadmap", Icons.Filled.Map, Screen.Roadmap.route),
    BottomNavItem("Insights", Icons.Filled.BarChart, Screen.Recommendation.route),
)

val bottomNavRoutes = bottomNavItems.map { it.route }

@Composable
fun AppNavGraph(
    isLoggedIn: Boolean,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(250)) },
            exitTransition = { fadeOut(tween(250)) },
            popEnterTransition = { fadeIn(tween(250)) },
            popExitTransition = { fadeOut(tween(250)) }
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToSkills = { navController.navigate(Screen.Skills.route) },
                    onNavigateToQuiz = { navController.navigate(Screen.QuizSelection.route) },
                    onNavigateToRoadmap = { navController.navigate(Screen.Roadmap.route) },
                    onNavigateToRecommendation = { navController.navigate(Screen.Recommendation.route) },
                    onSignOut = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
            composable(Screen.Skills.route) {
                SkillsScreen()
            }
            composable(Screen.Recommendation.route) {
                RecommendationScreen(
                    onNavigateToRoadmap = { navController.navigate(Screen.Roadmap.route) }
                )
            }
            composable(Screen.Roadmap.route) {
                RoadmapScreen()
            }
            composable(Screen.QuizSelection.route) {
                QuizSelectionScreen(
                    onStartQuiz = { topic, difficulty ->
                        navController.navigate(Screen.Quiz.createRoute(topic, difficulty))
                    }
                )
            }
            composable(Screen.Quiz.route) { backStackEntry ->
                val topic = backStackEntry.arguments?.getString("topic") ?: ""
                val difficulty = backStackEntry.arguments?.getString("difficulty") ?: ""
                QuizScreen(
                    topic = topic,
                    difficulty = difficulty,
                    onQuizComplete = { score, total ->
                        navController.navigate(
                            Screen.QuizResult.createRoute(score, total, topic, difficulty)
                        ) { popUpTo(Screen.QuizSelection.route) }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.QuizResult.route) { backStackEntry ->
                val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
                val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0
                val topic = backStackEntry.arguments?.getString("topic") ?: ""
                val difficulty = backStackEntry.arguments?.getString("difficulty") ?: ""
                QuizResultScreen(
                    score = score,
                    total = total,
                    topic = topic,
                    difficulty = difficulty,
                    onRetakeQuiz = {
                        navController.navigate(Screen.Quiz.createRoute(topic, difficulty)) {
                            popUpTo(Screen.QuizSelection.route)
                        }
                    },
                    onGoHome = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.QuizSelection.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AppBottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                alwaysShowLabel = true
            )
        }
    }
}
