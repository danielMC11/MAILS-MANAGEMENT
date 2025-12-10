# Análisis de la Estructura y Endpoints del Proyecto

Este documento proporciona un análisis detallado de los endpoints existentes en el proyecto, describiendo la función de cada componente principal (Controlador, Servicio, Repositorio, etc.) para cada módulo o entidad de negocio.

---

## 1. Gestión de Usuarios

Este módulo se encarga de todo lo relacionado con los usuarios del sistema, incluyendo su creación, actualización, eliminación y asignación de roles.

-   **Entidad Principal:** `Usuario.java`
-   **Ubicación:** `com.project.entity.Usuario`

### Archivos y Funciones

| Capa | Archivo(s) | Funciones Principales |
| :--- | :--- | :--- |
| **Controlador** | `UsuarioController.java` | Expone los endpoints para el CRUD de usuarios, búsqueda y cambio de estado. |
| **Servicio** | `UsuarioService.java` (Interfaz)<br>`UsuarioServiceImpl.java` (Implementación) | - **`crearUsuario`**: Crea un nuevo usuario, cifra su contraseña y le asigna roles.<br>- **`actualizarUsuario`**: Modifica los datos de un usuario existente.<br>- **`eliminarUsuario`**: Realiza un borrado lógico del usuario (`activo = false`).<br>- **`cambiarEstado`**: Activa o desactiva un usuario explícitamente.<br>- **`listar...`, `buscar...`, `obtener...`**: Métodos para consultar usuarios por diferentes criterios. |
| **Repositorio**| `UsuarioRepository.java`<br>`RolRepository.java` | - **`findByCorreo`**: Busca un usuario por su email.<br>- **`findByNombreContainingIgnoreCase`, `findByRol`**: Consultas personalizadas para búsquedas.<br>- `RolRepository` se usa para validar y obtener los roles a asignar. |
| **DTOs** | `UsuarioCrearRequest.java`<br>`UsuarioActualizarRequest.java`<br>`UsuarioResponse.java` | Modelan los datos para crear, actualizar y mostrar usuarios, evitando exponer la entidad y la contraseña. |

### Endpoints Expuestos (`/api/v1/usuarios/`)

-   `POST /crear`: Crea un nuevo usuario.
-   `PUT /actualizar/{id}`: Actualiza un usuario.
-   `DELETE /eliminar/{id}`: Desactiva un usuario (borrado lógico).
-   `PUT /{id}/estado`: Cambia el estado (activo/inactivo) de un usuario.
-   `GET /`: Lista todos los usuarios.
-   `GET /pagina`: Lista usuarios de forma paginada.
-   `GET /{id}`: Obtiene un usuario por su ID.
-   `GET /buscar/nombre`: Busca usuarios por nombre o apellido.
-   `GET /buscar/rol` y `GET /rol/{rol}`: Busca usuarios por su rol.

---

## 2. Gestión de Entidades

Representa a las organizaciones o dominios de correo con los que interactúa el sistema (ej. "llanosgas.com").

-   **Entidad Principal:** `Entidad.java`
-   **Ubicación:** `com.project.entity.Entidad`

### Archivos y Funciones

| Capa | Archivo(s) | Funciones Principales |
| :--- | :--- | :--- |
| **Controlador** | `EntidadController.java` | Endpoints para el CRUD de entidades. |
| **Servicio** | `EntidadService.java`<br>`EntidadServiceImpl.java` | - **`crearEntidad`**: Crea una nueva entidad, validando que el dominio no exista.<br>- **`actualizarEntidad`**: Actualiza los datos de una entidad.<br>- **`eliminarEntidad`**: Elimina una entidad, validando que no tenga `Cuentas` asociadas.<br>- **`listarEntidades`, `buscarPorNombre`**: Métodos de consulta. |
| **Repositorio**| `EntidadRepository.java` | - **`existsByDominioCorreo`**: Valida si ya existe una entidad con un dominio específico.<br>- **`findByNombreEntidadContainingIgnoreCase`**: Busca entidades por nombre. |
| **DTOs** | `EntidadCrearRequest.java`<br>`EntidadActualizarRequest.java`<br>`EntidadResponse.java` | DTOs para las operaciones CRUD. |

### Endpoints Expuestos (`/api/v1/entidades/`)

-   `POST /crear`: Crea una nueva entidad.
-   `PUT /actualizar/{id}`: Actualiza una entidad.
-   `DELETE /eliminar/{id}`: Elimina una entidad.
-   `GET /`: Lista todas las entidades.
-   `GET /buscar`: Busca entidades por nombre.

---

## 3. Gestión de Cuentas

Representa las cuentas de correo electrónico que se gestionan en el sistema. Cada cuenta pertenece a una `Entidad`.

-   **Entidad Principal:** `Cuenta.java`
-   **Ubicación:** `com.project.entity.Cuenta`

### Archivos y Funciones

