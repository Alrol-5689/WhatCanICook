# WhatCanICook

Red social de recetas en Android (Kotlin, Material 3). La app permite autenticarse, publicar recetas y descubrir lo que cocina la comunidad.

## Estado actual
- Pantalla de **logueo** diseñada con Material 3: logo, copy de bienvenida, campos de email/contraseña y acciones "Entrar", "Crear cuenta" y "Continuar como invitado".
- Tema `Theme.WhatCanICook` basado en Material 3 DayNight listo para extender.
- Configuración de compilación: `compileSdk 36`, `minSdk 24`, `targetSdk 36`, Gradle 9.2.1 y AGP 9.0.1.

## Requisitos
- JDK 17 (necesario para Gradle 9.2.1/AGP 9.x).
- Android Studio Ladybug (2024.1.2) o superior, con el SDK 36 instalado.
- Dispositivo o emulador Android con API 24+.

## Puesta en marcha rápida
1) Abre el proyecto en Android Studio y sincroniza Gradle, o instala dependencias por CLI con `./gradlew tasks`.
2) Conecta un emulador o dispositivo con depuración USB activa (API 24+).

## Compilar y ejecutar
- Android Studio: botón **Run** sobre la configuración "app".
- CLI: `./gradlew assembleDebug` genera `app/build/outputs/apk/debug/app-debug.apk`.

## Pruebas
- Unitarias JVM: `./gradlew test`.
- Instrumentadas: `./gradlew connectedAndroidTest` (requiere dispositivo o emulador).

## Estructura útil
- `app/src/main/java/com/app/MainActivity.kt` – actividad principal.
- `app/src/main/res/layout/activity_main.xml` – pantalla de logueo.
- `app/src/main/AndroidManifest.xml` – declaración de actividad y tema.
- `gradle/libs.versions.toml` – versiones centralizadas de dependencias y plugins.

## Roadmap social
- Feed de recetas: global y siguiendo.
- Perfil de usuario y seguimiento.
- Publicación/edición de recetas con fotos, pasos e ingredientes.
- Comentarios, reacciones y guardados.
- Buscador por ingredientes/tags.

## Próximos pasos sugeridos
- Conectar la pantalla de logueo a un proveedor de autenticación (Firebase Auth, Supabase, OIDC propio).
- Añadir navegación a un `HomeActivity`/`HomeScreen` con feed simulado mientras se implementa el backend.
- Crear modelos `User` y `Recipe` y repositorios (remoto + caché Room) para empezar a poblar el feed.
