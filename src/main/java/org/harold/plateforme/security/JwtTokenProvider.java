package org.harold.plateforme.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.harold.plateforme.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Composant de gestion des tokens JWT.
 *
 * <p>Génère, valide et parse les tokens JWT
 * pour l'authentification et le refresh token.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * Génère la clé secrète depuis la configuration.
     *
     * @return  la clé secrète HMAC-SHA
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Génère un access token JWT pour un utilisateur.
     *
     * <p>Le token contient l'email comme subject
     * et expire après jwt.expiration millisecondes (24h).</p>
     *
     * @param email     email de l'utilisateur
     * @param role      rôle de l'utilisateur
     * @return          le token JWT signé
     */
    public String genererAccessToken(String email, String role) {
        Date maintenant = new Date();
        Date expiration = new Date(maintenant.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(maintenant)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Génère un refresh token JWT pour un utilisateur.
     *
     * <p>Le refresh token expire après jwt.refresh-expiration
     * millisecondes (7 jours).</p>
     *
     * @param email     email de l'utilisateur
     * @return          le refresh token JWT signé
     */
    public String genererRefreshToken(String email) {
        Date maintenant = new Date();
        Date expiration = new Date(
                maintenant.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(maintenant)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrait l'email depuis un token JWT.
     *
     * @param token     le token JWT
     * @return          l'email contenu dans le token
     */
    public String getEmailDepuisToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrait le rôle depuis un access token JWT.
     *
     * @param token     le token JWT
     * @return          le rôle contenu dans le token
     */
    public String getRoleDepuisToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Valide un token JWT.
     *
     * <p>Vérifie la signature et la date d'expiration.</p>
     *
     * @param token     le token à valider
     * @return          true si le token est valide
     * @throws ValidationException  si le token est expiré ou invalide
     */
    public boolean validerToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ValidationException("token", "Le token JWT est expiré");
        } catch (Exception e) {
            throw new ValidationException("token", "Le token JWT est invalide");
        }
    }

    /**
     * Parse et retourne les claims d'un token JWT.
     *
     * @param token     le token à parser
     * @return          les claims du token
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}