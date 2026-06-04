package org.harold.plateforme.security;

import org.harold.plateforme.exception.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilitaire d'accès à l'utilisateur authentifié.
 *
 * <p>Récupère les informations de l'utilisateur connecté
 * (id, email, rôle) depuis le contexte de sécurité Spring,
 * alimenté par le filtre JWT à chaque requête.</p>
 *
 * @author Harold
 * @version 1.0
 */
public class SecurityUtils {

    /**
     * Constructeur privé — classe utilitaire statique.
     */
    private SecurityUtils() {}

    /**
     * Récupère le principal de l'utilisateur connecté.
     *
     * @return  le principal authentifié
     * @throws AccessDeniedException    si aucun utilisateur
     *                                  n'est authentifié
     */
    private static UtilisateurPrincipal getPrincipal() {
        Authentication auth = SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null
                || !(auth.getPrincipal()
                instanceof UtilisateurPrincipal principal)) {
            throw new AccessDeniedException(
                    "Aucun utilisateur authentifié");
        }
        return principal;
    }

    /**
     * Récupère l'id de l'utilisateur connecté.
     *
     * @return  l'identifiant de l'utilisateur
     */
    public static Long getCurrentUserId() {
        return getPrincipal().getId();
    }

    /**
     * Récupère l'email de l'utilisateur connecté.
     *
     * @return  l'email de l'utilisateur
     */
    public static String getCurrentUserEmail() {
        return getPrincipal().getEmail();
    }

    /**
     * Récupère le rôle de l'utilisateur connecté.
     *
     * @return  le rôle de l'utilisateur (ex: ROLE_ADMIN)
     */
    public static String getCurrentUserRole() {
        return getPrincipal().getRole();
    }
}