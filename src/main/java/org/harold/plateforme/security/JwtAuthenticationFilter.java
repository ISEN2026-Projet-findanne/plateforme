package org.harold.plateforme.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification JWT.
 *
 * <p>S'exécute une fois par requête. Extrait le token JWT
 * de l'en-tête Authorization, le valide et place l'utilisateur
 * authentifié dans le contexte de sécurité de Spring.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    /** Préfixe attendu dans l'en-tête Authorization. */
    private static final String PREFIXE_BEARER = "Bearer ";

    /**
     * Filtre chaque requête pour authentifier via le token JWT.
     *
     * @param request       la requête HTTP entrante
     * @param response      la réponse HTTP
     * @param filterChain   la chaîne de filtres à poursuivre
     * @throws ServletException en cas d'erreur de servlet
     * @throws IOException      en cas d'erreur d'entrée/sortie
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extraire le token de l'en-tête Authorization
        String token = extraireToken(request);

        // 2. Si pas de token, on continue sans authentifier
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Valider le token
            if (jwtTokenProvider.validerToken(token)) {

                // 4. Extraire les infos du token
                String email = jwtTokenProvider.getEmailDepuisToken(token);
                String role = jwtTokenProvider.getRoleDepuisToken(token);
                Long userId = jwtTokenProvider.getUserIdDepuisToken(token);

                // 5. Construire le principal (Piste B : id dans le contexte)
                UtilisateurPrincipal principal =
                        new UtilisateurPrincipal(userId, email, role);

                // 6. Créer l'authentification
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities());

                // 7. Placer dans le contexte de sécurité
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Token invalide ou expiré : on n'authentifie pas,
            // le contexte reste vide. La config de sécurité
            // renverra un 401 si l'endpoint est protégé.
            SecurityContextHolder.clearContext();
        }

        // 8. Poursuivre la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT depuis l'en-tête Authorization.
     *
     * @param request   la requête HTTP
     * @return          le token sans le préfixe "Bearer ",
     *                  ou null si absent
     */
    private String extraireToken(HttpServletRequest request) {
        String entete = request.getHeader("Authorization");
        if (entete != null && entete.startsWith(PREFIXE_BEARER)) {
            return entete.substring(PREFIXE_BEARER.length());
        }
        return null;
    }
}