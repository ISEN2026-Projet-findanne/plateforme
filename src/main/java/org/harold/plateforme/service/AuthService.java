package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.auth.LoginRequest;
import org.harold.plateforme.dto.auth.LoginResponse;
import org.harold.plateforme.dto.auth.RefreshTokenRequest;
import org.harold.plateforme.entity.Utilisateur;
import org.harold.plateforme.exception.ValidationException;
import org.harold.plateforme.security.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service d'authentification.
 *
 * <p>Gère le login, la validation des credentials,
 * la génération des tokens JWT et le refresh token.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurService utilisateurService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    // ===== LOGIN =====

    /**
     * Authentifie un utilisateur et génère les tokens JWT.
     *
     * <p>Vérifie l'email et le mot de passe, puis génère
     * un access token et un refresh token si les credentials
     * sont valides.</p>
     *
     * @param dto   les credentials de connexion
     * @return      la réponse avec les tokens et infos utilisateur
     * @throws ValidationException  si email ou mot de passe incorrect
     *                              ou si le compte est désactivé
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest dto) {

        // 1. Récupérer l'utilisateur par email
        Utilisateur utilisateur;
        try {
            utilisateur = utilisateurService.getByEmail(dto.getEmail());
        } catch (Exception e) {
            throw new ValidationException(
                    "email", "Email ou mot de passe incorrect");
        }

        // 2. Vérifier que le compte est actif
        if (!utilisateur.isActif()) {
            throw new ValidationException(
                    "compte", "Ce compte est désactivé");
        }

        // 3. Vérifier le mot de passe
        if (!passwordEncoder.matches(
                dto.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new ValidationException(
                    "motDePasse", "Email ou mot de passe incorrect");
        }

        // 4. Générer les tokens
        String accessToken = jwtTokenProvider.genererAccessToken(
                utilisateur.getEmail(),
                utilisateur.getRole().name());
        String refreshToken = jwtTokenProvider.genererRefreshToken(
                utilisateur.getEmail());

        // 5. Retourner la réponse
        return new LoginResponse(
                accessToken,
                refreshToken,
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole().name());
    }

    // ===== REFRESH TOKEN =====

    /**
     * Génère un nouvel access token depuis un refresh token valide.
     *
     * <p>Valide le refresh token, récupère l'utilisateur
     * et génère un nouvel access token.</p>
     *
     * @param dto   le refresh token
     * @return      la réponse avec le nouvel access token
     * @throws ValidationException  si le refresh token est invalide
     *                              ou expiré
     */
    @Transactional(readOnly = true)
    public LoginResponse refresh(RefreshTokenRequest dto) {

        // 1. Valider le refresh token
        jwtTokenProvider.validerToken(dto.getRefreshToken());

        // 2. Extraire l'email depuis le refresh token
        String email = jwtTokenProvider.getEmailDepuisToken(
                dto.getRefreshToken());

        // 3. Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurService.getByEmail(email);

        // 4. Vérifier que le compte est toujours actif
        if (!utilisateur.isActif()) {
            throw new ValidationException(
                    "compte", "Ce compte est désactivé");
        }

        // 5. Générer un nouvel access token
        String nouvelAccessToken = jwtTokenProvider.genererAccessToken(
                utilisateur.getEmail(),
                utilisateur.getRole().name());

        // 6. Retourner la réponse avec le nouveau token
        return new LoginResponse(
                nouvelAccessToken,
                dto.getRefreshToken(),
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole().name());
    }
}