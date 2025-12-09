# Manual Técnico: Mails Management

## 1. Introducción

Este documento describe la arquitectura técnica y el funcionamiento del proyecto **Mails Management**.

El sistema está diseñado para gestionar el flujo de correos electrónicos entrantes, automatizando el proceso de asignación, revisión, aprobación y respuesta final a través de un motor de procesos de negocio (BPM).

La aplicación lee un buzón de correo, inicia un proceso de negocio por cada correo nuevo, y orquesta las tareas entre diferentes roles de usuario hasta que se envía una respuesta final.

## 2. Tecnologías Utilizadas

- **Lenguaje:** Java 17
- **Framework Principal:** Spring Boot 3.5.5
- **Motor de Procesos (BPM):** Camunda BPM 7.24.0
- **Base de Datos:** PostgreSQL
- **Acceso a Datos:** Spring Data JPA / Hibernate
- **Seguridad:** Spring Security
- **Servidor de Aplicaciones:** Incorporado (Tomcat)
- **Gestión de Dependencias:** Maven
- **Integración de Correo:** Jakarta Mail (a través de `angus-mail`)
- **Utilerías:** Lombok

## 3. Prerrequisitos

- **JDK 17** o superior.
- **Maven 3.x** o superior.
- Una instancia de **PostgreSQL** en ejecución.
- Acceso a una cuenta de correo **Gmail** con una contraseña de aplicación (si se usa la configuración por defecto).

## 4. Configuración

El archivo principal de configuración es `src/main/resources/application.yaml`.

### 4.1. Base de Datos

La conexión a la base de datos se configura bajo la sección `spring.datasource`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mails_db
    username: postgres
    password: postgres
```

- `ddl-auto: update` indica que Hibernate intentará actualizar el esquema de la base de datos automáticamente.

### 4.2. Camunda BPM

La configuración de Camunda se encuentra bajo `camunda.bpm`:

```yaml
camunda.bpm:
  admin-user:
    id: demo
    password: demo
```

- Esto crea un usuario administrador por defecto para la consola de Camunda (`demo`/`demo`).
- La aplicación escanea y despliega automáticamente todos los modelos de proceso (`*.bpmn`) que encuentre en el classpath.

### 4.3. Integración con Gmail

La aplicación se conecta a un buzón de Gmail para leer los correos. Las credenciales se configuran en la sección `gmail`:

```yaml
gmail:
    address: user@gmail.com
    password: "xxxx"
