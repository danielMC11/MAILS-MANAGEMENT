package com.project.repository;

import com.project.entity.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    @Transactional
    void deleteById(Long id);

    // NUEVOS MÉTODOS PARA BÚSQUEDA
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles r " +
            "WHERE LOWER(u.nombres) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
            "LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Usuario> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    // Buscar usuarios por rol (más eficiente)
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.roles r WHERE r.nombreRol = :rol")
    List<Usuario> findByRol(@Param("rol") String rol);

    // Para paginación con eager fetching
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles")
    Page<Usuario> findAllWithRoles(Pageable pageable);
}
