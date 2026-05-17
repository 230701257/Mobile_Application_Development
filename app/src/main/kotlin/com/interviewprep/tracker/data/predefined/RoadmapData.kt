package com.interviewprep.tracker.data.predefined

import com.interviewprep.tracker.model.Roadmap
import com.interviewprep.tracker.model.RoadmapStep

object RoadmapData {

    private val androidDeveloperRoadmap = Roadmap(
        role = "Android Developer",
        description = "Become a professional Android Developer with Kotlin and Jetpack Compose",
        totalWeeks = 24,
        steps = listOf(
            RoadmapStep(1, "Learn Kotlin Fundamentals",
                "Master Kotlin basics: variables, functions, classes, null safety, lambdas, and extension functions.",
                listOf("Kotlin Official Docs", "Kotlin Koans", "Udemy Kotlin Course"), 3),
            RoadmapStep(2, "Learn Object-Oriented Programming",
                "Understand OOP: inheritance, polymorphism, abstraction, encapsulation, interfaces, and abstract classes.",
                listOf("OOP in Kotlin book", "YouTube - OOP concepts"), 2),
            RoadmapStep(3, "Android Basics",
                "Learn Android fundamentals: Activities, Fragments, Intents, Lifecycle, Resources, and Manifest.",
                listOf("Android Developers Guide", "Google Codelabs", "Udacity Android Basics"), 3),
            RoadmapStep(4, "Jetpack Compose",
                "Learn declarative UI with Compose: Composables, State, Side effects, Navigation, Theming, and Animations.",
                listOf("Jetpack Compose Docs", "Compose Pathway on Google", "Compose Samples"), 4),
            RoadmapStep(5, "Architecture Patterns",
                "Learn MVVM, Repository Pattern, LiveData, ViewModel, StateFlow, and Clean Architecture principles.",
                listOf("Android Architecture Guide", "Now In Android sample app"), 2),
            RoadmapStep(6, "Dependency Injection with Hilt",
                "Master Hilt/Dagger for DI: modules, components, scopes, and testing with DI.",
                listOf("Hilt Official Docs", "Dagger-Hilt Codelab"), 2),
            RoadmapStep(7, "Firebase Integration",
                "Learn Firebase: Authentication, Firestore, Realtime Database, Storage, and Cloud Messaging.",
                listOf("Firebase Docs", "Firebase Android Codelab"), 2),
            RoadmapStep(8, "Networking & REST APIs",
                "Integrate REST APIs with Retrofit, OkHttp, JSON parsing, and error handling.",
                listOf("Retrofit Docs", "JSON placeholder practice"), 2),
            RoadmapStep(9, "Local Data Persistence",
                "Learn Room Database, DataStore Preferences, and SQLite for offline data storage.",
                listOf("Room Database Codelab", "DataStore Docs"), 1),
            RoadmapStep(10, "Data Structures & Algorithms",
                "Practice DSA: Arrays, LinkedLists, Trees, Graphs, Sorting, Searching, and Dynamic Programming.",
                listOf("LeetCode", "GeeksForGeeks DSA", "Cracking the Coding Interview"), 4),
            RoadmapStep(11, "Build Portfolio Projects",
                "Build 3–5 real-world apps demonstrating your skills. Host on GitHub with proper documentation.",
                listOf("GitHub", "Play Store publishing guide"), 3),
            RoadmapStep(12, "Interview Preparation",
                "Practice coding problems, behavioral questions, system design, and mock interviews.",
                listOf("InterviewBit", "LeetCode", "Pramp for mock interviews"), 2)
        )
    )

