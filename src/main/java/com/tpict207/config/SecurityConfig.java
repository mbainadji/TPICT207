package com.tpict207.config;

import com.tpict207.model.Utilisateur;
import com.tpict207.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.PrintWriter;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Supports {noop}, {bcrypt}, etc.
        // Also supports legacy/plain passwords by defaulting matches to noop when no {id} prefix is present.
        DelegatingPasswordEncoder pe = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        pe.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return pe;
    }

    @Bean
    public UserDetailsService userDetailsService(UtilisateurRepository utilisateurRepository) {
        return username -> {
            Utilisateur u = utilisateurRepository.findByNomUtilisateur(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

            // Spring Security expects ROLE_* for hasRole checks.
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()));

            return User.withUsername(u.getNomUtilisateur())
                    .password(u.getMotDePasse())
                    .authorities(authorities)
                    .build();
        };
    }

    @Bean
    public AuthenticationEntryPoint apiAuthEntryPoint() {
        // Always return JSON so the UI can show a clear message for both:
        // - missing Authorization header
        // - invalid Basic credentials
        return (req, res, ex) -> {
            res.setStatus(401);
            res.setCharacterEncoding("UTF-8");
            res.setContentType("application/json");
            String auth = req.getHeader("Authorization");
            String msg = (auth == null || auth.isBlank())
                    ? "Non authentifie. Merci de vous connecter."
                    : "Identifiants invalides. Verifiez utilisateur et mot de passe.";
            try (PrintWriter w = res.getWriter()) {
                w.write("{\"message\":\"" + msg + "\"}");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationEntryPoint apiAuthEntryPoint) throws Exception {
        http
                // We serve a small static UI that calls the API via fetch + Basic Auth.
                .csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")))
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(apiAuthEntryPoint)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public landing page + assets
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/etudiants.html",
                                "/cours.html",
                                "/notes.html",
                                "/assets/**",
                                "/favicon.svg"
                        ).permitAll()
                        .requestMatchers("/error").permitAll()

                        // API requires authentication
                        .requestMatchers("/api/**").authenticated()

                        // Everything else
                        .anyRequest().denyAll()
                )
                .httpBasic(basic -> basic.authenticationEntryPoint(apiAuthEntryPoint));

        // Keep strict defaults (no extra tooling exposed from the UI).

        return http.build();
    }
}
