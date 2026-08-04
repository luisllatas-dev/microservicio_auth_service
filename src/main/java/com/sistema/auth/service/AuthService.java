package com.sistema.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistema.auth.dto.AuthResponse;
import com.sistema.auth.dto.LoginRequest;
import com.sistema.auth.dto.RegistroRequest;
import com.sistema.auth.model.Usuario;
import com.sistema.auth.repository.UsuarioRepository;
import com.sistema.auth.security.CustomUserDetailsService;
import com.sistema.auth.security.JwtService;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de autenticación.
 * Maneja el registro de nuevos usuarios y el inicio de sesión con generación de JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Registra un nuevo usuario en la base de datos del auth-service.
     * La contraseña es encriptada con BCrypt antes de guardarse.
     * @param request datos del nuevo usuario (email, password, rol)
     * @return AuthResponse con el token JWT generado inmediatamente tras el registro
     * @throws IllegalArgumentException si el email ya está registrado
     */
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "El email " + request.getEmail() + " ya está registrado en el sistema");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generarToken(userDetails, request.getRol().name());

        return new AuthResponse(token, request.getEmail(), request.getRol().name(),
                jwtService.getExpiration());
    }

    /**
     * Autentica un usuario existente y retorna un nuevo token JWT.
     * @param request credenciales de login (email, password)
     * @return AuthResponse con el token JWT fresco
     */
    public AuthResponse login(LoginRequest request) {
        // Spring Security verifica email y contraseña con BCrypt automáticamente
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();

        String token = jwtService.generarToken(userDetails, usuario.getRol().name());

        return new AuthResponse(token, request.getEmail(), usuario.getRol().name(),
                jwtService.getExpiration());
    }

    /**
     * Actualiza el email y/o contraseña de un usuario existente en la base de datos solicitudes_auth.
     */
    public void actualizarUsuario(String emailOriginal, String nuevoEmail, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByEmail(emailOriginal)
                .orElseGet(() -> usuarioRepository.findByEmail(nuevoEmail).orElse(null));

        if (usuario != null) {
            if (nuevoEmail != null && !nuevoEmail.trim().isEmpty()) {
                usuario.setEmail(nuevoEmail);
            }
            if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            }
            usuarioRepository.save(usuario);
        }
    }

    /**
     * Elimina la cuenta de usuario de la base de datos solicitudes_auth por su email.
     */
    public void eliminarUsuario(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuarioRepository::delete);
    }
}
