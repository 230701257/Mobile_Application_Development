package com.interviewprep.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interviewprep.tracker.model.AuthState
import com.interviewprep.tracker.navigation.AppNavGraph
import com.interviewprep.tracker.ui.theme.InterviewPrepTrackerTheme
import com.interviewprep.tracker.viewmodel.AuthViewModel
import com.interviewprep.tracker.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var keepSplash = true
        splashScreen.setKeepOnScreenCondition { keepSplash }

        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.authState.collectAsStateWithLifecycle()

            // Keep splash until auth state resolves
            LaunchedEffect(authState) {
                if (authState !is AuthState.Loading) {
                    keepSplash = false
                }
            }

            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }

            // Load persisted theme preference
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            val storedDark by dashboardViewModel.isDarkMode.collectAsStateWithLifecycle(initialValue = systemDark)
            LaunchedEffect(storedDark) { isDarkTheme = storedDark }

            InterviewPrepTrackerTheme(darkTheme = isDarkTheme) {
                if (authState !is AuthState.Loading) {
                    AppNavGraph(
                        isLoggedIn = authState is AuthState.Authenticated,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = {
                            isDarkTheme = !isDarkTheme
                            dashboardViewModel.toggleDarkMode(isDarkTheme)
                        }
                    )
                }
            }
        }
    }
}
