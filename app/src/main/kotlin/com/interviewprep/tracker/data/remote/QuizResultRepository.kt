package com.interviewprep.tracker.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.interviewprep.tracker.model.QuizResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizResultRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun userResultsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("quizResults")

    fun getResultsFlow(userId: String): Flow<List<QuizResult>> = callbackFlow {
        val listener = userResultsCollection(userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val results = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        QuizResult(
                            id = doc.id,
                            userId = doc.getString("userId") ?: userId,
                            topic = doc.getString("topic") ?: "",
                            difficulty = doc.getString("difficulty") ?: "",
                            score = (doc.getLong("score") ?: 0L).toInt(),
                            totalQuestions = (doc.getLong("totalQuestions") ?: 0L).toInt(),
                            percentage = (doc.getLong("percentage") ?: 0L).toInt(),
                            timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(results)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveResult(result: QuizResult): Result<String> {
        return try {
            val docRef = userResultsCollection(result.userId).document()
            val withId = result.copy(id = docRef.id)
            docRef.set(withId.toMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getResultsOnce(userId: String): Result<List<QuizResult>> {
        return try {
            val snapshot = userResultsCollection(userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get().await()
            val results = snapshot.documents.mapNotNull { doc ->
                try {
                    QuizResult(
                        id = doc.id,
                        userId = doc.getString("userId") ?: userId,
                        topic = doc.getString("topic") ?: "",
                        difficulty = doc.getString("difficulty") ?: "",
                        score = (doc.getLong("score") ?: 0L).toInt(),
                        totalQuestions = (doc.getLong("totalQuestions") ?: 0L).toInt(),
                        percentage = (doc.getLong("percentage") ?: 0L).toInt(),
                        timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                    )
                } catch (e: Exception) { null }
            }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
