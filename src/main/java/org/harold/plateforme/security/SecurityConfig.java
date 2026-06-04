package org.harold.plateforme.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de la sécurité Spring Security avec JWT.
 *
 * <p>Désactive les sessions (authentification stateless via JWT),
 * laisse les endpoints d'authentification publics et protège
 * tous les autres endpoints. Le filtre JWT est exécuté avant
 * le filtre d'authentification standard de Spring.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configure la chaîne de filtres de sécurité.
     *
     * @param http  l'objet HttpSecurity à configurer
     * @return      la chaîne de filtres configurée
     * @throws Exception    si erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                // Pas de CSRF : API REST stateless
                .csrf(csrf -> csrf.disable())

                // Pas de session : chaque requête porte son token
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                // Règles d'accès aux endpoints
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics : authentification
                        .requestMatchers("/api/auth/**").permitAll()
                        // Tout le reste exige un token valide
                        .anyRequest().authenticated())

                // Ajouter le filtre JWT avant le filtre standard
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}