    private val flutterDeveloperRoadmap = Roadmap(
        role = "Flutter Developer",
        description = "Become a cross-platform mobile developer with Flutter and Dart",
        totalWeeks = 20,
        steps = listOf(
            RoadmapStep(1, "Learn Dart Programming",
                "Master Dart: types, functions, classes, async/await, streams, and null safety.",
                listOf("Dart Official Docs", "DartPad"), 2),
            RoadmapStep(2, "Flutter Basics",
                "Learn Flutter widgets, layouts, navigation, and the widget lifecycle.",
                listOf("Flutter Official Docs", "Flutter Codelabs"), 3),
            RoadmapStep(3, "State Management",
                "Learn state management: setState, Provider, Riverpod, and Bloc pattern.",
                listOf("Bloc Library Docs", "Riverpod Docs"), 3),
            RoadmapStep(4, "Firebase Integration",
                "Integrate Firebase: Auth, Firestore, Storage, and push notifications.",
                listOf("Firebase Flutter Docs", "FlutterFire"), 2),
            RoadmapStep(5, "REST API Integration",
                "Consume REST APIs with Dio/http package, JSON serialization, and error handling.",
                listOf("Dio Package", "json_serializable"), 2),
            RoadmapStep(6, "Animations & UI Polish",
                "Learn implicit/explicit animations, custom painters, and UI performance optimization.",
                listOf("Flutter Animation Docs", "Rive for Flutter"), 2),
            RoadmapStep(7, "Platform Channels & Plugins",
                "Learn platform channels for native code access and explore popular Flutter packages.",
                listOf("pub.dev", "Platform Channels Docs"), 2),
            RoadmapStep(8, "Testing & Deployment",
                "Write unit, widget, and integration tests. Deploy to Play Store and App Store.",
                listOf("Flutter Testing Docs", "Store deployment guides"), 2),
            RoadmapStep(9, "Build Projects & Portfolio",
                "Build cross-platform apps and publish on GitHub and app stores.",
                listOf("GitHub", "Play Store Console"), 2)
        )
    )

    private val dataScientistRoadmap = Roadmap(
        role = "Data Scientist",
        description = "Become a Data Scientist with Python, ML, and statistical analysis skills",
        totalWeeks = 28,
        steps = listOf(
            RoadmapStep(1, "Python Programming",
                "Learn Python: syntax, data structures, functions, OOP, file I/O, and libraries.",
                listOf("Python Official Docs", "Automate the Boring Stuff", "Real Python"), 3),
            RoadmapStep(2, "Mathematics & Statistics",
                "Study linear algebra, calculus, probability, and statistics fundamentals.",
                listOf("Khan Academy Math", "StatQuest YouTube", "3Blue1Brown"), 4),
            RoadmapStep(3, "Data Manipulation",
                "Master Pandas, NumPy for data cleaning, transformation, and exploration.",
                listOf("Pandas Docs", "NumPy Docs", "Kaggle Learn"), 3),
            RoadmapStep(4, "Data Visualization",
                "Learn Matplotlib, Seaborn, Plotly for effective data visualization.",
                listOf("Matplotlib Docs", "Seaborn Gallery", "Plotly Docs"), 2),
            RoadmapStep(5, "SQL & Databases",
                "Master SQL: queries, joins, aggregations, window functions, and database design.",
                listOf("Mode SQL Tutorial", "W3Schools SQL", "LeetCode SQL"), 2),
            RoadmapStep(6, "Machine Learning Basics",
                "Learn supervised/unsupervised learning: regression, classification, clustering with Scikit-learn.",
                listOf("Scikit-learn Docs", "Hands-On ML with Scikit-Learn book", "Kaggle"), 4),
            RoadmapStep(7, "Deep Learning",
                "Study neural networks, CNNs, RNNs with TensorFlow/Keras and PyTorch.",
                listOf("Deep Learning Specialization (Coursera)", "Fast.ai", "TensorFlow Docs"), 4),
            RoadmapStep(8, "Feature Engineering & Model Tuning",
                "Learn feature selection, engineering, cross-validation, hyperparameter tuning.",
                listOf("Kaggle competitions", "Feature Engineering for ML book"), 3),
            RoadmapStep(9, "Projects & Portfolio",
                "Build end-to-end ML projects, participate in Kaggle competitions.",
                listOf("Kaggle", "GitHub", "Towards Data Science"), 3)
        )
    )