```

**Nota de Seguridad:** La contraseña debe ser una "Contraseña de Aplicación" generada desde la configuración de la cuenta de Google, no la contraseña principal del usuario.

## 5. Estructura del Proyecto

- `com.project.camunda`: Contiene la lógica relacionada con el proceso de Camunda.
  - `delegate`: Clases Java que son invocadas por los Service Tasks del BPMN (ej. `RegistrarRecepcion`).
  - `impl`: Clases que implementan lógica de negocio invocada desde el proceso.
- `com.project.controller`: Controladores REST para exponer la API del sistema.
- `com.project.dto`: Data Transfer Objects para las peticiones y respuestas de la API.
- `com.project.entity`: Entidades JPA que mapean a la estructura de la base de datos.
- `com.project.enums`: Enumeraciones para estados, etapas y roles.
- `com.project.mails`: Lógica para la interacción con el servidor de correo (IMAP).
- `com.project.repository`: Repositorios Spring Data JPA para el acceso a la base de datos.
- `com.project.service`: Capa de servicio que contiene la lógica de negocio principal.
- `src/main/resources`:
  - `*.bpmn`: Modelos del proceso de negocio.
  - `application.yaml`: Fichero de configuración.

## 6. Modelo de Datos

El modelo de datos se centra en tres entidades principales:

- **`Correo`**: Representa un correo electrónico gestionado por el sistema. Almacena el asunto, cuerpo, estado, fechas clave y los identificadores del proceso (`idProceso`) y de radicado.
- **`Usuario`**: Modela a un usuario del sistema. Implementa `UserDetails` de Spring Security para la autenticación y autorización. Cada usuario tiene uno o más roles.
- **`FlujoCorreos`**: Es una tabla de auditoría que registra cada paso del proceso para un correo. Asocia un `Correo`, un `Usuario` y una `ETAPA` del proceso (Elaboración, Revisión, etc.), junto con las fechas de inicio y fin de la tarea.

## 7. Proceso de Negocio (BPMN)

El flujo de trabajo está modelado en dos archivos BPMN que funcionan conjuntamente.

### 7.1. Proceso Principal: `mails-management-process`

Este es el proceso de alto nivel que se inicia con cada correo recibido.

1.  **Correo Recibido**: El proceso es iniciado por un componente externo (`ImapService`) que monitorea el buzón.
2.  **Registrar Recepción**: Un `ServiceTask` (`${registrarRecepcion}`) registra el correo en la base de datos.
3.  **Remitir a Gestor**: Una `UserTask` asigna la tarea a un usuario con el rol `GESTOR`.
4.  **Gestionar Correo**: Un `CallActivity` invoca al subproceso `mails-management-call` donde ocurre la gestión detallada del correo.
5.  **Correo Gestionado**: El proceso finaliza cuando el subproceso termina.

### 7.2. Subproceso: `mails-management-call`

Este proceso detalla el ciclo de vida de la gestión del correo y está dividido en carriles (Lanes) según el rol del usuario.

1.  **Etapa de Elaboración**: El `GESTOR` prepara una propuesta de respuesta.
2.  **Etapa de Revisión**:
    - El `GESTOR` remite la respuesta a los `REVISORES`.
    - Los `REVISORES` reciben una tarea (`Responder solicitud de revisión`). Pueden aprobarla o devolverla al `GESTOR` para corrección.
3.  **Etapa de Aprobación**:
    - Si los revisores aprueban, el `GESTOR` remite la respuesta al `APROBADOR`.
    - El `APROBADOR` recibe una tarea (`Responder solicitud de aprobación`) para dar el visto bueno final.
4.  **Etapa de Envío**:
    - Con la aprobación final, el `GESTOR` envía el correo a una "bandeja de salida" (`Enviar a bandeja de salida`).
    - Un `ServiceTask` (`${enviarRespuestaFinal}`) se encarga del envío real del correo.
5.  **Fin**: El subproceso finaliza.

A lo largo del proceso, `ServiceTask` como `${registrarInicioElaboracion}` o `${registrarFinRevision}` utilizan las clases delegate para actualizar la entidad `FlujoCorreos`, manteniendo un registro de auditoría completo.

## 8. REST API Endpoints

La aplicación expone una API para la gestión de usuarios.

**URL Base**: `/api/v1/usuarios`

| Método | Ruta                  | Descripción                | Request Body                     | Respuesta Exitosa                      |
|--------|-----------------------|----------------------------|----------------------------------|----------------------------------------|
| `POST` | `/crear`              | Crea un nuevo usuario.     | `UsuarioCrearRequest`            | `201 Created` con `UsuarioResponse`    |
| `DELETE`| `/eliminar/{id}`      | Elimina un usuario (lógico).| N/A                              | `200 OK`                               |
| `PUT`  | `/actualizar/{id}`    | Actualiza un usuario.      | `UsuarioActualizarRequest`       | `200 OK` con datos del usuario         |

## 9. Build & Run

1.  **Configurar la Base de Datos**: Asegúrese de que su instancia de PostgreSQL esté activa y cree una base de datos (ej. `mails_db`).
2.  **Configurar `application.yaml`**: Ajuste las credenciales de la base de datos y de Gmail.
3.  **Compilar el Proyecto**:
    ```bash
    mvn clean install
    ```
4.  **Ejecutar la Aplicación**:
    ```bash
    mvn spring-boot:run
    ```
    O ejecute el JAR generado:
    ```bash
    java -jar target/mails-management-1.0.0-SNAPSHOT.jar
    ```
5.  **Acceder a la Consola de Camunda**:
    - **URL**: `http://localhost:8080/camunda/app/`
    - **Usuario**: `demo`
    - **Contraseña**: `demo`

