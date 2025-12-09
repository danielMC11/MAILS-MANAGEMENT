# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is a **Email Traceability Management System** (MAILS-MANAGEMENT) for Llanogas, built with Spring Boot 3.5.5, Java 17, and Camunda BPM 7.24.0. The system receives emails via IMAP, processes them through a multi-stage workflow (reception, elaboration, revision, approval, and sending), and tracks the entire lifecycle with user assignments at each stage.

## Commands

### Build & Run
```powershell
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Development
```powershell
# Compile without tests
mvn clean compile

# Package the application
mvn package

# Run with Spring Boot Maven plugin
mvn spring-boot:run
```

### Access Points
- **Application**: http://localhost:8080
- **Camunda Cockpit**: http://localhost:8080/camunda
  - Username: `demo`
  - Password: `demo`
- **H2 Console** (if enabled): Check application.yaml for database settings

### Database
The project uses **PostgreSQL** as the primary database:
- Database: `mails_db`
- Host: `localhost:5432`
- User: `postgres`
- Password: `postgres`

Note: An H2 database file (`camunda-h2-database.mv.db`) exists in the root for Camunda's internal use.

## Architecture

### Core Flow: Email Processing Pipeline

The application implements a **Strategy Pattern** for email processing using `MailProcessor` interface with multiple implementations:

1. **IMAP Service** (`ImapService`) listens continuously to Gmail inbox using IDLE mode
2. When an email arrives, it's converted to a `Mail` object
3. The `mailHandler` (configured in `MailConfig`) iterates through registered `MailProcessor` implementations
4. Each processor's `supports()` method determines if it should handle the email
5. The appropriate processor's `process()` method is invoked

**Key Processors:**
- `StartMailProcess`: Handles emails from external users (not in the system). Creates a new Camunda process instance with key `mails-management-process`

### Camunda BPMN Workflow

The system uses **Camunda BPM** to orchestrate a multi-stage email approval workflow defined in `processMail.bpmn`:

**Workflow Stages (ETAPA enum):**
1. **RECEPCION** (Reception) - Integrador receives the email
2. **ELABORACION** (Elaboration) - Gestor drafts the response
3. **REVISION** (Revision) - Reviewers validate the response (can send back for corrections)
4. **APROBACION** (Approval) - Approver gives final approval
5. **ENVIO** (Sending) - Email is sent from outbox

**User Roles (lanes in BPMN):**
- **Integrador**: Routes incoming emails to gestores
- **Gestor**: Drafts responses, manages revisions and approval flow
- **Revisores**: Review and approve/reject responses
- **Aprobador**: Final approval before sending

**Key Delegates** (in `com.project.camunda.delegate`):
- `RegistrarRecepcion`: Creates `Correo` entity and initial `FlujoCorreos` record
- `RegistrarInicio[Etapa]`: Records when each stage starts
- `RegistrarFin[Etapa]`: Records when each stage completes

### Entity Model

**Core Entities:**
- `Correo`: Email record with subject, body, status (ESTADO: PENDIENTE/RESPONDIDO), reception/response dates, and unique radicados (filing numbers)
- `FlujoCorreos`: Tracks each stage assignment - links Correo, Usuario, ETAPA, and timestamps
- `Usuario`: System user with roles (ROL: INTEGRADOR, GESTOR, REVISOR, APROBADOR)
- `Cuenta`: External email account (sender) with associated Entidad
- `Entidad`: External organization/entity
- `TipoSolicitud`: Request type classification
- `Rol`: User role definition

**Key Relationships:**
- `Correo` has one `Cuenta` (sender) and optionally one `TipoSolicitud`
- `FlujoCorreos` links `Correo` to `Usuario` for each ETAPA
- Process uses `correoId` as Camunda business key

### Service Layer Pattern

Services follow interface-implementation pattern:
- Interfaces in `com.project.service`
- Implementations in `com.project.service.impl`
- Use `@Service` on implementations
- Constructor injection preferred (Lombok `@RequiredArgsConstructor` is used in config classes)

### REST API

Controller pattern:
- Base path: `/api/v1/{resource}/`
- Example: `UsuarioController` at `/api/v1/usuarios/`
- Uses DTO pattern: `UsuarioCrearRequest`, `UsuarioActualizarRequest`, `UsuarioResponse`
- Validation with `@Valid` on request DTOs
- Custom validator: `@ExisteRoles` checks if roles exist before user creation/update

### Configuration

**Security** (`SecurityConfig`):
- Currently configured to **permit all** requests (authentication is commented out)
- CORS enabled for `http://localhost:3000` (React frontend)
- Form-based login configured for `/login` and `/logout` endpoints (currently disabled)
- Uses `BCryptPasswordEncoder` for passwords

**Email** (`application.yaml`):
- Gmail IMAP connection: `danielcarrillo3200@gmail.com`
- Uses app-specific password (not visible in code)
- IMAPS protocol on `imap.gmail.com`

## Development Guidelines

### Adding New Email Processors

1. Create class implementing `MailProcessor` interface
2. Implement `supports(Mail mail)` - return true if this processor should handle the email
3. Implement `process(Mail mail)` - execute your logic
4. Annotate with `@Component` - Spring will auto-discover and register it

### Extending the Workflow

1. Modify `processMail.bpmn` in Camunda Modeler
2. Add new service tasks with `camunda:delegateExpression="${beanName}"`
3. Create corresponding delegate class implementing `JavaDelegate`
4. Annotate delegate with `@Service("beanName")`
5. Update ETAPA enum if adding new stages

### Working with Process Variables

Camunda process variables are set/retrieved via `DelegateExecution`:
```java
delegateExecution.getVariable("correoId")
delegateExecution.setVariable("etapaActual", ETAPA.RECEPCION)
```

Key variables in the process:
- `correoId`: Email identifier (message ID)
- `from`, `to`, `subject`, `text`, `date`: Email metadata
- `etapaActual`: Current workflow stage
- `devueltoRevision`: Boolean flag for revision returns
- `flujoRecepcionId`, etc.: FlujoCorreos IDs for each stage

### Database Migrations

The project uses `spring.jpa.hibernate.ddl-auto=update` which auto-updates schema. For production:
- Change to `validate`
- Use Flyway or Liquibase for controlled migrations
- Schema changes require manual migration scripts

## Important Notes

- The application runs a **background thread** listening to IMAP indefinitely (`ImapService.startIdleListener()`)
- Gmail requires an **app-specific password** (not regular account password)
- Camunda maintains its own schema in PostgreSQL
- Email radicados (filing numbers) must be unique in the database
- Process instances are identified by the original email's message ID
