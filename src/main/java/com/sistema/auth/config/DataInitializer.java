package com.sistema.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sistema.auth.model.Rol;
import com.sistema.auth.model.Usuario;
import com.sistema.auth.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inicializador de datos de prueba para la base de datos solicitudes_auth.
 * Si la base de datos está vacía, registra automáticamente el usuario Administrador inicial.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!usuarioRepository.existsByEmail("admin@sistema.com")) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@sistema.com");
            admin.setPassword(passwordEncoder.encode("Admin123#"));
            admin.setRol(Rol.ROLE_ADMINISTRADOR);
            admin.setActivo(true);
            usuarioRepository.save(admin);
            log.info("Usuario Administrador por defecto (admin@sistema.com) creado exitosamente.");
        }
    }
}
