# WhatCanICook

Red social de recetas en Android (Kotlin, Material 3). La app permite autenticarse, publicar recetas y descubrir lo que cocina la comunidad.

## Estado actual
- Pantalla de **login** funcional con validación básica y navegación a `MainActivity` tras éxito.
- Tema `Theme.WhatCanICook` basado en Material 3 DayNight listo para extender.
- Retrofit configurado con `BuildConfig.BASE_URL` y repositorio de autenticación mínimo.
- Configuración de compilación: `compileSdk 36`, `minSdk 24`, `targetSdk 36`, Gradle 9.2.1 y AGP 9.0.1.

## Requisitos
- JDK 17 (necesario para Gradle 9.2.1/AGP 9.x).
- Android Studio Ladybug (2024.1.2) o superior, con el SDK 36 instalado.
- Dispositivo o emulador Android con API 24+.

## Configurar el backend (BASE_URL)
El cliente usa `BuildConfig.BASE_URL` definido en `app/build.gradle.kts`. Por defecto toma la propiedad `BASE_URL` de `local.properties` y, si no existe, cae a `http://10.0.2.2:8080/` (la IP del host vista desde el emulador estándar de Android).

1) Abre `local.properties` (no se versiona) y añade/edita:
   ```
   BASE_URL=http://<tu_ip_host>:8080/
   ```
   - **Dispositivo físico por cable / Wi‑Fi**: el móvil y tu máquina deben estar en la **misma red**. Usa la IP de tu ordenador en esa red (ej. `192.168.x.x`).
   - **Emulador**: puedes borrar esa línea o poner la IP de la VM que uses. En el emulador clásico basta no definirla para usar `10.0.2.2`.
2) Reinicia la sincronización Gradle para regenerar `BuildConfig`.

## Puesta en marcha rápida
1) Abre el proyecto en Android Studio y sincroniza Gradle, o instala dependencias por CLI con `./gradlew tasks`.
2) Conecta un emulador o dispositivo con depuración USB activa (API 24+).

## Compilar y ejecutar
- Android Studio: botón **Run** sobre la configuración "app".
- CLI: `./gradlew assembleDebug` genera `app/build/outputs/apk/debug/app-debug.apk`.

## Pruebas
- Unitarias JVM: `./gradlew test`.
- Instrumentadas: `./gradlew connectedAndroidTest` (requiere dispositivo o emulador).

## Esquema de clases (función principal)
- `network/RetrofitClient` – construye Retrofit con `BuildConfig.BASE_URL` y expone `authApi`.
- `network/AuthApi` – endpoints `auth/login` y `auth/register`.
- `repository/AuthRepository` – capa intermedia que delega en `AuthApi`.
- `ui/login/LoginViewModel` – valida campos, invoca `AuthRepository.login`, expone `LiveData` de éxito/error.
- `ui/login/LoginActivity` – binding de UI, observa el ViewModel y navega a `MainActivity` con los datos del usuario.
- `ui/main/MainActivity` – muestra username/email y permite cerrar sesión (vuelve a `LoginActivity`).
- `ui/register/…` y `ui/recipes/…` – actividades vacías listas para implementar registro y navegación de recetas.
- `model/entity` – `User`, `Recipe`, `Ingredient`, `RecipeStep` básicos.
- `model/request` / `model/response` – DTOs para login/registro y respuesta de autenticación.

## Roadmap social
- Feed de recetas: global y siguiendo.
- Perfil de usuario y seguimiento.
- Publicación/edición de recetas con fotos, pasos e ingredientes.
- Comentarios, reacciones y guardados.
- Buscador por ingredientes/tags.

## Próximos pasos sugeridos
- Añadir backend real a `BASE_URL` y manejar tokens en `AuthInterceptor/SessionManager`.
- Conectar la pantalla de registro (`RegisterActivity`) y construir el flujo de alta.
- Crear `RecipeRepository` y poblar `RecipesActivity`/`RecipeDetailActivity` con datos reales o mock.
