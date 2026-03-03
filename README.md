# TaskNotes — Personal Task Manager

A simple Android application for managing personal tasks and notes.
Built as Assignment 03 for SEN4302 Mobile Application Development.

---

## App Description

TaskNotes is a lightweight personal task manager that allows users to:

- **Add** new tasks with a title and optional description
- **View** all saved tasks in a clean scrollable list
- **Complete** tasks by tapping the checkbox (shows strikethrough)
- **Delete** tasks using the delete button on each card
- **Persist** all data locally — tasks remain after app restart

The app works completely offline with no internet connection,
no backend server, and no Firebase. All data is stored privately
on the device using SharedPreferences.

## Screenshots

### Screenshot 1 — App Launch Fist Time
![App Launch](screenshots/App_Launch.jpg)
![App Launch](screenshots/P_App_Launch.png)

When the app is opened for the first time (or after all tasks
are deleted), a friendly empty state message is shown.
The floating action button (+) is always visible to add tasks.

---

### Screenshot 2 — Task List Screen
![Task List](screenshots/Display_List_of_Tasks.jpg)
![Task List](screenshots/P_Display_List_of_Tasks.png)

The main screen shows all saved tasks as Material Design cards.
Completed tasks display with strikethrough text and grey color.
The delete button (red trash icon) appears on every card.

---

### Screenshot 3 — Add New Task Screen
![Add Task](screenshots/Add_New_Task.jpg)
![Add Task](screenshots/P_Add_New_Tasks.png)

The Add Task screen provides two input fields.
Title is required — an error message appears if left empty.
Description is optional. The Cancel button discards input
and returns to the main screen.

---

### Screenshot 4 — Delete Notification Popup
![Delete Task](screenshots/P_Task_Delete_Notification.png)

When the delete button (trash icon) is pressed, the selected task is removed immediately.
A confirmation Snackbar message appears at the bottom showing “Task deleted”.
This provides instant feedback to the user.

### Screenshot 5 — Save Notification Popup
![Save Task](screenshots/P_Task_Save_Notification.png)

After adding a new task successfully, a Snackbar message appears displaying “Task saved!”.
The task list refreshes automatically and shows the newly added task.
This confirms the save operation to the user.

## Technical Specifications

Language - Kotlin 
Minimum SDK - API 26 (Android 8.0 Oreo) 
Target SDK - API 36 (Android 16) 
Architecture - MVVM (Model-View-ViewModel) 
Data Persistence - SharedPreferences  
UI Framework - Material Design Components 
IDE - Android Studio 
Internet - Not required
Backend - None

## Project Structure
```
com.student.tasknotes/
│
├── model/
│   └── Task.kt                 Data class for task objects
│
├── viewmodel/
│   └── TaskViewModel.kt        State management + input validation
│
├── adapter/
│   └── TaskAdapter.kt          RecyclerView adapter for task list
│
├── TaskRepository.kt           SharedPreferences data operations
├── MainActivity.kt             Main screen — displays task list
└── AddEditTaskActivity.kt      Add task screen

res/layout/
├── activity_main.xml           Main screen layout
├── activity_add_edit_task.xml  Add task screen layout
└── item_task.xml               Individual task card layout
```

## Design Choices

### 1. Architecture — MVVM Pattern

The app follows the **MVVM (Model-View-ViewModel)** architecture:

- **Model** (`Task.kt`) — Pure data, no Android dependencies
- **View** (`MainActivity`, `AddEditTaskActivity`) — Only handles
UI display and user interaction. Never touches data directly.
- **ViewModel** (`TaskViewModel`) — Holds all app state and logic.
Survives screen rotation so no data is lost when phone is rotated.

This separation makes the code easier to understand, test,
and maintain. Each class has one clear responsibility.

---

### 2. Data Persistence — SharedPreferences

SharedPreferences was chosen over Room (SQLite) because:

- This app stores a simple flat list — no complex queries needed
- SharedPreferences is sufficient for beginner-level projects
- Easier to implement and understand for learning purposes
- Tasks are serialized to JSON using Android's built-in JSONObject
- Data survives app restarts, device reboots, and screen rotation

**How it works:**
```
List<Task>  to  JSONArray (String)  to  SharedPreferences
SharedPreferences to  JSONArray  to  List<Task>
```

---

### 3. State Management — LiveData + ViewModel

- `TaskViewModel` holds the task list as `LiveData<List<Task>>`
- `MainActivity` observes this LiveData
- When data changes, the UI updates **automatically**
- No manual refreshing needed — the observer pattern handles it
- ViewModel survives screen rotation (Activity does not)
- Draft text (unsaved typing) is preserved in ViewModel fields

---

### 4. UI Design — Material Design

- `MaterialCardView` for task cards — elevation and rounded corners
- `TextInputLayout` with outlined style for input fields
- `FloatingActionButton` for adding tasks (standard Android UX)
- `RecyclerView` for efficient scrolling list
- Consistent color scheme: deep blue primary, amber accent
- No animations — clarity prioritized over visual complexity

---

### 5. RecyclerView Implementation

RecyclerView was chosen over ListView because:

- More memory efficient (recycles card views while scrolling)
- Better performance for long lists
- Modern Android standard for displaying lists
- Supports ViewHolder pattern out of the box

The adapter uses callbacks (lambdas) for delete and complete
actions. This keeps the adapter decoupled from the Activity —
the adapter does not need to know how these actions are handled.

---

## Secure Coding Practices

### Practice 1 — Input Validation (`TaskViewModel.kt`)

All user input is validated before saving:
```kotlin
// Trim whitespace — prevents "   " being saved as a task
val cleanTitle = title.trim()

// Reject blank input
if (cleanTitle.isBlank()) return false

// Enforce length limit — prevents unreasonably large input
if (cleanTitle.length > 100) return false
```

Only clean, validated data is passed to the repository and stored.

---

### Practice 2 — Secure Local Storage (`TaskRepository.kt`)

SharedPreferences is opened with `MODE_PRIVATE`:
```kotlin
context.getSharedPreferences("tasknotes_prefs", Context.MODE_PRIVATE)
```

`MODE_PRIVATE` ensures that only this app can read or write
the stored data. No other app on the device can access our tasks.
No sensitive data (passwords, tokens, personal information)
is stored in SharedPreferences.

---

### Practice 3 — Safe UI State Management (`TaskAdapter.kt`)

Checkbox listeners are removed before setting state:
```kotlin
// Remove listener BEFORE setting checked state
binding.checkBoxComplete.setOnCheckedChangeListener(null)
binding.checkBoxComplete.isChecked = task.isCompleted

// Re-attach listener AFTER setting state
binding.checkBoxComplete.setOnCheckedChangeListener { ... }
```

This prevents phantom toggle events caused by RecyclerView
recycling card views and accidentally triggering old listeners.

---

## Author

**Course:** SEN4302 Mobile Application Development
**Assignment:** 03 — Mini Project
**Lecturer:** Eranga Tennakoon
**Student ID** 11526