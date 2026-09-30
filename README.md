# 🔐 Demo Seguridad 01 - Spring Security con RBAC

## 📋 Descripción del Proyecto

Aplicación web desarrollada con **Spring Boot 4.1.1** y **Java 22** que implementa un sistema de **autenticación y autorización basada en roles (RBAC)** utilizando **Spring Security**. El proyecto demuestra cómo proteger endpoints REST con diferentes niveles de acceso según el rol del usuario.

---

## 🎯 Objetivo

Demostrar la implementación de seguridad en aplicaciones Spring Boot mediante:
- Autenticación con **HTTP Basic**
- Autorización basada en **roles** (USER, ADMIN, MANAGER)
- Encriptación de contraseñas con **BCrypt**
- Persistencia de datos con **JPA/Hibernate** y **MySQL**

---

## 🏗️ Arquitectura del Proyecto

```
com.tecsup
├── DemoSeguridad01Application.java          → Punto de entrada de la aplicación
├── config
│   └── SecurityConfig.java                  → Configuración de seguridad (filtros, roles)
├── controller
│   ├── PublicController.java                → Endpoint público (/api/free)
│   ├── UserController.java                  → Endpoint para USER (/client/home)
│   ├── ManagerController.java               → Endpoint para MANAGER (/manager/reportes)
│   └── AdminController.java                 → Endpoint para ADMIN (/management/dashboard)
├── model
│   ├── User.java                            → Entidad JPA de usuarios
│   └── Role.java                            → Entidad JPA de roles
├── repository
│   ├── UserRepository.java                  → Repositorio JPA de usuarios
│   └── RoleRepository.java                  → Repositorio JPA de roles
├── service
│   └── UserDetailsServiceImpl.java          → Servicio de autenticación de Spring Security
└── util
    └── DataLoader.java                      → Carga inicial de datos (roles y usuarios)
```

---

## 🔧 Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Spring Boot** | 4.1.1 | Framework principal |
| **Spring Security** | (incluido) | Autenticación y autorización |
| **Spring Data JPA** | (incluido) | Persistencia de datos |
| **MySQL** | 8.0+ | Base de datos relacional |
| **Lombok** | (incluido) | Reducción de código boilerplate |
| **BCrypt** | (incluido) | Encriptación de contraseñas |
| **Maven** | 3.9+ | Gestión de dependencias y build |

---

## 🔐 Conceptos de Seguridad Implementados

### 1. Autenticación (Authentication)
> **¿Quién eres?**

Es el proceso de verificar la identidad del usuario. En este proyecto se utiliza **HTTP Basic Authentication**, donde el cliente envía sus credenciales (usuario y contraseña) en cada petición HTTP.

**Flujo de autenticación:**
```
Cliente → HTTP Basic Auth → Spring Security → UserDetailsService → Base de datos
```

### 2. Autorización (Authorization)
> **¿Qué puedes hacer?**

Es el proceso de verificar qué permisos tiene un usuario autenticado. Se implementa mediante **roles** que determinan a qué endpoints puede acceder el usuario.

**Roles implementados:**
- `ROLE_USER` → Acceso básico
- `ROLE_ADMIN` → Acceso total
- `ROLE_MANAGER` → Acceso a módulo de reportes

### 3. RBAC (Role-Based Access Control)
> **Control de acceso basado en roles**

Modelo de seguridad donde los permisos se asignan a roles, y los usuarios se asignan a roles. Simplifica la gestión de permisos en aplicaciones con muchos usuarios.

**Jerarquía de acceso:**
```
Público (sin autenticación)
    ↓
USER (acceso básico)
    ↓
MANAGER (acceso a reportes)
    ↓
ADMIN (acceso total)
```

### 4. BCrypt Password Hashing
> **Almacenamiento seguro de contraseñas**

Las contraseñas nunca se almacenan en texto plano. Se utiliza el algoritmo **BCrypt** que aplica:
- **Salt aleatorio** → Previene ataques de tablas arcoíris
- **Hash iterativo** → Resistente a ataques de fuerza bruta
- **Factor de costo configurable** → Ajustable según necesidades de seguridad

### 5. CSRF (Cross-Site Request Forgery)
> **Protección contra falsificación de peticiones**

En este proyecto está **deshabilitado** para facilitar pruebas con Postman y APIs REST. En aplicaciones con sesiones de formulario debe estar habilitado.

---

## 📊 Modelo de Datos

### Entidad `User`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único (PK, autoincremental) |
| `username` | String | Nombre de usuario (único, no nulo) |
| `password` | String | Contraseña encriptada con BCrypt |
| `roles` | Set<Role> | Conjunto de roles asignados (ManyToMany) |

### Entidad `Role`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único (PK, autoincremental) |
| `name` | String | Nombre del rol (único, no nulo) |

### Relaciones
- **User** ↔ **Role**: Relación **ManyToMany** a través de la tabla `user_roles`
- **FetchType.EAGER**: Los roles se cargan automáticamente con el usuario

---

## 🔒 Configuración de Seguridad

### Matriz de Acceso

| Endpoint | Público | USER | MANAGER | ADMIN |
|----------|---------|------|---------|-------|
| `GET /api/free` | ✅ | ✅ | ✅ | ✅ |
| `GET /client/home` | ❌ | ✅ | ❌ | ✅ |
| `GET /manager/reportes` | ❌ | ❌ | ✅ | ❌ |
| `GET /management/dashboard` | ❌ | ❌ | ❌ | ✅ |

### Reglas de Seguridad (SecurityConfig)

