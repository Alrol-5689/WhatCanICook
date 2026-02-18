# WhatCanICook

Aplicacion Spring Boot 4.0.2 (Java 21) lista para construir una API con Spring Web MVC, Spring Data JPA y validacion sobre MySQL.

## Requerimientos
- Java 21 (JDK).
- Maven 3.9+ o el wrapper incluido `./mvnw`.
- MySQL 8+ escuchando en `localhost:3309` con la base `WhatCanICook`, usuario `root` y contrasena vacia (configurable).
- Docker opcional para levantar MySQL rapidamente.

## Configuracion rapida
1) Clonar o descargar el repositorio.
2) Crear la base de datos (si usas MySQL local):
   ```sql
   CREATE DATABASE WhatCanICook CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3) Ajustar credenciales en `src/main/resources/application.properties` si difieren de tu entorno.

### MySQL via Docker (opcional)
```bash
docker run --name mysql-whatcanicook -p 3309:3306 -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -e MYSQL_DATABASE=WhatCanICook -d mysql:8
```

## Como ejecutar
- Desarrollo con recarga: `./mvnw spring-boot:run`
- Empaquetar: `./mvnw clean package`
- Ejecutar el JAR generado: `java -jar target/WhatCanICook-0.0.1-SNAPSHOT.jar`

## Configuracion de JPA y base de datos
- `spring.jpa.hibernate.ddl-auto=update` mantiene el esquema creando tablas y columnas nuevas sin borrar datos.
- `spring.jpa.show-sql=true` registra las sentencias SQL en consola.
- Dialecto configurado: `org.hibernate.dialect.MySQLDialect`.
- `spring.jpa.open-in-view=false` deshabilita Open Session in View para evitar LazyInitialization en vistas.

## Estructura basica
- `src/main/java/com/WhatCanICook/WhatCanICookApplication.java` arranca la aplicacion.
- `src/main/resources/application.properties` contiene la configuracion de datasource y JPA.
- No hay controladores ni entidades aun; el proyecto sirve como punto de partida para anadirlos.

## Notas
- Dependencias incluidas: Spring Web MVC, Spring Data JPA, Validation, Actuator, H2 console (para uso futuro), DevTools y Lombok (opcional, requiere plugin del IDE).
- No hay pruebas automatizadas por el momento.
