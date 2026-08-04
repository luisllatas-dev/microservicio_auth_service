package com.sistema.auth.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sistema.auth.model.Usuario;
import com.sistema.auth.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementación de UserDetailsService para cargar usuarios desde la BD MySQL.
 * Spring Security usa este servicio durante la autenticación por email/password.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carga el usuario por email. Spring Security llama este método durante el login.
     * @param email el identificador del usuario (usamos email como username)
     * @return UserDetails con email, contraseña encriptada y rol
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró un usuario con el email: " + email));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name().replace("ROLE_", ""))
                .disabled(!usuario.isActivo())
                .build();
    }
}
