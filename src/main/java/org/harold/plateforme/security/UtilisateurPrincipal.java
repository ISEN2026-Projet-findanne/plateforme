package org.harold.plateforme.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Représente l'utilisateur authentifié dans le contexte
 * de sécurité Spring.
 *
 * <p>Étend les informations standard de UserDetails en y
 * ajoutant l'identifiant et le rôle de l'utilisateur,
 * afin que les controllers puissent récupérer l'id sans
 * accès à la base de données.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
public class UtilisateurPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String role;

    /**
     * Construit le principal à partir des données du token.
     *
     * @param id     identifiant de l'utilisateur
     * @param email  email de l'utilisateur
     * @param role   rôle de l'utilisateur (ex: ROLE_ADMIN)
     */
    public UtilisateurPrincipal(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    /**
     * Retourne les autorités (le rôle) de l'utilisateur.
     *
     * @return  la liste des autorités
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    /**
     * Mot de passe non utilisé (authentification par token).
     *
     * @return  null
     */
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * L'identifiant Spring est l'email.
     *
     * @return  l'email de l'utilisateur
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}