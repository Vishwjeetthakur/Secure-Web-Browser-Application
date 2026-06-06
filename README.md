# Secure Web Browser

## Overview

Secure Web Browser is an Android application built using Kotlin and Jetpack Compose. The application provides a simple and secure browsing experience with Google Authentication, browsing history management, and welcome notifications. The project follows the MVVM architecture pattern and uses Room Database for local data storage.

---

# Project Setup Steps

### Prerequisites

* Android Studio
* JDK 11 or above
* Firebase Project
* Android Device or Emulator

### Installation

1. Clone the repository.

```bash
git clone <repository-url>
```

2. Open the project in Android Studio.

3. Create a Firebase project from the Firebase Console.

4. Register an Android application using:

```text
com.vishwajeet.securewebbrowserapplication
```

5. Download the `google-services.json` file and place it inside the app module.

```text
app/google-services.json
```

6. Add your Google Web Client ID to `local.properties`.

```properties
WEB_CLIENT_ID=your_web_client_id_here
```

7. Sync Gradle.

8. Build and run the application.

---

# Firebase Setup Explanation

Firebase Authentication is used to implement Google Sign-In functionality.

The setup process includes:

* Creating a Firebase project.
* Adding an Android application to Firebase.
* Enabling Google Authentication in Firebase Authentication.
* Downloading and configuring the `google-services.json` file.
* Generating and configuring SHA-1 fingerprints.
* Obtaining the Google Web Client ID.
* Storing the Web Client ID securely in `local.properties`.

This prevents sensitive configuration values from being pushed to GitHub.

---

# Architecture Explanation

The application follows the MVVM (Model-View-ViewModel) architecture.

### Model Layer

Contains:

* Room Database
* DAO interfaces
* Repository classes
* Entity classes

### ViewModel Layer

Responsible for:

* Managing UI state
* Business logic
* Authentication handling
* Browser history management
* Notification handling

### View Layer

Built using Jetpack Compose.

Responsibilities include:

* Displaying UI
* Collecting state from ViewModels
* Triggering user actions

### Data Flow

```text
UI (Compose)
      ↓
ViewModel
      ↓
Repository
      ↓
Room Database
```

This architecture improves maintainability, scalability, and separation of concerns.

---

# Database Schema Explanation

The application uses Room Database to store browsing history.

## HistoryEntity

| Field           | Type   | Description          |
| --------------- | ------ | -------------------- |
| id              | Int    | Primary Key          |
| url             | String | Website URL          |
| title           | String | Page Title           |
| visitCount      | Int    | Number of visits     |
| lastVisitedTime | Long   | Last visit timestamp |

### History Logic

When a website is opened:

* If the URL already exists:

  * Visit count is increased.
  * Last visited timestamp is updated.

* If the URL does not exist:

  * A new record is inserted.

This allows the browser to maintain a simple browsing history system.

---

# Notification Flow Explanation

The application shows a welcome notification when the user opens the app.

### Flow

1. Application starts.
2. Notification channel is created.
3. SharedPreferences checks whether the notification has already been shown today.
4. If already shown:

   * No notification is displayed.
5. If not shown:

   * A welcome notification is created.
   * The current date is stored.
   * The notification is displayed.

### Benefits

* Prevents notification spam.
* Shows only one notification per day.
* Provides a lightweight user engagement mechanism.

---

# WebView Lifecycle Handling Explanation

The browser functionality is implemented using Android WebView.

### WebView Initialization

The WebView is configured with:

* JavaScript enabled
* DOM Storage enabled
* Safe browsing support

### Lifecycle Handling

#### onResume()

Resumes WebView execution when the application returns to the foreground.

#### onPause()

Pauses WebView activity when the application moves to the background.

#### onDestroy()

Destroys WebView resources and prevents memory leaks.

### Navigation Handling

When the user presses the back button:

* If WebView can navigate back:

  * Previous page is loaded.
* Otherwise:

  * The screen is closed.

This behavior provides a browser-like navigation experience.

---

# Challenges Faced

### 1. Google Sign-In Re-Authentication Issue

One of the most challenging issues occurred during the implementation of Google Sign-In.

After logging out, clicking "Continue with Google" immediately logged the user back into the previously used account without displaying the Google account selection screen.

After investigation, I found that Firebase Authentication and Google Sign-In maintain separate sessions. Although Firebase sign-out successfully removed the Firebase session, the Google SDK still retained cached account credentials.

To solve this problem, I redesigned the logout flow to clear both Firebase and Google Sign-In sessions. The logout implementation was updated to explicitly invalidate cached Google credentials before redirecting the user back to the authentication screen.

This ensured that users could choose a different Google account when signing in again.

### 2. Room Database and KSP Build Errors

While integrating Room Database, I faced multiple KSP-related build issues.

Some generated classes were not being created correctly, causing compilation failures and unresolved references.

The issue was resolved by verifying dependency compatibility, rebuilding generated files, cleaning Gradle caches, and updating Room/KSP versions.

This helped me better understand Android's annotation processing and code generation mechanisms.

### 3. WebView State Management

Initially, WebView pages were reloading unexpectedly during configuration changes and screen recompositions.

I improved state handling to provide a smoother browsing experience and reduce unnecessary reloads.

### 4. Android 13 Notification Permission

Android 13 introduced runtime notification permissions.

Additional permission handling logic was required to ensure notifications were displayed correctly while maintaining compatibility with older Android versions.

---

# Future Improvements

The following features can be added in future releases:

* Bookmark Management
* Download Manager
* Incognito Mode
* Multiple Browser Tabs
* Dark Theme Support
* Website Blocking Feature
* Password Protected Browser
* Search Suggestions
* Cloud Synchronization
* Browser Settings Screen
* Enhanced Security Features

---

# Technologies Used

* Kotlin
* Jetpack Compose
* MVVM Architecture
* Room Database
* Firebase Authentication
* Google Sign-In
* Coroutines
* StateFlow
* WebView
* SharedPreferences
* Material 3

---

# Author

Vishwajeet Thakur

Android Developer | Kotlin | Jetpack Compose | Firebase
