# HabitQuest

<div align="center">

## Tu compañera de bienestar para hábitos, rutinas y progreso tranquilo

HabitQuest es una app Android para registrar hábitos y tareas con una interfaz pastel de bienestar, streaks emocionales, XP, niveles, una mascota evolutiva y persistencia local sin conexión.

<br />

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white)
![Room v7](https://img.shields.io/badge/Room-v7%20Offline-0F9D58?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-MVP-F59E0B?style=for-the-badge)

</div>

---

## Overview

HabitQuest convierte hábitos y tareas cotidianas en un loop gamificado y calmo: crea actividades, complótalas, acumula XP, mantén tu racha emocional, sube de nivel y revisa tu progreso con el tiempo.

La app es local-first: no requiere conexión, no usa Firebase. La UI está escrita en Jetpack Compose con Material 3, la persistencia es Room, y la arquitectura sigue el patrón MVVM con Hilt.

---

## Pantallas

| Home (Hoy) | Perfil + Pet | Estadísticas |
|---|---|---|
| Vista diaria con hábitos y tareas pendientes | Avatar, nivel, mascota evolutiva y logros | Gráficos semanales y desglose por categoría |

> Capturas en `docs/screenshots/` — próximamente.

---

## Features

### Loop principal
- Creación de hábitos y tareas con selector de categoría, validación y preview de XP antes de guardar.
- Completar y revertir hábitos/tareas con actualización inmediata de XP.
- Home muestra solo las tareas del día (por `scheduledDate`) más las tareas sin fecha pendientes.
- Feedback visual al completar: toast de XP, banner de logros desbloqueados, reacción de la mascota.

### Calendario de tareas
- Vista mensual en grid de 7 columnas con navegación mes anterior/siguiente.
- Tareas futuras visibles por `scheduledDate`; completadas por `completedAt`.
- Panel detalle al seleccionar un día con estado pendiente/completado de cada tarea.

### Gamificación
- **XP por categoría** — cada hábito otorga XP según su tipo (ver tabla más abajo).
- **Bonus de racha** — +25 XP al completar un hábito con racha ≥ 3 días.
- **Tareas** — 30 XP fijo por tarea completada.
- **Niveles** — 5 niveles con nombre y color propios.
- **Logros** — 5 achievements desbloqueables (Primer paso, Ritmo de 7 días, Rutina organizada, Nueva versión, Energía acumulada).
- **Misión diaria** — progreso visual hacia completar los hábitos del día.

### Sistema de streaks emocionales
Milestones suaves: **Constancia → Ritmo → Flujo → Hábito real**, con tarjeta de recompensa al alcanzarlos.

### Mascota evolutiva (Companion)
La mascota refleja la constancia del usuario usando XP, nivel, racha y totales de hábitos/tareas completados. Reacciona en tiempo real al completar actividades.

| Stage | Nombre | XP mínimo | Nivel mínimo | Racha mínima |
|---|---|---|---|---|
| 🌱 Semilla | EGG | 0 | 1 | 0 |
| 🌷 Brote | BABY | 300 | 2 | 1 |
| 🌸 Flor | EXPLORER | 800 | 3 | 3 |
| ✨ Aura | GUARDIAN | 1 500 | 4 | 7 |
| 👑 Esencia | LEGEND | 2 500 | 5 | 14 |

### Perfil
Avatar personalizable (6 opciones), nombre de usuario, nivel y rango, barra de XP, stats globales, mascota con estado emocional (IDLE / HAPPY / PROUD / TIRED) y tarjetas de logros con rareza.

### Onboarding
Flujo de bienvenida animado la primera vez que se abre la app, con opción de omitir. Estado persistido en DataStore.

### Sonido ambiental
Loop de audio opcional (18% de volumen), que se pausa automáticamente al ir a segundo plano. Toggle persistido por usuario en DataStore.

### Recordatorios
- Hábitos: recordatorio diario vía WorkManager.
- Tareas: alarma exacta programada con `AlarmManager`, restaurada tras reinicio del dispositivo (`BootReceiver`).
- Permiso `SCHEDULE_EXACT_ALARM` solicitado en runtime.

### UI
- Pastel wellness con tarjetas redondeadas, gradientes suaves y tipografía cálida.
- Componentes premium reutilizables: `PremiumSurfaceCard`, `GlowIconBadge`, `NeonProgressBar`.
- Soporte dark mode automático vía Material 3.
- Animaciones con `AnimatedContent`, `AnimatedVisibility` y transiciones de Compose.

---

## XP y Niveles

### XP por categoría de hábito

| Categoría | XP base | + Bonus racha ≥ 3 |
|---|---|---|
| Productividad | 40 | +25 |
| Salud física | 35 | +25 |
| Desarrollo | 35 | +25 |
| Salud mental | 30 | +25 |
| Espiritualidad | 30 | +25 |
| Autocuidado | 30 | +25 |
| Familia / Corazón | 30 | +25 |
| Vida diaria | 20 | +25 |
| **Tarea (fijo)** | **30** | — |

### Niveles

| Nivel | Nombre | XP requerido |
|---|---|---|
| 1 | Inicio | 0 |
| 2 | Aprendiz | 200 |
| 3 | Constante | 500 |
| 4 | Creadora de hábitos | 1 000 |
| 5 | Inspiradora | 2 000 |

---

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Persistencia | Room 2.6 (DB v7) |
| DI | Hilt 2.51 |
| Arquitectura | MVVM |
| Async / estado | Coroutines + Flow + StateFlow |
| Preferencias | DataStore (Preferences) |
| Recordatorios | WorkManager + AlarmManager |
| Build | Gradle Kotlin DSL, AGP |
| minSdk / targetSdk | 26 / 35 |

---

## Arquitectura

```text
app/src/main/java/com/habitquest/
├── audio/                    # AmbientSoundPlayer (MediaPlayer lifecycle-aware)
├── data/
│   ├── local/                # Room DB (v7), DAOs, entities, migrations 4→7
│   ├── preferences/          # DataStore: ambient sound, onboarding, first run
│   └── repository/           # HabitRepository interface + HabitRepositoryImpl
├── di/                       # Hilt modules (Database, Repository, Notification)
├── domain/
│   ├── gamification/         # AchievementPolicy, PetEvolution, PetEvolutionPolicy,
│   │                         #   PetReactionPolicy, PetReactionState
│   └── model/                # Habit, Task, UserStats, Level/Levels, XpRules,
│                             #   HabitTemplate, Achievement
├── notification/             # AlarmTaskReminderScheduler, BootReceiver,
│                             #   HabitReminderWorker, NotificationHelper,
│                             #   TaskReminderReceiver, TaskReminderRestoreWorker
└── ui/
    ├── component/            # 16 componentes Compose reutilizables
    ├── navigation/           # NavGraph, Screen (Home · Progress · Profile · TasksCalendar)
    ├── screen/
    │   ├── home/             # HomeScreen, HomeViewModel, HabitCreateSheet, QuickAddSheet,
    │   │                     #   CategorySelector, FrequencySelector, HomeTaskFilters
    │   ├── onboarding/       # OnboardingScreen, OnboardingPage
    │   ├── profile/          # ProfileScreen, ProfileViewModel, ProfileHeaderCard,
    │   │                     #   RankCard, AchievementBadgeCard, XpProgressCard
    │   ├── progress/         # ProgressScreen, ProgressViewModel (logros + actividad semanal)
    │   ├── statistics/       # StatisticsScreen, StatisticsViewModel, StatisticsCalculator,
    │   │                     #   StatisticsCards
    │   └── tasks/            # TasksCalendarScreen, TasksCalendarViewModel
    └── theme/                # Color, Theme, Type
```

El flujo de datos va de Room DAOs → `HabitRepository` → ViewModels como `StateFlow<UiState>`. Los Composables colectan ese estado con `collectAsStateWithLifecycle` y envían acciones de vuelta al ViewModel.

---

## Build & Test

```bash
# Debug APK
./gradlew :app:assembleDebug

# Release APK
./gradlew :app:assembleRelease

# Unit tests
./gradlew :app:testDebugUnitTest
```

En Windows reemplaza `./gradlew` por `.\gradlew.bat`.

### Cobertura de tests (17 archivos JVM)

| Área | Archivos |
|---|---|
| Dominio | `XpRulesTest`, `LevelsTest`, `PetEvolutionTest`, `PetEvolutionPolicyTest`, `PetReactionPolicyTest`, `AchievementPolicyTest` |
| Repository | `HabitRepositoryImplTest` |
| Data / DB | `DatabaseSeedPolicyTest`, `HabitQuestMigrationsTest`, `AmbientSoundPreferencesTest` |
| Notificaciones | `AlarmTaskReminderSchedulerTest`, `TaskReminderRestoreWorkerTest` |
| UI / ViewModel | `HomeTaskFiltersTest`, `HomeViewModelPetReactionTest`, `HabitCreateSheetReminderTest`, `StatisticsCalculatorTest`, `TasksCalendarScreenTest` |
| Assets | `PetImageResTest` |

---

## Roadmap

- [ ] Tests de UI con Compose (smoke tests de navegación y flujos core).
- [ ] Backup / export local.
- [ ] Widget de pantalla de inicio.

---

## Estado del proyecto

MVP funcional: el core loop está implementado, la app compila y todas las pantallas principales están operativas. No requiere red ni Firebase para ninguna funcionalidad.
