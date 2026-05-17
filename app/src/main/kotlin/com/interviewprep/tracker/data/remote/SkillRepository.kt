package com.interviewprep.tracker.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.interviewprep.tracker.model.Skill
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkillRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun userSkillsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("skills")

    fun getSkillsFlow(userId: String): Flow<List<Skill>> = callbackFlow {
        val listener = userSkillsCollection(userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val skills = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Skill(
                            id = doc.id,
                            userId = doc.getString("userId") ?: userId,
                            skillName = doc.getString("skillName") ?: "",
                            proficiencyLevel = doc.getString("proficiencyLevel") ?: "BEGINNER",
                            timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(skills)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addSkill(skill: Skill): Result<String> {
        return try {
            val docRef = userSkillsCollection(skill.userId).document()
            val skillWithId = skill.copy(id = docRef.id)
            docRef.set(skillWithId.toMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSkill(skill: Skill): Result<Unit> {
        return try {
            userSkillsCollection(skill.userId)
                .document(skill.id)
                .set(skill.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSkill(userId: String, skillId: String): Result<Unit> {
        return try {
            userSkillsCollection(userId).document(skillId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSkillsOnce(userId: String): Result<List<Skill>> {
        return try {
            val snapshot = userSkillsCollection(userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()
            val skills = snapshot.documents.mapNotNull { doc ->
                try {
                    Skill(
                        id = doc.id,
                        userId = doc.getString("userId") ?: userId,
                        skillName = doc.getString("skillName") ?: "",
                        proficiencyLevel = doc.getString("proficiencyLevel") ?: "BEGINNER",
                        timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                    )
                } catch (e: Exception) { null }
            }
            Result.success(skills)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
