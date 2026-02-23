# WhatCanICook

Aplicación Android en Kotlin que sirve como punto de partida para **WhatCanICook**, con una actividad básica y tema Material 3 listo para extenderse con funcionalidad de recetas.

## Estado actual
- Una sola `MainActivity` que muestra un `TextView` centrado (layout `res/layout/activity_main.xml`).
- Tema `Theme.WhatCanICook` basado en Material 3 DayNight.
- Configuración de compilación: `compileSdk 36`, `minSdk 24`, `targetSdk 36`, Gradle 9.2.1 y AGP 9.0.1.

## Requisitos
- JDK 17 (necesario para Gradle 9.2.1/AGP 9.x).
- Android Studio Ladybug (2024.1.2) o superior, con el SDK 36 instalado.
- Dispositivo o emulador Android con API 24+ para ejecutar.

## Puesta en marcha rápida
1) Abre el proyecto en Android Studio y permite la sincronización de Gradle, o bien instala dependencias por CLI con `./gradlew tasks`.
2) Conecta un emulador o dispositivo con depuración USB activa (API 24+).

## Compilar y ejecutar
- Desde Android Studio: botón **Run** sobre la configuración "app".
- CLI: `./gradlew assembleDebug` genera `app/build/outputs/apk/debug/app-debug.apk` listo para instalar.

## Pruebas
- Unitarias JVM: `./gradlew test`.
- Instrumentadas en dispositivo/emulador: `./gradlew connectedAndroidTest` (requiere dispositivo disponible).

## Estructura útil
- `app/src/main/java/com/app/MainActivity.kt` – actividad principal.
- `app/src/main/res/layout/activity_main.xml` – layout con el texto inicial.
- `app/src/main/AndroidManifest.xml` – declaración de actividad y tema.
- `gradle/libs.versions.toml` – versiones centralizadas de dependencias y plugins.

## Próximos pasos sugeridos
- Sustituir el texto inicial por la UI para buscar ingredientes y sugerir recetas.
- Añadir capa de datos (API o base local) y navegación si se agregan más pantallas.
- Configurar `versionCode`/`versionName` y paquetes (`applicationId`) definitivos antes de publicar.
