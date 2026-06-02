package org.harold.plateforme.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration temporaire de sécurité pour les tests.
 *
 * <p>Cette configuration autorise tous les endpoints sans
 * authentification pour permettre les tests Postman.
 * Elle sera remplacée par la configuration JWT complète
 * après la phase de tests.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres de sécurité.
     *
     * <p>Désactive CSRF et autorise toutes les requêtes
     * pour la phase de tests.</p>
     *
     * @param http  l'objet HttpSecurity à configurer
     * @return      la chaîne de filtres configurée
     * @throws Exception si erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
        return http.build();
    }
}