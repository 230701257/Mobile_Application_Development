package com.interviewprep.tracker.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.interviewprep.tracker.model.AuthState
import com.interviewprep.tracker.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    val authStateFlow: Flow<AuthState> = callbackFlow {
        trySend(AuthState.Loading)
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                trySend(AuthState.Authenticated(user.toUser()))
            } else {
                trySend(AuthState.Unauthenticated)
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    val currentUser: FirebaseUser? get() = firebaseAuth.currentUser

    val isLoggedIn: Boolean get() = firebaseAuth.currentUser != null

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Sign in failed: no user returned")
            Result.success(user.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, displayName: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Registration failed: no user returned")
            Result.success(user.toUser().copy(displayName = displayName))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toUser() = User(
        uid = uid,
        email = email ?: "",
        displayName = displayName ?: email?.substringBefore("@") ?: "User"
    )
}
