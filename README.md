# Sistema de Digitalización de Formatos Sanitarios — Rosmar

Sistema web que permite digitalizar el formato SSOP-R-PO Pre-Operational Log mediante fotografías e inteligencia artificial, eliminando la captura manual en Excel.

---

## Tabla de Contenidos

- [Descripción](#descripción)
- [Problema identificado](#problema-identificado)
- [Solución](#solución)
- [Arquitectura](#arquitectura)
- [Requerimientos](#requerimientos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso](#uso)
- [Contribución](#contribución)
- [Roadmap](#roadmap)

---

## Descripción

Aplicación web desarrollada con Java 21 y Spring Boot que permite al equipo de sanidad de Rosmar Consultoría de Sanidad fotografiar sus formatos físicos de inspección SSOP-R-PO. La inteligencia artificial de Anthropic (Claude Vision API) extrae automáticamente los datos del formato, el usuario los revisa y confirma, y se guardan en una base de datos MySQL.

---

## Problema Identificado

El equipo de sanidad de Rosmar registraba las inspecciones sanitarias en formatos físicos en papel y los comunicaba por WhatsApp. Esto generaba pérdida de información, falta de trazabilidad y dificultades para preparar reportes en auditorías sanitarias.

---

## Solución

Sistema web con dos roles de usuario:

- **Empleado**: sube la foto del formato, revisa los datos extraídos y confirma.
- **Supervisor**: consulta el historial, filtra por fechas y exporta reportes a Excel.

---

## Arquitectura

```
Navegador web (HTML + Bootstrap 5)
        ↓ HTTP
Spring Boot 4 + Tomcat embebido
  ├── Spring Security (autenticación y roles)
  ├── Spring Data JPA (acceso a datos)
  ├── Thymeleaf (plantillas HTML)
  └── Apache POI (generación de Excel)
        ↓ HTTP
Anthropic Claude Vision API
(extracción de datos de imágenes)
        ↓ JDBC
MySQL 8.0
  ├── usuarios
  ├── registros_ssop
  └── items_ssop
```

---

## Requerimientos

### Runtime
- Java 21 (JDK)
- Maven 3.9+ (o usar el wrapper `./mvnw` incluido)

### Base de datos
- MySQL 8.0

### Dependencias principales

| Dependencia | Versión |
|---|---|
| Spring Boot | 4.1.0 |
| Spring Security | incluida |
| Spring Data JPA | incluida |
| Thymeleaf | incluida |
| MySQL Connector/J | incluida |
| Apache POI | 5.4.0 |
| Commons IO | 2.15.1 |

---

## Instalación

### 1. Clonar el repositorio

```bash
git clone git@github.com:CarlosTirado16/rosmar-digitalizacion.git
cd rosmar-digitalizacion
```

### 2. Crear la base de datos

```sql
CREATE DATABASE rosmar_digitalizacion;
```

### 3. Insertar usuarios iniciales

```sql
USE rosmar_digitalizacion;

-- Contraseña: Admin1234
INSERT INTO usuarios (nombre, apellido, email, password, rol) VALUES 
('Supervisor', 'Rosmar', 'supervisor@rosmar.com', 
'$2a$10$aVgjHwEYLeja5fjBHf8ZIOcax9qIEobgkYiJND/XDCF7Syn9h8sRa', 
'SUPERVISOR');

-- Contraseña: Tecnico1234
INSERT INTO usuarios (nombre, apellido, email, password, rol) VALUES 
('Carlos', 'Tirado', 'carlos@rosmar.com',
'$2a$10$Z.GfWzS3frLpYu5V6FfmkeznxXTXIHu4QQ3VHRPypxUuFY/horHo2',
'EMPLEADO');
```

### 4. Configurar variables de entorno

El proyecto requiere las siguientes variables de entorno:

```
ANTHROPIC_API_KEY=tu_api_key_de_anthropic
DB_PASSWORD=tu_password_de_mysql
```

### 5. Ejecutar el proyecto

```bash
./mvnw spring-boot:run
```

Abre el navegador en: `http://localhost:8080`

### Ejecutar pruebas manualmente

```bash
./mvnw test
```

### Generar JAR para producción

```bash
./mvnw clean package -DskipTests
java -jar target/digitalizacion-0.0.1-SNAPSHOT.jar
```

---

## Configuración

### application.properties

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/rosmar_digitalizacion
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Subida de archivos
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
app.upload.dir=uploads

# Anthropic API
anthropic.api.key=${ANTHROPIC_API_KEY}

# Servidor
server.port=8080
```

---

## Uso

### Usuario Empleado

1. Accede a `http://localhost:8080` e inicia sesión
2. Haz clic en **Subir formato**
3. Selecciona o toma la foto del formato SSOP-R-PO
4. Espera a que la IA extraiga los datos automáticamente
5. Revisa y corrige los datos si es necesario
6. Confirma para guardar en la base de datos

**Credenciales de prueba:**
- Email: `carlos@rosmar.com`
- Contraseña: `Tecnico1234`

### Usuario Supervisor

Además de subir formatos, el supervisor puede:

1. **Ver historial** — consulta todos los registros con filtro por rango de fechas
2. **Ver detalle** — accede al detalle completo de cada registro
3. **Exportar Excel** — descarga los registros en formato Excel por rango de fechas

**Credenciales de prueba:**
- Email: `supervisor@rosmar.com`
- Contraseña: `Admin1234`

---

## Contribución

Este proyecto sigue el flujo de trabajo **Git Flow** con dos ramas principales protegidas: `main` y `develop`.

### Pasos para contribuir

**1. Clona el repositorio**
```bash
git clone git@github.com:CarlosTirado16/rosmar-digitalizacion.git
cd rosmar-digitalizacion
```

**2. Crea un branch desde develop**
```bash
git checkout develop
git pull origin develop
git checkout -b feature/nombre-de-tu-feature
```

Convención de nombres:
- `feature/` — nueva funcionalidad
- `fix/` — corrección de errores
- `docs/` — documentación

**3. Desarrolla y haz commit**
```bash
git add .
git commit -m "feat: descripcion del cambio"
```

Convención de commits:
- `feat:` — nueva funcionalidad
- `fix:` — corrección de error
- `docs:` — documentación
- `chore:` — configuración

**4. Sube tu branch y crea un Pull Request**
```bash
git push origin feature/nombre-de-tu-feature
```

- Base: `develop`
- Compare: `feature/nombre-de-tu-feature`

---

## Roadmap

| # | Funcionalidad |
|---|---|
| 1 | Soporte para múltiples tipos de formatos físicos |
| 2 | Dashboard con gráficas de incidencias por área |
| 3 | Notificaciones automáticas al supervisor |
| 4 | Modo offline con sincronización posterior |
| 5 | Aplicación móvil nativa |

---

**Alumno:** Carlos Eduardo Tirado Bañuelos  
**Institución:** Universidad Tecmilenio  
**Materia:** Tetramestre Empresarial I  
**Empresa:** Rosmar Consultoría de Sanidad
