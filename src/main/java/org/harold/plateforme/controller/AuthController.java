package org.harold.plateforme.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.auth.LoginRequest;
import org.harold.plateforme.dto.auth.LoginResponse;
import org.harold.plateforme.dto.auth.RefreshTokenRequest;
import org.harold.plateforme.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de gestion de l'authentification.
 *
 * <p>Endpoints publics — aucun token JWT requis.
 * Gère le login et le refresh token.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authentifie un utilisateur et retourne les tokens JWT.
     *
     * @param dto   les credentials de connexion
     * @return      HTTP 200 avec tokens et infos utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    /**
     * Renouvelle l'access token depuis un refresh token valide.
     *
     * @param dto   le refresh token
     * @return      HTTP 200 avec nouveau access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest dto) {
        return ResponseEntity.ok(authService.refresh(dto));
    }
}