```java
.requestMatchers("/api/free").permitAll()                       // Público
.requestMatchers("/management/dashboard").hasRole("ADMIN")     // Solo ADMIN
.requestMatchers("/client/home").hasAnyRole("USER", "ADMIN")   // USER o ADMIN
.requestMatchers("/manager/reportes").hasRole("MANAGER")       // Solo MANAGER
.anyRequest().authenticated()                                   // Cualquier otra ruta requiere autenticación
```

---

## 👤 Usuarios del Sistema

| Usuario | Contraseña | Rol | Descripción |
|---------|------------|-----|-------------|
| `user` | `user2026` | USER | Usuario estándar con acceso básico |
| `admin` | `admin2026` | ADMIN | Administrador con acceso total |
| `manager` | `manager2026` | MANAGER | Gerente con acceso a reportes |

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos
- **Java 22** o superior
- **Maven 3.9+** (o usar el wrapper `mvnw`)
- **MySQL 8.0+** instalado y ejecutándose

### Pasos de Instalación

1. **Crear la base de datos en MySQL:**
   ```sql
   CREATE DATABASE securitydb;
   ```

2. **Configurar credenciales** en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/securitydb
   spring.datasource.username=root
   spring.datasource.password=tu_password
   ```

3. **Ejecutar la aplicación:**
   ```bash
   # Con Maven wrapper (Windows)
   mvnw.cmd spring-boot:run

   # Con Maven wrapper (Linux/Mac)
   ./mvnw spring-boot:run

   # Con Maven instalado
   mvn spring-boot:run
   ```

4. **Verificar que la aplicación esté corriendo:**
   ```
   http://localhost:8080/api/free
   ```

---

## 🧪 Pruebas con Postman

### 1. Endpoint Público (sin autenticación)
```
GET http://localhost:8080/api/free
```
**Respuesta esperada:** `Endpoint público funcionando`

### 2. Autenticación como USER
```
GET http://localhost:8080/client/home
Authorization: Basic Auth
Username: user
Password: user2026
```
**Respuesta esperada:** `Bienvenido USER`

### 3. Autenticación como ADMIN
```
GET http://localhost:8080/management/dashboard
Authorization: Basic Auth
Username: admin
Password: admin2026
```
**Respuesta esperada:** `Bienvenido ADMIN`

### 4. Autenticación como MANAGER
```
GET http://localhost:8080/manager/reportes
Authorization: Basic Auth
Username: manager
Password: manager2026
```
**Respuesta esperada:** `Bienvenido MANAGER - Módulo de Reportes`

### 5. Prueba de Acceso Denegado
```
GET http://localhost:8080/management/dashboard
Authorization: Basic Auth
Username: user
Password: user2026
```
**Respuesta esperada:** `403 Forbidden`

---

## 📁 Estructura de Carpetas

```
demoSeguridad01/
├── src/
│   ├── main/
│   │   ├── java/com/tecsup/
│   │   │   ├── config/          → Configuración de seguridad
│   │   │   ├── controller/      → Controladores REST
│   │   │   ├── model/           → Entidades JPA
│   │   │   ├── repository/      → Repositorios JPA
│   │   │   ├── service/         → Servicios de negocio
│   │   │   └── util/            → Utilidades (DataLoader)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/tecsup/     → Tests unitarios
├── .mvn/wrapper/                → Maven wrapper
├── pom.xml                      → Dependencias del proyecto
└── README.md                    → Este archivo
```

---

## 📚 Conceptos Clave

### Spring Security
Framework de seguridad para aplicaciones Java que proporciona:
- **Autenticación** → Verificación de identidad
- **Autorización** → Control de acceso
- **Protección contra ataques** → CSRF, XSS, etc.
- **Integración con múltiples fuentes de datos** → JDBC, LDAP, OAuth2, JWT

### JPA (Java Persistence API)
Especificación de Java para el mapeo objeto-relacional (ORM):
- **Entidades** → Clases Java mapeadas a tablas
- **Repositorios** → Interfaces para operaciones CRUD
- **JPQL** → Lenguaje de consulta similar a SQL

### Lombok
Biblioteca que reduce el código boilerplate:
- `@Data` → Genera getters, setters, toString, equals, hashCode
- `@NoArgsConstructor` → Constructor sin argumentos
- `@AllArgsConstructor` → Constructor con todos los argumentos

### BCrypt
Algoritmo de hash de contraseñas:
- **Salt aleatorio** → Cada hash es único
- **Adaptativo** → Se puede aumentar el factor de costo
- **Unidireccional** → No se puede revertir

---

## 🔗 Enlaces Útiles

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Lombok Project](https://projectlombok.org/)
- [Postman](https://www.postman.com/)

---

## 📄 Licencia

Este proyecto es de uso educativo como parte del curso de Desarrollo de Aplicaciones Web.

---

## 👨‍💻 Autor

**Ymerino Dev** - [GitHub](https://github.com/ymerino-dev)

---

## 📌 Notas Adicionales

- **CSRF deshabilitado** → Aceptable para APIs REST, no para aplicaciones con sesiones de formulario
- **HTTP Basic** → Las credenciales viajan en cada petición (codificadas en Base64). En producción se recomienda **JWT** u **OAuth2**
- **Contraseñas en código** → Las contraseñas iniciales están en `DataLoader.java`. En producción deberían usar variables de entorno
- **Sin tests de seguridad** → Solo existe un test de contexto (`contextLoads`), no hay tests de integración para verificar los roles

---

**Última actualización:** Septiembre 2026
