package org.harold.plateforme.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Objet de requête pour le renouvellement du token JWT.
 *
 * <p>Envoyé par React quand l'access token est expiré
 * pour obtenir un nouveau token sans se reconnecter.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class RefreshTokenRequest {

    /**
     * Le refresh token envoyé par React.
     * Doit être non vide.
     */
    @NotBlank(message = "Le refresh token est obligatoire")
    private String refreshToken;
}