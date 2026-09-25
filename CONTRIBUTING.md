# Contribuir a HabitQuest

Gracias por ayudar a mejorar HabitQuest. El proyecto busca una experiencia de
bienestar accesible, estable y coherente en Android.

## Preparar el entorno

- Android Studio y JDK 17.
- Android SDK correspondiente al `compileSdk` del proyecto.
- Un emulador o dispositivo con Android 8.0 (API 26) o superior.

Abre la carpeta raíz en Android Studio y sincroniza Gradle antes de realizar
cambios.

## Flujo de trabajo

1. Crea una rama enfocada desde `master`.
2. Mantén cada pull request limitado a un objetivo.
3. Añade o actualiza pruebas cuando cambie la lógica.
4. Ejecuta las verificaciones locales.
5. Describe el problema, la solución y la validación en el pull request.

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Criterios de aceptación

- La compilación, las pruebas y Android Lint deben completarse correctamente.
- La interfaz debe conservar soporte para modo claro y oscuro.
- Los cambios de Room deben incluir una migración y pruebas asociadas.
- No se deben versionar secretos, claves de firma ni bases de datos locales.
- Los nuevos estados de UI deben seguir el flujo unidireccional existente.

## Estilo

Usa Kotlin idiomático, estado inmutable y componentes Compose pequeños. Mantén
las reglas de dominio fuera de la capa visual y documenta en el pull request
cualquier decisión que no resulte evidente en el código.
