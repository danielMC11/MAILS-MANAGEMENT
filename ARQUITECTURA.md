# Arquitectura del Proyecto y Guía para Crear Endpoints

Este documento describe la arquitectura de capas utilizada en el proyecto y los pasos a seguir para crear nuevos endpoints de forma consistente.

## Arquitectura de Capas

El proyecto sigue una arquitectura de 4 capas clásica en aplicaciones Spring Boot, diseñada para separar responsabilidades, mejorar la mantenibilidad y facilitar las pruebas.

![Diagrama de Arquitectura](https://i.imgur.com/8yK1hN0.png)

### 1. Capa de Controlador (Controller)

-   **Responsabilidad:** Exponer los endpoints de la API REST, manejar las solicitudes HTTP y las respuestas.
-   **Ubicación:** `src/main/java/com/project/controller/`
-   **Características:**
    -   Clases anotadas con `@RestController`.
    -   Cada método corresponde a un endpoint (`@GetMapping`, `@PostMapping`, etc.).
    -   Recibe **DTOs de Petición** (`...Request`) como `@RequestBody` o parámetros.
    -   Valida los datos de entrada usando `@Valid`.
    -   **No contiene lógica de negocio.** Su única función es delegar la tarea a la capa de Servicio.
    -   Retorna `ResponseEntity` con **DTOs de Respuesta** (`...Response`) y un código de estado HTTP.

### 2. Capa de Servicio (Service)

-   **Responsabilidad:** Orquestar la lógica de negocio de la aplicación.
-   **Ubicación:**
    -   Interfaces: `src/main/java/com/project/service/`
    -   Implementaciones: `src/main/java/com/project/service/impl/`
-   **Características:**
    -   Se define una interfaz (ej. `UsuarioService`) y su implementación (ej. `UsuarioServiceImpl`). Esto facilita la inyección de dependencias y las pruebas.
    -   La implementación está anotada con `@Service`.
    -   Aquí reside la lógica principal: validaciones complejas, cálculos, y la orquestación de llamadas a uno o más repositorios.
    -   Maneja las transacciones con la base de datos (`@Transactional`).
    -   Recibe datos de la capa de controlador y llama a la capa de repositorio.
    -   Transforma las **Entidades** de la base de datos en **DTOs de Respuesta** para enviarlos de vuelta al controlador.

### 3. Capa de Repositorio (Repository)

-   **Responsabilidad:** Abstraer el acceso y la manipulación de los datos en la base de datos.
-   **Ubicación:** `src/main/java/com/project/repository/`
-   **Características:**
    -   Interfaces que extienden `JpaRepository<Entidad, TipoId>`.
    -   Anotadas con `@Repository`.
    -   Proporcionan métodos CRUD (`save`, `findById`, `findAll`, `delete`) de forma automática.
    -   Se pueden definir consultas personalizadas:
        -   Siguiendo la convención de nombres de métodos de Spring Data JPA (ej. `findByCorreoCuenta(...)`).
        -   Usando la anotación `@Query` para escribir consultas en JPQL o SQL nativo.

### 4. Capa de Entidad (Entity) y DTOs

-   **Entidades:**
    -   **Responsabilidad:** Representar las tablas de la base de datos como objetos Java.
    -   **Ubicación:** `src/main/java/com/project/entity/`
    -   Clases POJO anotadas con `@Entity`, `@Table`, `@Id`, `@Column`, y relaciones (`@ManyToOne`, etc.).
    -   **Importante:** Las entidades **nunca** deben ser expuestas directamente en los endpoints.

-   **DTOs (Data Transfer Objects):**
    -   **Responsabilidad:** Transportar datos entre el cliente (Postman, Frontend) y la capa de controlador.
    -   **Ubicación:** `src/main/java/com/project/dto/`
    -   Son clases POJO simples que se utilizan para:
        -   **Peticiones (`...Request.java`):** Para modelar los datos que se reciben en un `POST` o `PUT`.
        -   **Respuestas (`...Response.java`):** Para modelar los datos que se envían como respuesta, seleccionando solo la información necesaria y evitando exponer la estructura interna de la base de datos.

---

## Guía Paso a Paso para Crear un Nuevo Endpoint

Vamos a usar como ejemplo la creación de un endpoint para **buscar Cuentas**.

### Paso 1: Definir la Consulta en el Repositorio

Si la consulta es compleja y no puede ser resuelta por los métodos por defecto de `JpaRepository`, define un nuevo método en la interfaz correspondiente.

**Archivo:** `src/main/java/com/project/repository/CuentaRepository.java`

```java
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    // ... otros métodos

    @Query("SELECT c FROM Cuenta c JOIN FETCH c.entidad e WHERE " +
           "LOWER(c.nombreCuenta) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.correoCuenta) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.nombreEntidad) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Cuenta> search(@Param("query") String query);
}
```

### Paso 2: Añadir el Método en la Interfaz del Servicio

Declara el nuevo método de negocio en la interfaz del servicio.

**Archivo:** `src/main/java/com/project/service/CuentaService.java`

```java
public interface CuentaService {
    // ... otros métodos

    List<CuentaResponse> buscarCuentas(String query);
}
```

### Paso 3: Implementar la Lógica en el Servicio

Implementa el método en la clase de servicio. Aquí es donde se llama al repositorio y se mapean las entidades a DTOs.

**Archivo:** `src/main/java/com/project/service/impl/CuentaServiceImpl.java`

```java
@Service
public class CuentaServiceImpl implements CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    // ...

    @Override
    public List<CuentaResponse> buscarCuentas(String query) {
        // 1. Llamar al método del repositorio
        List<Cuenta> cuentas = cuentaRepository.search(query);

        // 2. Mapear la lista de Entidades a una lista de DTOs
        return cuentas.stream()
                .map(this::toCuentaResponse) // Usando un método helper
                .collect(Collectors.toList());
    }

    // Método helper para no repetir código
    private CuentaResponse toCuentaResponse(Cuenta cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getId())
                .nombreCuenta(cuenta.getNombreCuenta())
                .correoCuenta(cuenta.getCorreoCuenta())
                .entidadId(cuenta.getEntidad().getId())
                .nombreEntidad(cuenta.getEntidad().getNombreEntidad())
                .build();
    }
}
```

### Paso 4: Exponer el Endpoint en el Controlador

Finalmente, crea el método en el controlador que expondrá la funcionalidad a través de la API REST.

**Archivo:** `src/main/java/com/project/controller/CuentaController.java`

```java
@RestController
@RequestMapping("/api/v1/cuentas/")
public class CuentaController {

    private final CuentaService cuentaService;

    // ... constructor

    @GetMapping("/buscar")
    public ResponseEntity<List<CuentaResponse>> buscarCuentas(@RequestParam("query") String query) {
        List<CuentaResponse> cuentas = cuentaService.buscarCuentas(query);
        return ResponseEntity.ok(cuentas);
    }
}
```

Siguiendo estos pasos, te aseguras de mantener la estructura limpia y ordenada del proyecto, facilitando su crecimiento y mantenimiento a futuro.
