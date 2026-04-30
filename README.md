# HabitQuest

<div align="center">

## A wellness companion for habits, routines, and gentle progress

HabitQuest is an Android MVP for tracking habits and tasks with a modern pastel wellness/lifestyle interface, emotional streaks, XP, levels, an evolution pet, and local offline persistence.

<br />

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white)
![Room](https://img.shields.io/badge/Room-Offline-0F9D58?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-MVP-F59E0B?style=for-the-badge)

</div>

---

## Overview

HabitQuest turns everyday habits and tasks into a calm gamified loop: create activities, complete them, gain XP, maintain emotional streaks, level up, and review progress over time.

The app is built as a local-first Android experience with a pastel, modern, emotional wellness/lifestyle visual style. Data is persisted with Room, the UI is written in Jetpack Compose with Material 3, and the project uses Hilt-backed MVVM screens.

## Screenshots

Place screenshots in `docs/screenshots/` using the following relative paths:

| Home Wellness | Evolution Pet | Statistics |
|---|---|---|
| ![Home Wellness](docs/screenshots/home-wellness.png) | ![Evolution Pet](docs/screenshots/profile-pet.png) | ![Statistics](docs/screenshots/statistics.png) |

## Features

- Habit and task creation with category selection, validation, and XP preview.
- Complete and revert habits/tasks with XP updates.
- Home keeps the daily view focused by showing only pending tasks scheduled for today, plus unscheduled pending tasks.
- Monthly task calendar in a modern 7-column grid layout.
- Calendar month navigation with previous/next controls.
- Future pending tasks appear in the calendar by `scheduledDate`.
- Completed tasks appear in the calendar by `completedAt`.
- Selected calendar days show a lower detail panel with that day's tasks and their pending/completed state.
- Category-based habit XP policy and fixed task XP.
- Emotional streak system with soft milestones like Constancia, Ritmo, Flujo, and Habito real.
- XP, levels, daily mission progress, and gentle completion feedback.
- Evolution Pet System: a companion that evolves from Egg to Legend based on XP, level, streak, completed habits, and completed tasks.
- Profile screen with avatar, level, rank, evolution pet, stats, and achievement badges.
- Statistics screen with weekly habit/task charts and category breakdown; weekly task metrics use `completedAt`.
- Pastel wellness UI with rounded cards, soft gradients, and a calm lifestyle feel.
- Offline persistence with Room.
- Room v6 includes nullable task scheduling dates and safe migrations for task completion/scheduling timestamps.
- Bottom navigation across Home, Statistics, and Profile.

## Evolution Pet System

The Profile screen includes a lightweight digital companion that represents user consistency. It evolves using existing progress data only: total XP, current level, streak, completed habits, and completed tasks.

Stages:

- **Egg**
- **Baby**
- **Explorer**
- **Guardian**
- **Legend**

## Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Persistence:** Room
- **Dependency injection:** Hilt
- **Architecture style:** MVVM
- **Async/state:** Kotlin Coroutines, Flow, StateFlow
- **Build:** Gradle Kotlin DSL, Android Gradle Plugin

## Architecture

The project keeps a compact MVVM structure suitable for an MVP:

```text
app/src/main/java/com/habitquest/
+-- data/
|   +-- local/          # Room database, DAOs, entities
|   +-- repository/     # Repository contract and implementation
+-- di/                 # Hilt modules
+-- domain/model/       # Domain models and level/achievement helpers
+-- notification/       # Reminder-related worker/helper classes
+-- ui/
    +-- component/      # Reusable Compose UI components
    +-- navigation/     # Bottom navigation and routes
    +-- screen/home/    # Home flow, create sheet, selectors
    +-- screen/profile/ # Profile, rank, badges, XP progress
    +-- screen/statistics/
    +-- screen/tasks/   # TasksCalendar history screen
```

Data flows from Room DAOs through `HabitRepository`, then into screen `ViewModel`s as `StateFlow` UI state. Compose screens collect that state and send user actions back to the ViewModel.

## MVP Status

HabitQuest is currently an MVP: the core loop works, the app builds successfully, and the main screens are implemented. It is ready to publish as an early project/demo repository, with the known limitations listed in the roadmap.

## Build & Test

From the project root:

```bash
./gradlew :app:assembleDebug
```

On Windows:

```powershell
.\gradlew.bat :app:assembleDebug
```

Run existing unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

On Windows:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Current unit test coverage includes JVM tests for XP rules, level calculation, habit and task completion/revert behavior, statistics calculation, Home task filtering, and pet evolution.

## Roadmap

- Add Compose smoke/UI tests for navigation and core flows.
- Add local backup/export support.

## Project Status

This repository is intended as a clean, presentable Android MVP for habit tracking with gamification. It does not use Firebase and does not require a network connection for the core experience.
