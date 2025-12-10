# Análisis Detallado de Endpoints por Controlador

Este documento ofrece una descripción detallada de cada endpoint disponible en la API, agrupados por su controlador correspondiente. Se especifica el método HTTP, la URL, los parámetros requeridos y el cuerpo de la respuesta esperada.

---

## 1. UsuarioController
**URL Base:** `/api/v1/usuarios`

| Método | URL | Parámetros/Body | Cuerpo de Respuesta | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/crear` | **Body:** `UsuarioCrearRequest` | `UsuarioResponse` | Crea un nuevo usuario. |
| `PUT` | `/actualizar/{id}` | **Path:** `id` del usuario<br>**Body:** `UsuarioActualizarRequest` | `UsuarioResponse` | Actualiza los datos de un usuario existente. |
| `DELETE`| `/eliminar/{id}` | **Path:** `id` del usuario | `200 OK` (vacío) | Desactiva un usuario (borrado lógico). |
| `PUT` | `/{id}/estado` | **Path:** `id` del usuario<br>**Query:** `activo` (boolean) | `200 OK` (vacío) | Cambia el estado de un usuario a activo o inactivo. |
| `GET` | `/` | (Ninguno) | `List<UsuarioResponse>` | Lista todos los usuarios activos. |
| `GET` | `/pagina` | **Query:** `pagina`, `tamano`, `ordenarPor`, `direccion` | `Page<UsuarioResponse>` | Retorna una lista paginada y ordenada de usuarios. |
| `GET` | `/{id}` | **Path:** `id` del usuario | `UsuarioResponse` | Obtiene un usuario específico por su ID. |
| `GET` | `/buscar/nombre` | **Query:** `nombre` (String) | `List<UsuarioResponse>` | Busca usuarios cuyo nombre o apellido contenga el texto. |
| `GET` | `/buscar/rol` | **Query:** `rol` (String) | `List<UsuarioResponse>` | Busca usuarios que tengan un rol específico. |
| `GET` | `/rol/{rol}` | **Path:** `rol` (String) | `List<UsuarioResponse>` | Alternativa para buscar usuarios por rol. |
| `GET` | `/correo/{correo}`| **Path:** `correo` (String) | `501 NOT_IMPLEMENTED` | Endpoint no implementado. |

---

## 2. EntidadController
**URL Base:** `/api/v1/entidades`

| Método | URL | Parámetros/Body | Cuerpo de Respuesta | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/crear` | **Body:** `EntidadCrearRequest` | `EntidadResponse` | Crea una nueva entidad (dominio). |
| `PUT` | `/actualizar/{id}` | **Path:** `id` de la entidad<br>**Body:** `EntidadActualizarRequest` | `EntidadResponse` | Actualiza una entidad existente. |
| `DELETE`| `/eliminar/{id}` | **Path:** `id` de la entidad | `200 OK` (vacío) | Elimina una entidad. |
| `GET` | `/` | (Ninguno) | `List<EntidadResponse>` | Lista todas las entidades. |
| `GET` | `/buscar` | **Query:** `nombre` (String) | `List<EntidadResponse>` | Busca entidades cuyo nombre contenga el texto. |

---

## 3. CuentaController
**URL Base:** `/api/v1/cuentas`

| Método | URL | Parámetros/Body | Cuerpo de Respuesta | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/crear` | **Body:** `CuentaCrearRequest` | `CuentaResponse` | Crea una nueva cuenta de correo asociada a una entidad. |
| `PUT` | `/actualizar/{id}` | **Path:** `id` de la cuenta<br>**Body:** `CuentaActualizarRequest` | `CuentaResponse` | Actualiza una cuenta de correo. |
| `DELETE`| `/eliminar/{id}` | **Path:** `id` de la cuenta | `200 OK` (vacío) | Elimina una cuenta de correo. |
| `GET` | `/` | (Ninguno) | `List<CuentaResponse>` | Lista todas las cuentas. |
| `GET` | `/buscar` | **Query:** `query` (String) | `List<CuentaResponse>` | Busca en nombre de cuenta, email o nombre de entidad. |
| `GET` | `/contar/{entidadId}`| **Path:** `entidadId` | `Long` | Cuenta el número de cuentas asociadas a una entidad. |

---

## 4. TipoSolicitudController
**URL Base:** `/api/v1/tipos-solicitud`

| Método | URL | Parámetros/Body | Cuerpo de Respuesta | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/` | **Body:** `TipoSolicitudRequest` | `TipoSolicitudResponse` | Crea un nuevo tipo de solicitud. |
| `PUT` | `/{id}` | **Path:** `id`<br>**Body:** `TipoSolicitudRequest` | `TipoSolicitudResponse` | Actualiza un tipo de solicitud. |
| `DELETE`| `/{id}` | **Path:** `id` | `204 NO_CONTENT` | Elimina un tipo de solicitud. |
| `GET` | `/` | (Ninguno) | `List<TipoSolicitudResponse>` | Lista todos los tipos de solicitud. |
| `GET` | `/{id}` | **Path:** `id` | `TipoSolicitudResponse` | Obtiene un tipo de solicitud por su ID. |
| `GET` | `/buscar` | **Query:** `nombre` (String) | `List<TipoSolicitudResponse>`| Busca tipos de solicitud por nombre. |

