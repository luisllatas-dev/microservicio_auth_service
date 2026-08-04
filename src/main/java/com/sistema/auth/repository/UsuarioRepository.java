package com.sistema.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistema.auth.model.Usuario;

/**
 * Repositorio JPA para la entidad Usuario.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su email (usado en el login y validación de registro).
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si ya existe un usuario con un email determinado.
     */
    boolean existsByEmail(String email);
}
