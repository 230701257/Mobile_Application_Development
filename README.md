# 🎯 Interview Prep Tracker

A **professional, hackathon-ready Android app** built with Kotlin + Jetpack Compose to help developers prepare for technical interviews.

---

## 📱 Screenshots & Features

### Core Features
| Feature | Description |
|---|---|
| 🔐 Authentication | Firebase Email/Password login & registration |
| ⭐ Skills Tracker | Add, edit, delete skills with proficiency levels |
| 💡 Role Recommendations | AI-free predefined role matching logic |
| 🗺️ Learning Roadmaps | Detailed roadmaps for 5 career paths |
| 🧠 MCQ Quiz Engine | 80+ questions across 9 topics, 3 difficulty levels |
| 📊 Dashboard | Charts, streaks, stats & recent activity |
| 🌗 Dark/Light Theme | Full Material 3 theming |

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Repository Pattern
- **DI**: Hilt
- **Backend**: Firebase Auth + Firestore
- **Navigation**: Navigation Compose
- **Async**: Coroutines + Flow
- **Persistence**: DataStore Preferences

---

## 🚀 Setup Instructions

### Step 1: Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (e.g., `interview-prep-tracker`)
3. Register Android app with package: `com.interviewprep.tracker`
4. Download `google-services.json`
5. Replace `app/google-services.json` with your downloaded file

### Step 2: Enable Firebase Services

In Firebase Console:
- **Authentication** → Sign-in method → Enable **Email/Password**
- **Firestore Database** → Create database → Start in **test mode**

### Step 3: Firestore Security Rules (Production)

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### Step 4: Open in Android Studio

1. Open Android Studio (Hedgehog or newer)
2. File → Open → Select `InterviewPrepTracker` folder
3. Wait for Gradle sync to complete
4. Replace `app/google-services.json` with your file
5. Run on emulator or device (API 26+)

---

## 📂 Project Structure

```
app/src/main/kotlin/com/interviewprep/tracker/
├── data/
│   ├── local/
│   │   └── UserPreferencesRepository.kt   # DataStore prefs
│   ├── remote/
│   │   ├── AuthRepository.kt              # Firebase Auth
│   │   ├── SkillRepository.kt             # Firestore skills
│   │   └── QuizResultRepository.kt        # Firestore results
│   └── predefined/
│       ├── RoleMappings.kt                # Role matching logic
│       ├── RoadmapData.kt                 # Hardcoded roadmaps
│       └── QuizData.kt                    # MCQ bank (80+ Qs)
├── model/
│   └── Models.kt                          # All data models
├── ui/
│   ├── screens/
│   │   ├── auth/                          # Login, Register
│   │   ├── dashboard/                     # Main dashboard
│   │   ├── skills/                        # Skills CRUD
│   │   ├── quiz/                          # Quiz, Selection, Result
│   │   ├── recommendation/                # Role cards
│   │   └── roadmap/                       # Timeline roadmap
│   ├── components/
│   │   └── CommonComponents.kt            # Reusable composables
│   └── theme/
│       ├── Color.kt
│       ├── Typography.kt
│       └── Theme.kt
├── navigation/
│   └── NavGraph.kt                        # Navigation routes
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── SkillsViewModel.kt
│   ├── QuizViewModel.kt
│   ├── DashboardViewModel.kt
│   ├── RecommendationViewModel.kt
│   └── RoadmapViewModel.kt
├── di/
│   └── AppModule.kt                       # Hilt modules
├── utils/
│   └── Utils.kt                           # Extensions & helpers
├── InterviewPrepApp.kt                    # Hilt Application
└── MainActivity.kt                        # Entry point
```

---

## 🧠 Quiz Topics & Questions

| Topic | Questions |
|---|---|
| Kotlin | 12 questions (Easy/Medium/Hard) |
| Java | 10 questions |
| Python | 8 questions |
| DBMS | 8 questions |
| OOPs | 8 questions |
| Operating Systems | 7 questions |
| Computer Networks | 7 questions |
| Aptitude | 8 questions |
| DSA | 10 questions |

---

## 💼 Role Recommendation Engine

Predefined skill-to-role mapping (no AI):

| Skills | Recommended Role |
|---|---|
| Kotlin + Java + Android | Android Developer |
| Flutter + Dart | Flutter Developer |
| Python + SQL | Data Scientist |
| HTML + CSS + JavaScript | Frontend Developer |
| Java + Spring | Backend Developer |
| JavaScript + Node.js | Full Stack Developer |
| Docker + Linux | DevOps Engineer |
| Python + Machine Learning | ML Engineer |

---

## 🗺️ Learning Roadmaps

- **Android Developer** — 12 steps, ~24 weeks
- **Flutter Developer** — 9 steps, ~20 weeks
- **Data Scientist** — 9 steps, ~28 weeks
- **Frontend Developer** — 9 steps, ~22 weeks
- **Backend Developer** — 9 steps, ~26 weeks

---

## ✅ Verification Checklist

- [x] Compiles with no errors
- [x] Firebase Auth (login/register/logout)
- [x] Skills CRUD with Firestore sync
- [x] Role recommendations (8 roles)
- [x] Roadmaps with expandable timeline (5 paths)
- [x] MCQ Quiz with timer (10 questions/session)
- [x] Answer reveal + explanation on selection
- [x] Score saved to Firestore
- [x] Dashboard with stats & charts
- [x] Dark/Light theme toggle
- [x] Pull to refresh on dashboard
- [x] Swipe to delete skills
- [x] Search skills
- [x] Bottom navigation
- [x] Splash screen
- [x] Loading/empty/error states
- [x] Streak tracking
- [x] Session persistence via DataStore

---

## 📋 Firestore Data Schema

```
users/
  {userId}/
    skills/
      {skillId}/
        - id: String
        - userId: String
        - skillName: String
        - proficiencyLevel: String (BEGINNER/INTERMEDIATE/ADVANCED/EXPERT)
        - timestamp: Timestamp
    quizResults/
      {resultId}/
        - id: String
        - userId: String
        - topic: String
        - difficulty: String
        - score: Int
        - totalQuestions: Int
        - percentage: Int
        - timestamp: Timestamp
```

---

## 👨‍💻 Built With

- Kotlin 2.0 + Compose BOM 2024
- Firebase BOM 33
- Hilt 2.52
- Navigation Compose 2.8
- DataStore 1.1
- Material 3
- Vico Charts

---

*Built as a portfolio-grade, hackathon-ready Android application.*