---

## 5. CorreoController
**URL Base:** `/api/v1/correos`

| Método | URL | Parámetros/Body | Cuerpo de Respuesta | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/search` | **Body:** `CorreoFilterRequest` | `Page<CorreoResponse>` | Búsqueda avanzada y paginada de correos. |
| `GET` | `/asunto` | **Query:** `texto` (String) | `List<CorreoResponse>` | Busca correos por texto en el asunto. |
| `GET` | `/cuenta/{cuentaId}`| **Path:** `cuentaId` | `List<CorreoResponse>` | Obtiene correos de una cuenta específica. |
| `GET` | `/entidad/{entidadId}`| **Path:** `entidadId` | `List<CorreoResponse>` | Obtiene correos de una entidad específica. |
| `GET` | `/tipo-solicitud/{tipoSolicitudId}` | **Path:** `tipoSolicitudId` | `List<CorreoResponse>` | Obtiene correos de un tipo de solicitud. |
| `GET` | `/estado/{estado}` | **Path:** `estado` (String) | `List<CorreoResponse>` | Obtiene correos en un estado específico. |
| `GET` | `/vencidos` | (Ninguno) | `List<CorreoResponse>` | Obtiene los correos vencidos. |
| `GET` | `/por-vencer` | **Query:** `dias` (Integer) | `List<CorreoResponse>` | Obtiene correos que están por vencer en los próximos `dias`. |
| `GET` | `/sin-respuesta` | (Ninguno) | `List<CorreoResponse>` | Obtiene correos que aún no han sido respondidos. |
| `GET` | `/respuestas-recientes` | **Query:** `dias` (Integer) | `List<CorreoResponse>` | Obtiene correos con respuestas en los últimos `dias`. |
| `GET` | `/radicado-entrada/{radicado}` | **Path:** `radicado` | `CorreoResponse` | Obtiene un correo por su radicado de entrada. |
| `GET` | `/radicado-salida/{radicado}` | **Path:** `radicado` | `CorreoResponse` | Obtiene un correo por su radicado de salida. |
| `GET` | `/estadisticas` | (Ninguno) | `CorreoEstadisticasResponse` | Obtiene un resumen de estadísticas de correos. |
| `GET` | `/{id}` | **Path:** `id` del correo | `CorreoResponse` | Obtiene un correo específico por su ID. |

---

## 6. FlujoCorreoController
**URL Base:** `/api/v1/flujos-correo`

*(Nota: Esta tabla es un resumen, el controlador tiene muchos endpoints especializados)*

| Método | URL | Parámetros/Body | Cuerpo de Respuesta | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/search` | **Body:** `FlujoCorreoFilterRequest`| `Page<FlujoCorreoResponse>` | Búsqueda avanzada y paginada de flujos. |
| `GET` | `/{id}` | **Path:** `id` del flujo | `FlujoCorreoResponse` | Obtiene un flujo por su ID. |
| `GET` | `/correo/{correoId}/historial` | **Path:** `correoId` | `List<FlujoCorreoResponse>`| Obtiene todo el historial de etapas de un correo. |
| `GET` | `/usuario/{usuarioId}/pendientes` | **Path:** `usuarioId` | `List<FlujoCorreoResponse>`| Obtiene los flujos pendientes para un usuario. |
| `GET` | `/estadisticas` | (Ninguno) | `FlujoCorreoEstadisticasResponse` | Obtiene estadísticas generales sobre los flujos. |
| `PUT` | `/{id}/asignar-usuario` | **Path:** `id`<br>**Query:** `usuarioId` | `FlujoCorreoResponse` | Asigna un usuario a una etapa del flujo. |

---

## 7. DashboardController
**URL Base:** `/api/v1/dashboard`

| Método | URL | Parámetros/Body | Cuerpo de Respuesta | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/estadisticas` | (Ninguno) | `DashboardEstadisticasDTO` | Obtiene un DTO con múltiples estadísticas para el dashboard. |
| `GET` | `/distribucion/estado` | (Ninguno) | `Map<String, Long>` | Retorna un mapa con la cantidad de correos por estado. |
| `GET` | `/distribucion/etapa` | (Ninguno) | `Map<String, Long>` | Retorna un mapa con la cantidad de flujos por etapa. |
| `GET` | `/indicadores` | (Ninguno) | `List<MetricaResponse>` | Retorna una lista de indicadores clave de rendimiento (KPIs). |
