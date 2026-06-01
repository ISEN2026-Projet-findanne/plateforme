package org.harold.plateforme.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration générale de l'application.
 *
 * <p>Déclare les beans des librairies externes utilisés
 * dans toute l'application :</p>
 * <ul>
 *   <li>ModelMapper : conversion entité vers DTO et vice versa</li>
 *   <li>BCryptPasswordEncoder : hashage des mots de passe</li>
 * </ul>
 *
 * @author Harold
 * @version 1.0
 */
@Configuration
public class AppConfig {

    /**
     * Déclare le bean ModelMapper utilisé par tous les mappers.
     *
     * <p>La stratégie STRICT évite les mappings incorrects
     * entre champs de noms similaires mais de types différents.
     * Spring injecte ce bean partout où il est déclaré
     * comme dépendance finale.</p>
     *
     * @return  une instance configurée de ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    /**
     * Déclare le bean BCryptPasswordEncoder utilisé par Spring Security.
     *
     * <p>BCrypt est l'algorithme de hashage recommandé pour
     * les mots de passe. Il est utilisé lors de la création
     * d'un utilisateur et lors de l'authentification.</p>
     *
     * @return  une instance de BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}