    private val frontendDeveloperRoadmap = Roadmap(
        role = "Frontend Developer",
        description = "Become a modern Frontend Developer with React and TypeScript",
        totalWeeks = 22,
        steps = listOf(
            RoadmapStep(1, "HTML & CSS Fundamentals",
                "Master HTML semantics, CSS layouts (Flexbox, Grid), responsive design.",
                listOf("MDN Web Docs", "CSS Tricks", "freeCodeCamp"), 3),
            RoadmapStep(2, "JavaScript Fundamentals",
                "Learn JS: variables, functions, DOM, events, async/await, fetch API, ES6+ features.",
                listOf("JavaScript.info", "Eloquent JavaScript", "MDN"), 4),
            RoadmapStep(3, "React.js",
                "Learn React: components, hooks, state management, Context API, and React Router.",
                listOf("React Docs", "Full Stack Open", "Scrimba React"), 4),
            RoadmapStep(4, "TypeScript",
                "Add TypeScript: types, interfaces, generics, and TypeScript with React.",
                listOf("TypeScript Docs", "TypeScript Deep Dive book"), 2),
            RoadmapStep(5, "CSS Frameworks",
                "Learn Tailwind CSS, styled-components, and CSS modules.",
                listOf("Tailwind CSS Docs", "styled-components Docs"), 2),
            RoadmapStep(6, "State Management",
                "Learn Redux Toolkit, Zustand, or Recoil for complex state management.",
                listOf("Redux Docs", "Zustand GitHub"), 2),
            RoadmapStep(7, "Testing",
                "Write unit and integration tests with Jest, React Testing Library.",
                listOf("Jest Docs", "Testing Library Docs"), 2),
            RoadmapStep(8, "Performance & Optimization",
                "Learn lazy loading, code splitting, memoization, and Web Vitals optimization.",
                listOf("web.dev", "React Performance Docs"), 2),
            RoadmapStep(9, "Build Tools & Deployment",
                "Learn Vite/Webpack, npm/yarn, Git, CI/CD, and deploy to Vercel/Netlify.",
                listOf("Vite Docs", "Vercel", "GitHub Actions"), 1)
        )
    )

    private val backendDeveloperRoadmap = Roadmap(
        role = "Backend Developer",
        description = "Become a Backend Developer with Java, Spring Boot and microservices",
        totalWeeks = 26,
        steps = listOf(
            RoadmapStep(1, "Java Programming",
                "Master Java: OOP, Collections, Generics, Exception handling, Java 8+ features.",
                listOf("Oracle Java Docs", "Effective Java book", "Baeldung"), 4),
            RoadmapStep(2, "Spring Framework",
                "Learn Spring Core: IoC, DI, Bean lifecycle, AOP.",
                listOf("Spring Docs", "Spring in Action book"), 3),
            RoadmapStep(3, "Spring Boot",
                "Build REST APIs with Spring Boot: controllers, services, repositories, JPA.",
                listOf("Spring Boot Docs", "Spring Boot Guides"), 4),
            RoadmapStep(4, "Databases",
                "Learn SQL (PostgreSQL/MySQL), JPA/Hibernate, and NoSQL (MongoDB).",
                listOf("PostgreSQL Docs", "Hibernate Docs", "MongoDB University"), 3),
            RoadmapStep(5, "API Design",
                "Design RESTful APIs: versioning, authentication, documentation with Swagger.",
                listOf("REST API Design book", "Swagger Docs"), 2),
            RoadmapStep(6, "Security",
                "Implement Spring Security: JWT, OAuth2, HTTPS, and input validation.",
                listOf("Spring Security Docs", "OWASP Top 10"), 2),
            RoadmapStep(7, "Microservices",
                "Learn microservices architecture, service discovery, API gateway, Docker.",
                listOf("Microservices Patterns book", "Docker Docs"), 3),
            RoadmapStep(8, "Cloud & DevOps",
                "Deploy to AWS/GCP, learn CI/CD with Jenkins/GitHub Actions, Kubernetes basics.",
                listOf("AWS Free Tier", "GitHub Actions Docs"), 3),
            RoadmapStep(9, "Testing & Best Practices",
                "Write unit and integration tests with JUnit, Mockito. Practice clean code.",
                listOf("JUnit 5 Docs", "Mockito Docs", "Clean Code book"), 2)
        )
    )

    private val allRoadmaps = mapOf(
        "Android Developer" to androidDeveloperRoadmap,
        "Flutter Developer" to flutterDeveloperRoadmap,
        "Data Scientist" to dataScientistRoadmap,
        "Frontend Developer" to frontendDeveloperRoadmap,
        "Backend Developer" to backendDeveloperRoadmap
    )

    fun getRoadmapForRole(role: String): Roadmap? {
        return allRoadmaps.entries.find { (key, _) ->
            key.equals(role, ignoreCase = true) || role.contains(key, ignoreCase = true)
        }?.value
    }

    fun getAllRoles(): List<String> = allRoadmaps.keys.toList()

    fun getAllRoadmaps(): List<Roadmap> = allRoadmaps.values.toList()
}
