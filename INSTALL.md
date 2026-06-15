# Guía de Instalación — Sistema de Gestión de Alumnos
# IES Alfredo Coviello

Este documento explica cómo levantar el proyecto desde cero en una máquina sin nada instalado.
El sistema tiene dos partes: **Backend** (Java/Spring Boot) y **Frontend** (Angular).

---

## Índice

1. [Requisitos de software](#1-requisitos-de-software)
2. [Estructura del proyecto](#2-estructura-del-proyecto)
3. [Configuración de la base de datos](#3-configuración-de-la-base-de-datos)
4. [Levantar el backend](#4-levantar-el-backend)
5. [Levantar el frontend](#5-levantar-el-frontend)
6. [Usuarios de prueba](#6-usuarios-de-prueba)
7. [Configuración de email](#7-configuración-de-email)
8. [Resumen rápido (ya tenés todo instalado)](#8-resumen-rápido)
9. [Solución de problemas frecuentes](#9-solución-de-problemas-frecuentes)

---

## 1. Requisitos de software

### 1.1 Java Development Kit (JDK) 21

El backend requiere **Java 21 LTS**. Versiones anteriores no son compatibles.

**Descargar desde:**
- Eclipse Temurin (recomendado, gratuito): https://adoptium.net/es/temurin/releases/?version=21
- Oracle JDK: https://www.oracle.com/java/technologies/downloads/#java21

**Verificar instalación:**
```bash
java -version
# Debe mostrar: openjdk version "21.x.x" o similar
```

> No es necesario instalar Maven por separado — el proyecto incluye `mvnw` (Maven Wrapper),
> que descarga Maven automáticamente la primera vez que se ejecuta.

---

### 1.2 MySQL 8.x

El backend usa **MySQL 8.0 o superior**.

**Descargar desde:**
- MySQL Community Server: https://dev.mysql.com/downloads/mysql/
  - En el instalador, elegir "Developer Default" o como mínimo "MySQL Server" + "MySQL Workbench"

**Verificar instalación:**
```bash
mysql --version
# Debe mostrar: mysql  Ver 8.x.x ...
```

> Durante la instalación de MySQL, se pedirá una contraseña para el usuario `root`.
> El proyecto está configurado para usar `root` / `root` (ver sección 3).

---

### 1.3 Node.js 22 LTS y npm

El frontend requiere **Node.js 22 LTS** (incluye npm automáticamente).

**Descargar desde:**
- https://nodejs.org/en/download/ → elegir "22.x LTS"

**Verificar instalación:**
```bash
node --version
# Debe mostrar: v22.x.x

npm --version
# Debe mostrar: 10.x.x
```

---

### 1.4 Angular CLI (opcional pero recomendado)

```bash
npm install -g @angular/cli@21
```

Verificar:
```bash
ng version
# Debe mostrar: Angular CLI: 21.x.x
```

> Sin Angular CLI global, todos los comandos `ng ...` se reemplazan por `npx ng ...`

---

## 2. Estructura del proyecto

Los dos proyectos deben estar en carpetas separadas:

```
proyecto final coviello/
├── gestion-de-alumnos/          ← BACKEND (este repo)
│   ├── src/
│   ├── pom.xml
│   ├── mvnw  (Linux/Mac)
│   ├── mvnw.cmd  (Windows)
│   ├── FLUJOS.md
│   └── INSTALL.md  ← estás aquí
│
└── gestion-alumnos-front/       ← FRONTEND
    ├── src/
    ├── angular.json
    └── package.json
```

---

## 3. Configuración de la base de datos

### 3.1 Crear el schema

Abrir **MySQL Workbench** (o cualquier cliente MySQL) y ejecutar:

```sql
CREATE DATABASE IF NOT EXISTS GESTIONALUMNOS
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

> El nombre del schema es **GESTIONALUMNOS** (todo en mayúsculas, sin guiones ni espacios).

### 3.2 Credenciales configuradas en el proyecto

El archivo `src/main/resources/application.properties` ya tiene configurado:

| Parámetro | Valor |
|-----------|-------|
| URL       | `jdbc:mysql://localhost:3306/GESTIONALUMNOS` |
| Usuario   | `root` |
| Contraseña | `root` |
| Puerto    | `3306` (default de MySQL) |

**Si tu MySQL tiene una contraseña diferente para `root`**, editá el archivo
`src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA_AQUI
```

### 3.3 Las tablas se crean automáticamente

Spring Boot está configurado con `spring.jpa.hibernate.ddl-auto=update`, lo que significa que
**Hibernate crea y actualiza las tablas automáticamente** al iniciar el backend.
No hay que correr ningún script SQL adicional para las tablas.

### 3.4 Datos iniciales (cargados automáticamente)

Al levantar el backend por primera vez, el sistema crea automáticamente:
- Los 4 roles del sistema: `SUPER_ADMIN`, `ADMIN`, `DOCENTE`, `ALUMNO`
- Los usuarios iniciales (ver [sección 6](#6-usuarios-de-prueba))
- 3 carreras de ejemplo
- Los permisos del sistema

---

## 4. Levantar el backend

### 4.1 Navegar al directorio del backend

```bash
cd "ruta/a/gestion-de-alumnos"
```

### 4.2 Compilar y verificar que no hay errores

```bash
# Linux / Mac / Git Bash en Windows:
./mvnw compile -q

# Windows (cmd / PowerShell):
mvnw.cmd compile -q
```

Si el comando termina sin mensajes de error, el código compila correctamente.

### 4.3 Levantar el servidor

```bash
# Linux / Mac / Git Bash:
./mvnw spring-boot:run

# Windows (cmd / PowerShell):
mvnw.cmd spring-boot:run
```

**Primera ejecución:** Maven descarga las dependencias (~200 MB). Puede tardar varios minutos
dependiendo de la conexión a internet. Las ejecuciones posteriores son mucho más rápidas.

### 4.4 Verificar que está funcionando

Cuando el servidor esté listo, la consola debe mostrar algo como:
```
Started GestionDeAlumnosApplication in 4.3 seconds (process running for 5.1)
```

Probar en el navegador o con curl:
```
http://localhost:8080/api/carreras
```
Debe devolver un JSON con la lista de carreras.

> El backend escucha en el puerto **8080**.

---

## 5. Levantar el frontend

### 5.1 Navegar al directorio del frontend

```bash
cd "ruta/a/gestion-alumnos-front"
```

### 5.2 Instalar dependencias

```bash
npm install
```

Esto descarga todos los paquetes de Angular y sus dependencias (puede tardar 1-2 minutos la primera vez).

### 5.3 Levantar el servidor de desarrollo

```bash
npm start
# equivalente a: ng serve
```

### 5.4 Verificar que está funcionando

La consola debe mostrar:
```
✔ Compiled successfully.

Watch mode enabled. Watching for file changes...
  ➜  Local:   http://localhost:4200/
```

Abrir en el navegador:
```
http://localhost:4200
```

Debe aparecer la pantalla de login.

### 5.5 Proxy (ya configurado — no requiere cambios)

El frontend usa un proxy para redirigir las llamadas a la API al backend automáticamente.
La configuración está en `src/proxy.conf.json`:

```json
{
  "/api":   { "target": "http://localhost:8080" },
  "/auth":  { "target": "http://localhost:8080" }
}
```

Esto significa que cuando el frontend hace `GET /api/carreras`, Angular lo redirige
automáticamente a `http://localhost:8080/api/carreras`. **No hay que configurar nada extra.**

---

## 6. Usuarios de prueba

El sistema crea estos usuarios automáticamente al iniciar:

| Rol        | Email                         | Contraseña | Acceso |
|------------|-------------------------------|------------|--------|
| SUPER_ADMIN | `superadmin@coviello.com`    | `Admin1234` | Panel admin + gestión de usuarios |
| ADMIN       | `admin@coviello.com`         | `Admin1234` | Panel admin (preinscripciones, alumnos, etc.) |

> **DOCENTE y ALUMNO** no tienen usuarios iniciales. El SUPER_ADMIN debe crear docentes
> desde `/admin/usuarios`. Los alumnos se crean automáticamente cuando el ADMIN habilita
> una preinscripción.

### ¿Cómo se accede?

1. Ir a `http://localhost:4200`
2. Ingresar el email y contraseña de la tabla
3. El sistema redirige automáticamente según el rol:
   - `SUPER_ADMIN` y `ADMIN` → `/admin/lista` (panel de preinscripciones)
   - `DOCENTE` → `/docente/portal`
   - `ALUMNO` → `/dashboard`

---

## 7. Configuración de email

El sistema envía emails automáticamente para:
- PDF de formulario de preinscripción
- Confirmación de turno
- Habilitación de alumno (con contraseña temporal)
- Emails masivos

### Configuración actual (Mailtrap — entorno de pruebas)

El proyecto ya está configurado con **Mailtrap**, una herramienta que intercepta los emails
en entorno de desarrollo. Los emails **no llegan a destinatarios reales**, sino a una bandeja
de entrada de prueba en https://mailtrap.io.

Las credenciales en `application.properties`:
```properties
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=483b4631c26056
spring.mail.password=d6d78efd7a21c7
```

> Estas credenciales corresponden a la cuenta de Mailtrap del proyecto.
> Para ver los emails enviados, acceder a la cuenta en https://mailtrap.io.

### Cambiar a email real (producción)

Para usar Gmail en producción, reemplazar en `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu.cuenta@gmail.com
spring.mail.password=TU_APP_PASSWORD_DE_GMAIL
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> Para Gmail necesitás crear una "App Password" en la configuración de seguridad de Google,
> no usar la contraseña normal de la cuenta.

---

## 8. Resumen rápido

Si ya tenés todo instalado (JDK 21, MySQL, Node 22), los pasos son:

**Terminal 1 — Backend:**
```bash
cd "ruta/a/gestion-de-alumnos"
./mvnw spring-boot:run          # Linux/Mac
mvnw.cmd spring-boot:run        # Windows
```

**Terminal 2 — Frontend:**
```bash
cd "ruta/a/gestion-alumnos-front"
npm install                     # solo la primera vez
npm start
```

**Abrir en el navegador:**
```
http://localhost:4200
```

**Login inicial:**
```
superadmin@coviello.com  /  Admin1234
admin@coviello.com       /  Admin1234
```

---

## 9. Solución de problemas frecuentes

### ❌ "Access denied for user 'root'@'localhost'"
La contraseña de MySQL no es `root`. Editá `application.properties`:
```properties
spring.datasource.password=TU_CONTRASEÑA_REAL
```

### ❌ "Unknown database 'GESTIONALUMNOS'"
El schema no existe. Correr en MySQL Workbench:
```sql
CREATE DATABASE GESTIONALUMNOS CHARACTER SET utf8mb4;
```

### ❌ "Port 8080 already in use"
Otro proceso usa el puerto 8080. Opciones:
- Cerrar el proceso que lo usa
- Cambiar el puerto en `application.properties`: `server.port=8081`
  Y actualizar `src/proxy.conf.json` en el frontend para que apunte al nuevo puerto.

### ❌ "npm error: engine node@xx.x.x unsupported"
Versión de Node.js incorrecta. Necesitás Node 22+.
Usar [nvm](https://github.com/nvm-sh/nvm) (Linux/Mac) o
[nvm-windows](https://github.com/coreybutler/nvm-windows) para manejar versiones de Node.

### ❌ Los emails no llegan
En desarrollo esto es normal — los emails van a Mailtrap (bandeja de prueba), no al email real.
Revisar https://mailtrap.io con las credenciales del proyecto.

### ❌ "JAVA_HOME is not set" o Java no encontrado
El JDK no está correctamente en el PATH. Verificar con `java -version`.
Si no muestra nada, reinstalar el JDK o configurar la variable de entorno `JAVA_HOME`.

### ❌ El frontend carga pero las llamadas a la API dan 404 o CORS error
Verificar que el backend está corriendo en el puerto 8080. El proxy del frontend solo
funciona cuando el servidor de desarrollo (`npm start`) está activo.

---

## Versiones de referencia

| Software | Versión requerida | Notas |
|----------|------------------|-------|
| Java (JDK) | 21 LTS | OpenJDK o Oracle JDK |
| Maven | 3.9+ | Incluido via `mvnw`, no necesita instalarse |
| MySQL | 8.0+ | Community Server |
| Node.js | 22 LTS | Incluye npm 10+ |
| Angular CLI | 21.x | Opcional, se puede usar `npx ng` |
| Spring Boot | 4.0.5 | Incluido en el proyecto |
| Angular | 21.x | Incluido en el proyecto |
