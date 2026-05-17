package com.interviewprep.tracker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "interview_prep_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val QUIZ_STREAK = intPreferencesKey("quiz_streak")
        val LAST_QUIZ_DATE = longPreferencesKey("last_quiz_date")
        val TOTAL_QUIZZES_TAKEN = intPreferencesKey("total_quizzes_taken")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val CACHED_ROLE = stringPreferencesKey("cached_recommended_role")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[IS_DARK_MODE] ?: false }

    val quizStreak: Flow<Int> = context.dataStore.data.map { it[QUIZ_STREAK] ?: 0 }

    val lastQuizDate: Flow<Long> = context.dataStore.data.map { it[LAST_QUIZ_DATE] ?: 0L }

    val totalQuizzesTaken: Flow<Int> = context.dataStore.data.map { it[TOTAL_QUIZZES_TAKEN] ?: 0 }

    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_DONE] ?: false }

    val cachedRole: Flow<String> = context.dataStore.data.map { it[CACHED_ROLE] ?: "" }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[IS_DARK_MODE] = enabled }
    }

    suspend fun updateQuizStreak(streak: Int) {
        context.dataStore.edit { it[QUIZ_STREAK] = streak }
    }

    suspend fun updateLastQuizDate(timestamp: Long) {
        context.dataStore.edit { it[LAST_QUIZ_DATE] = timestamp }
    }

    suspend fun incrementTotalQuizzes() {
        context.dataStore.edit { prefs ->
            prefs[TOTAL_QUIZZES_TAKEN] = (prefs[TOTAL_QUIZZES_TAKEN] ?: 0) + 1
        }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[ONBOARDING_DONE] = done }
    }

    suspend fun setCachedRole(role: String) {
        context.dataStore.edit { it[CACHED_ROLE] = role }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
