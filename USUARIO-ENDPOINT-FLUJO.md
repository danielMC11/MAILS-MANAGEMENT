# Flujo de Endpoints de Usuario - Arquitectura Completa

## Tabla de Contenidos
1. [Visión General](#visión-general)
2. [Estructura de Archivos](#estructura-de-archivos)
3. [Flujo Completo por Operación](#flujo-completo-por-operación)
4. [Componentes Detallados](#componentes-detallados)
5. [Guía de Replicación](#guía-de-replicación)

---

## Visión General

Este documento explica **paso a paso** cómo funcionan los endpoints de Usuario, desde que llega la petición HTTP hasta que se devuelve la respuesta, pasando por todos los componentes involucrados.

### Arquitectura de Capas

```
┌─────────────────────────────────────────────────────────┐
│  1. CLIENTE (Frontend/Postman)                          │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP Request
                     ↓
┌─────────────────────────────────────────────────────────┐
│  2. CONTROLLER (@RestController)                        │
│     - Recibe petición HTTP                              │
│     - Valida con @Valid                                 │
│     - Delega al Service                                 │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│  3. VALIDADORES CUSTOM (@ExisteRoles)                   │
│     - Validador personalizado ejecuta lógica de negocio │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│  4. SERVICE INTERFACE                                   │
│     - Define contrato de operaciones                    │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│  5. SERVICE IMPLEMENTATION (@Service)                   │
│     - Lógica de negocio                                 │
│     - Transforma DTOs a Entidades                       │
│     - Cifra contraseñas                                 │
│     - Llama a Repositories                              │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│  6. REPOSITORIES (JpaRepository)                        │
│     - UsuarioRepository                                 │
│     - RolRepository                                     │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│  7. BASE DE DATOS (PostgreSQL)                          │
│     - Tabla usuarios                                    │
│     - Tabla roles                                       │
│     - Tabla usuarios_roles (Many-to-Many)               │
└─────────────────────────────────────────────────────────┘
                     │
                     ↓ (Respuesta hacia arriba)
┌─────────────────────────────────────────────────────────┐
│  8. DTO RESPONSE                                        │
│     - Service construye UsuarioResponse                 │
│     - Controller devuelve como JSON                     │
└─────────────────────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│  9. GLOBAL EXCEPTION HANDLER                            │
│     - Captura excepciones                               │
│     - Devuelve ErrorResponse                            │
└─────────────────────────────────────────────────────────┘
```

---

## Estructura de Archivos

```
src/main/java/com/project/
│
├── controller/
│   └── UsuarioController.java          (Capa de presentación - endpoints REST)
│
├── dto/
│   ├── UsuarioCrearRequest.java        (Request DTO para crear)
│   ├── UsuarioActualizarRequest.java   (Request DTO para actualizar)
│   └── UsuarioResponse.java            (Response DTO)
│
├── service/
│   ├── UsuarioService.java             (Interfaz del servicio)
│   └── impl/
│       └── UsuarioServiceImpl.java     (Implementación con lógica de negocio)
│
├── repository/
│   ├── UsuarioRepository.java          (Acceso a datos - usuarios)
│   └── RolRepository.java              (Acceso a datos - roles)
│
├── entity/
│   ├── Usuario.java                    (Entidad JPA - usuarios)
│   └── Rol.java                        (Entidad JPA - roles)
│
├── enums/
│   └── ROL.java                        (Enum con valores: INTEGRADOR, GESTOR, etc.)
│
├── validator/
│   ├── ExisteRoles.java                (Anotación custom)
│   └── ExisteRolesValidator.java       (Lógica de validación)
│
└── exceptions/
    ├── GlobalExceptionHandler.java     (Manejo global de errores)
    └── ErrorResponse.java              (Estructura de respuesta de error)
```

---

## Flujo Completo por Operación

### 1️⃣ CREAR USUARIO - `POST /api/v1/usuarios/crear`

#### **Paso 1: Cliente envía petición HTTP**
```json
POST http://localhost:8080/api/v1/usuarios/crear
Content-Type: application/json

{
  "nombres": "Juan Carlos",
  "apellidos": "Pérez González",
  "numeroCelular": "3001234567",
  "correo": "juan.perez@example.com",
  "password": "password123",
  "roles": ["GESTOR", "REVISOR"]
}
```

#### **Paso 2: Controller recibe la petición**
```java
@PostMapping("crear")
public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioCrearRequest request)
```

**¿Qué hace `@Valid`?**
- Spring ejecuta automáticamente las validaciones de `UsuarioCrearRequest`:
  - `@NotBlank` en nombres, apellidos, correo, password
  - `@Size` para longitud de campos
  - `@Email` para formato de correo
  - `@Pattern` para número de celular colombiano
  - **`@ExisteRoles`** ← Validador CUSTOM que verifica en BD

#### **Paso 3: Validador Custom - `ExisteRolesValidator`**
```java
@Component
public class ExisteRolesValidator implements ConstraintValidator<ExisteRoles, Set<ROL>>
```

**¿Qué hace?**
1. Recibe el `Set<ROL>` del request (["GESTOR", "REVISOR"])
2. Para cada rol, busca en la base de datos:
   ```java
   rolRepository.findByNombreRol(nombre).isEmpty()
   ```
3. Si algún rol NO existe en BD, la validación **falla** y devuelve error 400
4. Si todos existen, continúa al Service

#### **Paso 4: Service Interface - `UsuarioService`**
Define el contrato:
```java
public interface UsuarioService {
    UsuarioResponse crearUsuario(UsuarioCrearRequest usuarioCrearRequest);
}
```

#### **Paso 5: Service Implementation - `UsuarioServiceImpl`**
```java
@Service
public class UsuarioServiceImpl implements UsuarioService
```

**Flujo interno del método `crearUsuario`:**

**5.1 - Buscar entidades Rol en BD**
```java
Set<Rol> roles = new HashSet<>();
request.getRoles().forEach(rolNombre -> {
    Rol rol = rolRepository.findByNombreRol(rolNombre)
        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + rolNombre));
    roles.add(rol);
});
```

**5.2 - Crear nueva entidad Usuario**
```java
Usuario nuevoUsuario = new Usuario();
nuevoUsuario.setNombres(request.getNombres());
nuevoUsuario.setApellidos(request.getApellidos());
nuevoUsuario.setNumeroCelular(request.getNumeroCelular());
nuevoUsuario.setCorreo(request.getCorreo());

// ⚠️ IMPORTANTE: Cifrar contraseña antes de guardar
nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));

// Asignar roles
nuevoUsuario.setRoles(roles);
```

**5.3 - Guardar en BD con Repository**
```java
Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
```

**5.4 - Construir DTO de respuesta**
```java
return UsuarioResponse.builder()
    .id(usuarioGuardado.getId())
    .nombres(usuarioGuardado.getNombres())
    .apellidos(usuarioGuardado.getApellidos())
    .numeroCelular(usuarioGuardado.getNumeroCelular())
    .correo(usuarioGuardado.getCorreo())
    .roles(roles.stream()
        .map(rol -> rol.getNombreRol().name())
        .collect(Collectors.toSet()))
    .build();
```

#### **Paso 6: Repository - `UsuarioRepository`**
```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
}
```

- `save()` es heredado de `JpaRepository`
- Hibernate genera el SQL: `INSERT INTO usuarios (...) VALUES (...)`
- También inserta en `usuarios_roles` (tabla intermedia Many-to-Many)

#### **Paso 7: Controller devuelve respuesta**
```java
UsuarioResponse usuarioCreado = usuarioService.crearUsuario(request);
return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
```

**Respuesta HTTP:**
```json
HTTP/1.1 201 Created

{
  "id": 1,
  "nombres": "Juan Carlos",
  "apellidos": "Pérez González",
  "numeroCelular": "3001234567",
  "correo": "juan.perez@example.com",
  "roles": ["GESTOR", "REVISOR"]
}
```

**⚠️ Nota:** La contraseña **NO** se devuelve en la respuesta (por seguridad)

---

### 2️⃣ ACTUALIZAR USUARIO - `PUT /api/v1/usuarios/actualizar/{id}`

#### **Petición HTTP**
```json
PUT http://localhost:8080/api/v1/usuarios/actualizar/1
Content-Type: application/json

{
  "nombres": "Juan Carlos Editado",
  "apellidos": "Pérez González",
  "numeroCelular": "3009876543",
  "correo": "juan.perez.nuevo@example.com",
  "roles": ["APROBADOR"]
}
```

#### **Flujo (similar a crear, con diferencias clave)**

**1. Controller**
```java
@PutMapping("actualizar/{id}")
public ResponseEntity<?> actualizarUsuario(
    @PathVariable("id") Long id,
    @Valid @RequestBody UsuarioActualizarRequest request)
```

**2. Service - `actualizarUsuario`**

**Diferencias clave con crear:**
- ✅ Busca el usuario existente por ID
  ```java
  Usuario usuario = usuarioRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
  ```
- ✅ **NO** cifra contraseña (no se envía en request)
- ✅ Actualiza campos sobre entidad existente
  ```java
  usuario.setNombres(request.getNombres());
  usuario.setApellidos(request.getApellidos());
  // ... etc
  ```
- ✅ Usa `@Transactional` para garantizar consistencia

**3. Respuesta**
```json
HTTP/1.1 200 OK

{
  "id": 1,
  "nombres": "Juan Carlos Editado",
  "apellidos": "Pérez González",
  "numeroCelular": "3009876543",
  "correo": "juan.perez.nuevo@example.com",
  "roles": ["APROBADOR"]
}
```

---

### 3️⃣ ELIMINAR USUARIO - `DELETE /api/v1/usuarios/eliminar/{id}`

#### **Petición HTTP**
```
DELETE http://localhost:8080/api/v1/usuarios/eliminar/1
```

#### **Flujo**

**1. Controller**
```java
@DeleteMapping("eliminar/{id}")
public ResponseEntity<?> eliminarUsuario(@PathVariable("id") Long id) {
    usuarioService.eliminarUsuario(id);
    return ResponseEntity.ok().build();
}
```

**2. Service**
```java
@Override
public void eliminarUsuario(Long id) {
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    usuarioRepository.delete(usuario);
}
```

**⚠️ IMPORTANTE: Borrado Lógico (Soft Delete)**

La entidad `Usuario` tiene:
```java
@SQLDelete(sql = "UPDATE usuarios SET activo = false WHERE id = ?")
@SQLRestriction("activo = true")
```

**Esto significa:**
- ✅ `delete(usuario)` NO borra físicamente el registro
- ✅ Ejecuta `UPDATE usuarios SET activo = false WHERE id = ?`
- ✅ En consultas futuras, solo trae usuarios con `activo = true`

**3. Respuesta**
```
HTTP/1.1 200 OK
(sin body)
```

---

## Componentes Detallados

### 📦 DTOs (Data Transfer Objects)

#### **¿Por qué usar DTOs?**
- ✅ **Seguridad**: No exponer campos sensibles de entidades (ej: password)
- ✅ **Validación**: Validar datos de entrada antes de llegar al Service
- ✅ **Desacoplamiento**: Frontend no depende de estructura de entidades JPA
- ✅ **Control de datos**: Diferentes DTOs para crear/actualizar/respuesta

#### **UsuarioCrearRequest**
```java
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UsuarioCrearRequest {
    @NotBlank(message = "El nombre no puede ser vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 carácteres")
    private String nombres;
    
    // ... otros campos con validaciones
    
    @ExisteRoles  // ← Validador custom
    private Set<ROL> roles;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min=8, max=32, message = "La contraseña debe tener entre 8 y 32 carácteres")
    private String password;
}
```

**Validaciones estándar:**
- `@NotBlank`: Campo no puede ser vacío o solo espacios
- `@Size`: Longitud mínima/máxima
- `@Email`: Formato válido de correo
- `@Pattern`: Expresión regular (celular colombiano: 30X-32X + 8 dígitos)

#### **UsuarioActualizarRequest**
**Diferencia clave:** NO tiene campo `password` (no se puede cambiar por este endpoint)

#### **UsuarioResponse**
```java
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class UsuarioResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String numeroCelular;
    private String correo;
    private Set<String> roles;  // ← String, no Rol entity
}
```

**⚠️ Notas:**
- NO tiene `password` (nunca devolver contraseñas)
- `roles` es `Set<String>` (nombres de roles, no entidades completas)
- Usa `@Builder` para construcción fluida

---

### 🗃️ Entidades

#### **Usuario**
```java
@Data
@Entity
@SQLDelete(sql = "UPDATE usuarios SET activo = false WHERE id = ?")
@SQLRestriction("activo = true")
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombres;
    private String apellidos;
    private String numeroCelular;
    private String correo;
    private String password;
    private Boolean activo = Boolean.TRUE;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuarios_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles;
}
```

**Características importantes:**
- `@SQLDelete` y `@SQLRestriction`: Borrado lógico
- `@ManyToMany(fetch = EAGER)`: Carga roles automáticamente
- `@JoinTable`: Define tabla intermedia `usuarios_roles`
- `implements UserDetails`: Para integración con Spring Security

#### **Rol**
```java
@Data
@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private ROL nombreRol;
}
```

**⚠️ Importante:**
- `@Enumerated(EnumType.STRING)`: Guarda "GESTOR", no 0, 1, 2...
- `unique = true`: No puede haber dos roles con mismo nombre

#### **Enum ROL**
```java
public enum ROL {
    INTEGRADOR,
    GESTOR,
    REVISOR,
    APROBADOR
}
```

---

### 🔍 Repositories

```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    
    @Transactional
    void deleteById(Long id);
}
```

**Métodos heredados de `JpaRepository`:**
- `save(Usuario)` - Insertar/actualizar
- `findById(Long)` - Buscar por ID
- `delete(Usuario)` - Eliminar (respeta @SQLDelete)
- `findAll()` - Listar todos

**Métodos custom:**
- `findByCorreo(String)` - Spring Data genera query automático

```java
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombreRol(ROL nombreRol);
}
```

---

### ✅ Validadores Custom

#### **1. Anotación - `ExisteRoles.java`**
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExisteRolesValidator.class)
public @interface ExisteRoles {
    String message() default "Uno o más roles proporcionados no son válidos";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

#### **2. Validador - `ExisteRolesValidator.java`**
```java
@Component
public class ExisteRolesValidator implements ConstraintValidator<ExisteRoles, Set<ROL>> {
    
    @Autowired
    private RolRepository rolRepository;
    
    @Override
    public boolean isValid(Set<ROL> rolesNombres, ConstraintValidatorContext context) {
        if (rolesNombres == null || rolesNombres.isEmpty()) {
            return true;
        }
        
        for (ROL nombre : rolesNombres) {
            if (rolRepository.findByNombreRol(nombre).isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("El rol '" + nombre + "' no existe.")
                    .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
```

**¿Cuándo se ejecuta?**
- Cuando el Controller recibe un request con `@Valid`
- **ANTES** de llegar al Service
- Si falla, lanza `MethodArgumentNotValidException` → 400 Bad Request

---

### ⚠️ Manejo de Excepciones

#### **GlobalExceptionHandler.java**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
        RuntimeException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
        Exception ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse("Ha ocurrido un error inesperado", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
```

**Respuesta de error:**
```json
{
  "message": "Usuario no encontrado: 99",
  "path": "/api/v1/usuarios/actualizar/99",
  "timestamp": "2025-12-08T22:00:00"
}
```

---

## Guía de Replicación

### 📋 Checklist para replicar este patrón con otra entidad (ej: `Correo`)

#### **1. Crear la Entidad JPA**
```java
@Data
@Entity
@Table(name = "correos")
public class Correo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String asunto;
    private String cuerpoTexto;
    // ... otros campos
}
```

#### **2. Crear el Repository**
```java
@Repository
public interface CorreoRepository extends JpaRepository<Correo, Long> {
    // Métodos custom si es necesario
    Optional<Correo> findByRadicadoEntrada(String radicado);
}
```

#### **3. Crear los DTOs**

**CorreoCrearRequest.java**
```java
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CorreoCrearRequest {
    
    @NotBlank(message = "El asunto no puede ser vacío")
    @Size(max = 100)
    private String asunto;
    
    @NotBlank
    private String cuerpoTexto;
    
    // Validaciones custom si es necesario
    @ValidarPlazoRespuesta  // ejemplo
    private Integer plazoRespuestaEnDias;
}
```

**CorreoActualizarRequest.java** (sin los campos que no se pueden cambiar)

**CorreoResponse.java**
```java
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class CorreoResponse {
    private Long id;
    private String asunto;
    private String cuerpoTexto;
    private String estado;
    private LocalDateTime fechaRecepcion;
    // NO incluir datos sensibles
}
```

#### **4. Crear Service Interface**
```java
public interface CorreoService {
    CorreoResponse crearCorreo(CorreoCrearRequest request);
    CorreoResponse actualizarCorreo(Long id, CorreoActualizarRequest request);
    void eliminarCorreo(Long id);
    CorreoResponse obtenerCorreo(Long id);  // opcional
    List<CorreoResponse> listarCorreos();   // opcional
}
```

#### **5. Crear Service Implementation**
```java
@Service
public class CorreoServiceImpl implements CorreoService {
    
    @Autowired
    private CorreoRepository correoRepository;
    
    @Override
    public CorreoResponse crearCorreo(CorreoCrearRequest request) {
        // 1. Validar relaciones (ej: Cuenta existe)
        // 2. Crear entidad
        Correo nuevoCorreo = new Correo();
        nuevoCorreo.setAsunto(request.getAsunto());
        nuevoCorreo.setCuerpoTexto(request.getCuerpoTexto());
        // ... mapear campos
        
        // 3. Guardar
        Correo correoGuardado = correoRepository.save(nuevoCorreo);
        
        // 4. Construir response
        return CorreoResponse.builder()
            .id(correoGuardado.getId())
            .asunto(correoGuardado.getAsunto())
            // ... mapear campos
            .build();
    }
    
    // Implementar otros métodos...
}
```

#### **6. Crear Controller**
```java
@RestController
@RequestMapping("/api/v1/correos/")
public class CorreoController {
    
    private final CorreoService correoService;
    
    public CorreoController(CorreoService correoService) {
        this.correoService = correoService;
    }
    
    @PostMapping("crear")
    public ResponseEntity<?> crearCorreo(@Valid @RequestBody CorreoCrearRequest request) {
        CorreoResponse correoCreado = correoService.crearCorreo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(correoCreado);
    }
    
    @PutMapping("actualizar/{id}")
    public ResponseEntity<?> actualizarCorreo(
        @PathVariable("id") Long id,
        @Valid @RequestBody CorreoActualizarRequest request) {
        return ResponseEntity.ok(correoService.actualizarCorreo(id, request));
    }
    
    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<?> eliminarCorreo(@PathVariable("id") Long id) {
        correoService.eliminarCorreo(id);
        return ResponseEntity.ok().build();
    }
}
```

#### **7. (Opcional) Crear Validadores Custom**

Si necesitas validación especial (ej: validar que cuenta existe):

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExisteCuentaValidator.class)
public @interface ExisteCuenta {
    String message() default "La cuenta no existe";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

```java
@Component
public class ExisteCuentaValidator implements ConstraintValidator<ExisteCuenta, Long> {
    
    @Autowired
    private CuentaRepository cuentaRepository;
    
    @Override
    public boolean isValid(Long cuentaId, ConstraintValidatorContext context) {
        if (cuentaId == null) return true;
        return cuentaRepository.existsById(cuentaId);
    }
}
```

---

## Resumen de Principios Clave

### ✅ Separación de Responsabilidades
- **Controller**: Solo recibe/devuelve HTTP
- **Service**: Lógica de negocio
- **Repository**: Acceso a datos
- **DTOs**: Transferencia de datos
- **Entities**: Representación de BD

### ✅ Validación en Capas
1. **Validaciones básicas**: `@NotBlank`, `@Size`, `@Email` en DTOs
2. **Validaciones custom**: `@ExisteRoles` con acceso a BD
3. **Validaciones de negocio**: En Service (ej: "usuario ya existe")

### ✅ Transformación DTO ↔ Entity
- **Request DTO → Entity**: En Service (crear/actualizar)
- **Entity → Response DTO**: En Service (antes de devolver)
- **NO devolver entidades directamente**: Usar DTOs siempre

### ✅ Seguridad
- ✅ Cifrar contraseñas antes de guardar (`passwordEncoder.encode()`)
- ✅ NO devolver contraseñas en responses
- ✅ Borrado lógico (soft delete) con `@SQLDelete`
- ✅ Validar todos los inputs con `@Valid`

### ✅ Manejo de Errores
- ✅ Usar `Optional` y `orElseThrow()` en queries
- ✅ Lanzar excepciones descriptivas
- ✅ GlobalExceptionHandler captura y devuelve JSON consistente

---

## Comandos para Probar

### Crear Usuario
```bash
curl -X POST http://localhost:8080/api/v1/usuarios/crear \
  -H "Content-Type: application/json" \
  -d '{
    "nombres": "Juan",
    "apellidos": "Pérez",
    "numeroCelular": "3001234567",
    "correo": "juan@example.com",
    "password": "password123",
    "roles": ["GESTOR"]
  }'
```

### Actualizar Usuario
```bash
curl -X PUT http://localhost:8080/api/v1/usuarios/actualizar/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombres": "Juan Editado",
    "apellidos": "Pérez",
    "numeroCelular": "3009876543",
    "correo": "juan.nuevo@example.com",
    "roles": ["APROBADOR"]
  }'
```

### Eliminar Usuario
```bash
curl -X DELETE http://localhost:8080/api/v1/usuarios/eliminar/1
```

---

## Preguntas Frecuentes

### ❓ ¿Por qué dos DTOs distintos (Crear vs Actualizar)?
Para **actualizar NO necesitas password**, pero para **crear SÍ**. Separar DTOs permite validaciones diferentes.

### ❓ ¿Por qué Service devuelve DTO y no Entity?
Porque el Controller no debe exponer estructuras internas de BD. DTOs = contrato API estable.

### ❓ ¿Cuándo usar validador custom vs validación en Service?
- **Custom validator**: Si se puede validar ANTES de entrar al Service (ej: rol existe)
- **Service**: Si requiere lógica compleja o múltiples tablas

### ❓ ¿Por qué usar Optional en Repository?
Para evitar `NullPointerException`. Usar `.orElseThrow()` para fallar explícitamente.

### ❓ ¿Qué pasa si no uso @Transactional?
En operaciones de UPDATE, cambios pueden no persistir si hay error intermedio. `@Transactional` garantiza atomicidad.

---

**📝 Fin del documento**

Este flujo es **replicable para todas las entidades** del proyecto (Correo, Cuenta, Entidad, TipoSolicitud, etc.).
