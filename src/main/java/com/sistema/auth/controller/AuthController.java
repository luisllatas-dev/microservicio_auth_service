package com.sistema.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.auth.dto.ActualizarUsuarioRequest;
import com.sistema.auth.dto.AuthResponse;
import com.sistema.auth.dto.LoginRequest;
import com.sistema.auth.dto.RegistroRequest;
import com.sistema.auth.model.Rol;
import com.sistema.auth.repository.UsuarioRepository;
import com.sistema.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST que expone los endpoints del auth-service.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Endpoint público para iniciar sesión.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Endpoint público para que los clientes se registren con su propio email.
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistroRequest request) {
        return new ResponseEntity<>(authService.registrar(request), HttpStatus.CREATED);
    }

    /**
     * Endpoint interno para que el monolito cree usuarios al registrar técnicos o admins.
     * Solo accesible desde la red interna (no expuesto al exterior).
     * POST /api/auth/internal/crear-usuario
     */
    @PostMapping("/internal/crear-usuario")
    public ResponseEntity<AuthResponse> crearUsuarioInterno(
            @Valid @RequestBody RegistroRequest request) {
        return new ResponseEntity<>(authService.registrar(request), HttpStatus.CREATED);
    }

    /**
     * Endpoint interno para actualizar el email o contraseña de un usuario desde el monolito.
     * POST /api/auth/internal/actualizar-usuario
     */
    @PostMapping("/internal/actualizar-usuario")
    public ResponseEntity<Void> actualizarUsuarioInterno(@RequestBody ActualizarUsuarioRequest request) {
        authService.actualizarUsuario(request.getEmailOriginal(), request.getNuevoEmail(), request.getPassword());
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint interno para eliminar la cuenta de un usuario desde el monolito.
     * DELETE /api/auth/internal/eliminar-usuario
     */
    @org.springframework.web.bind.annotation.DeleteMapping("/internal/eliminar-usuario")
    public ResponseEntity<Void> eliminarUsuarioInterno(@RequestParam String email) {
        authService.eliminarUsuario(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para validar si un email ya está registrado como usuario.
     * Útil para verificaciones internas entre microservicios.
     * GET /api/auth/existe?email=...
     */
    @GetMapping("/existe")
    public ResponseEntity<Boolean> existeUsuario(@RequestParam String email) {
        return ResponseEntity.ok(usuarioRepository.existsByEmail(email));
    }

    /**
     * Endpoint para obtener el rol de un usuario por email.
     * GET /api/auth/rol?email=...
     */
    @GetMapping("/rol")
    public ResponseEntity<String> obtenerRol(@RequestParam String email) {
        return usuarioRepository.findByEmail(email)
                .map(u -> ResponseEntity.ok(u.getRol().name()))
                .orElse(ResponseEntity.notFound().build());
    }
}