| Capa | Archivo(s) | Funciones Principales |
| :--- | :--- | :--- |
| **Controlador** | `CuentaController.java` | Endpoints para el CRUD y búsqueda de cuentas. |
| **Servicio** | `CuentaService.java`<br>`CuentaServiceImpl.java` | - **`crearCuenta`**: Crea una cuenta, validando que el correo no exista y que el dominio corresponda al de la entidad.<br>- **`actualizarCuenta`**: Actualiza los datos de una cuenta.<br>- **`eliminarCuenta`**: Elimina una cuenta.<br>- **`listarCuentas`**: Lista todas las cuentas con información de su entidad.<br>- **`buscarCuentas`**: Búsqueda multi-criterio (nombre de cuenta, correo, nombre de entidad). |
| **Repositorio**| `CuentaRepository.java` | - **`search`**: JPQL complejo para buscar en `Cuenta` y en la `Entidad` relacionada.<br>- **`findAllWithEntidad`**: JPQL que usa `JOIN FETCH` para cargar eficientemente las entidades relacionadas y evitar problemas de N+1. |
| **DTOs** | `CuentaCrearRequest.java`<br>`CuentaActualizarRequest.java`<br>`CuentaResponse.java` | `CuentaResponse` incluye campos de la entidad (`entidadId`, `nombreEntidad`) para enriquecer la respuesta. |

### Endpoints Expuestos (`/api/v1/cuentas/`)

-   `POST /crear`: Crea una nueva cuenta de correo.
-   `PUT /actualizar/{id}`: Actualiza una cuenta.
-   `DELETE /eliminar/{id}`: Elimina una cuenta.
-   `GET /`: Lista todas las cuentas.
-   `GET /buscar`: Busca cuentas por nombre, email o nombre de la entidad.

---

## 4. Gestión de Tipos de Solicitud

Catalogo para los diferentes tipos de solicitud que pueden tener los correos (ej. "PQR", "Facturación", "Soporte").

-   **Entidad Principal:** `TipoSolicitud.java`
-   **Ubicación:** `com.project.entity.TipoSolicitud`

### Archivos y Funciones

| Capa | Archivo(s) | Funciones Principales |
| :--- | :--- | :--- |
| **Controlador** | `TipoSolicitudController.java` | Endpoints para el CRUD de los tipos de solicitud. |
| **Servicio** | `TipoSolicitudService.java`<br>`TipoSolicitudServiceImpl.java` | Lógica para crear, actualizar, eliminar y listar los tipos de solicitud, con validaciones para evitar nombres duplicados y borrado de tipos en uso. |
| **Repositorio**| `TipoSolicitudRepository.java` | Consultas para buscar por nombre y verificar existencia. |
| **DTOs** | `TipoSolicitudRequest.java`<br>`TipoSolicitudResponse.java`<br>`TipoSolicitudSimpleResponse.java` | DTOs para las operaciones CRUD. |

### Endpoints Expuestos (`/api/v1/tipos-solicitud/`)

-   `GET /`: Lista todos los tipos de solicitud.
-   `GET /{id}`: Obtiene un tipo de solicitud por ID.
-   `POST /`: Crea un nuevo tipo de solicitud.
-   `PUT /{id}`: Actualiza un tipo de solicitud.
-   `DELETE /{id}`: Elimina un tipo de solicitud.

---

## 5. Gestión de Correos y Flujos (Camunda)

Este es el corazón del sistema, donde se procesan los correos entrantes y se gestiona su ciclo de vida a través de un proceso de Camunda.

-   **Entidades Principales:** `Correo.java`, `FlujoCorreos.java`

### Archivos y Funciones

| Capa | Archivo(s) | Funciones Principales |
| :--- | :--- | :--- |
| **Controlador** | `CorreoController.java`<br>`FlujoCorreoController.java` | - **`CorreoController`**: Endpoints para consultar correos, obtener estadísticas y filtrar por diferentes criterios.<br>- **`FlujoCorreoController`**: Endpoints para consultar el historial de un correo en el flujo de Camunda. |
| **Servicio** | `CorreoService.java`<br>`FlujoCorreoService.java`<br>`ImapService.java`<br>`EmailService.java` | - **`ImapService`**: Se conecta al servidor de correo para leer emails no leídos.<br>- **`CorreoService`**: Procesa los emails leídos, los guarda en la BD y (potencialmente) inicia un proceso en Camunda.<br>- **`FlujoCorreoService`**: Provee la lógica para consultar las diferentes etapas por las que ha pasado un correo. |
| **Camunda** | `src/main/java/com/project/camunda/` | Contiene las clases `JavaDelegate` que se ejecutan en cada paso del proceso BPMN (ej. `RegistrarRecepcion`, `RemitirAprobador`, etc.). |
| **Repositorio**| `CorreoRepository.java`<br>`FlujoCorreoRepository.java` | Consultas complejas para filtrar correos y flujos por fechas, estados, usuarios, etc. |

### Endpoints Principales

-   `GET /api/v1/correos/`: Lista correos con filtros y paginación.
-   `GET /api/v1/correos/estadisticas`: Proporciona métricas sobre los correos.
-   `GET /api/v1/flujo-correos/correo/{id}`: Obtiene el historial de un correo específico.

---

## 6. Dashboard

Provee endpoints para consolidar estadísticas y métricas generales del sistema.

-   **Controlador:** `DashboardController.java`
-   **Servicio:** `DashboardService.java`
-   **Repositorio:** `DashboardRepository.java` (Probablemente con consultas nativas o JPQL complejas)
-   **Endpoints:**
    -   `GET /api/v1/dashboard/estadisticas`: Retorna un resumen de métricas clave (ej. correos por estado, correos vencidos, etc.).

