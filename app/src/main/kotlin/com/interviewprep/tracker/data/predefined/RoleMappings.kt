package com.interviewprep.tracker.data.predefined

import com.interviewprep.tracker.model.RoleRecommendation

object RoleMappings {

    data class RoleDefinition(
        val roleName: String,
        val coreSkills: List<String>,       // must match
        val bonusSkills: List<String>,      // nice to have
        val allRequiredSkills: List<String>,// full requirement list
        val icon: String,
        val color: Long
    )

    private val roles = listOf(
        RoleDefinition(
            roleName = "Android Developer",
            coreSkills = listOf("kotlin", "java", "android"),
            bonusSkills = listOf("jetpack compose", "firebase", "mvvm", "xml", "gradle", "retrofit", "room"),
            allRequiredSkills = listOf("Kotlin", "Java", "Jetpack Compose", "Firebase", "MVVM", "Android SDK", "Room Database", "Retrofit", "Coroutines"),
            icon = "📱",
            color = 0xFF6C63FF
        ),
        RoleDefinition(
            roleName = "Flutter Developer",
            coreSkills = listOf("flutter", "dart"),
            bonusSkills = listOf("firebase", "rest api", "state management", "bloc", "provider"),
            allRequiredSkills = listOf("Flutter", "Dart", "Firebase", "REST APIs", "State Management (Bloc/Provider)", "Git"),
            icon = "🦋",
            color = 0xFF54B9D1
        ),
        RoleDefinition(
            roleName = "Data Scientist",
            coreSkills = listOf("python", "sql"),
            bonusSkills = listOf("machine learning", "data analysis", "pandas", "numpy", "tensorflow", "pytorch", "r", "statistics"),
            allRequiredSkills = listOf("Python", "SQL", "Machine Learning", "Pandas", "NumPy", "TensorFlow/PyTorch", "Statistics", "Data Visualization"),
            icon = "📊",
            color = 0xFF43D9AD
        ),
        RoleDefinition(
            roleName = "Frontend Developer",
            coreSkills = listOf("html", "css", "javascript"),
            bonusSkills = listOf("react", "vue", "typescript", "tailwind", "redux", "sass", "webpack"),
            allRequiredSkills = listOf("HTML", "CSS", "JavaScript", "React/Vue", "TypeScript", "Tailwind CSS", "RESTful APIs", "Git"),
            icon = "🎨",
            color = 0xFFF7931E
        ),
        RoleDefinition(
            roleName = "Backend Developer",
            coreSkills = listOf("java", "spring"),
            bonusSkills = listOf("sql", "rest api", "microservices", "docker", "aws", "mongodb", "hibernate"),
            allRequiredSkills = listOf("Java", "Spring Boot", "SQL", "REST APIs", "Microservices", "Docker", "AWS", "Hibernate"),
            icon = "⚙️",
            color = 0xFFFF6B6B
        ),
        RoleDefinition(
            roleName = "Full Stack Developer",
            coreSkills = listOf("javascript", "nodejs"),
            bonusSkills = listOf("react", "express", "mongodb", "sql", "html", "css", "typescript"),
            allRequiredSkills = listOf("JavaScript", "Node.js", "React", "Express", "MongoDB", "SQL", "TypeScript", "REST APIs"),
            icon = "🔄",
            color = 0xFF9C59D1
        ),
        RoleDefinition(
            roleName = "DevOps Engineer",
            coreSkills = listOf("docker", "linux"),
            bonusSkills = listOf("kubernetes", "aws", "azure", "jenkins", "terraform", "bash", "python", "ci/cd"),
            allRequiredSkills = listOf("Docker", "Kubernetes", "Linux", "AWS/Azure", "Jenkins", "Terraform", "Bash Scripting", "CI/CD"),
            icon = "🚀",
            color = 0xFFFF9A3C
        ),
        RoleDefinition(
            roleName = "Machine Learning Engineer",
            coreSkills = listOf("python", "machine learning"),
            bonusSkills = listOf("tensorflow", "pytorch", "deep learning", "nlp", "sql", "scala", "spark"),
            allRequiredSkills = listOf("Python", "TensorFlow/PyTorch", "Machine Learning", "Deep Learning", "NLP", "SQL", "Spark"),
            icon = "🤖",
            color = 0xFF4ECDC4
        )
    )

    fun getRecommendations(userSkills: List<String>): List<RoleRecommendation> {
        val normalizedSkills = userSkills.map { it.lowercase().trim() }

        return roles.map { role ->
            val coreMatches = role.coreSkills.count { core ->
                normalizedSkills.any { it.contains(core) || core.contains(it) }
            }
            val bonusMatches = role.bonusSkills.count { bonus ->
                normalizedSkills.any { it.contains(bonus) || bonus.contains(it) }
            }

            val coreScore = (coreMatches.toFloat() / role.coreSkills.size) * 60
            val bonusScore = if (role.bonusSkills.isNotEmpty())
                (bonusMatches.toFloat() / role.bonusSkills.size) * 40 else 0f
            val matchPercentage = (coreScore + bonusScore).toInt().coerceIn(0, 100)

            val matchReasons = buildList {
                role.coreSkills.forEach { core ->
                    val matched = normalizedSkills.find { it.contains(core) || core.contains(it) }
                    if (matched != null) add("You have $core skills")
                }
                role.bonusSkills.take(3).forEach { bonus ->
                    val matched = normalizedSkills.find { it.contains(bonus) || bonus.contains(it) }
                    if (matched != null) add("Experience with $bonus")
                }
            }

            val userSkillsLower = normalizedSkills
            val missingSkills = role.allRequiredSkills.filter { required ->
                val req = required.lowercase()
                userSkillsLower.none { it.contains(req) || req.contains(it) }
            }

            RoleRecommendation(
                roleName = role.roleName,
                matchPercentage = matchPercentage,
                matchReasons = matchReasons.ifEmpty { listOf("Add more skills to see match reasons") },
                missingSkills = missingSkills.take(5),
                icon = role.icon,
                color = role.color
            )
        }.filter { it.matchPercentage > 0 }
            .sortedByDescending { it.matchPercentage }
            .take(5)
    }

    fun getTopRecommendation(userSkills: List<String>): RoleRecommendation? {
        return getRecommendations(userSkills).firstOrNull()
    }
}
