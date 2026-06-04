package org.harold.plateforme.security;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.entity.Utilisateur;
import org.harold.plateforme.repository.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du UserDetailsService de Spring Security.
 *
 * <p>Charge un utilisateur depuis la base de données à partir
 * de son email et le convertit au format UserDetails compris
 * par Spring Security. Le rôle de l'utilisateur devient
 * une autorité (authority).</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    /**
     * Charge un utilisateur par son email (identifiant Spring).
     *
     * @param email     l'email de l'utilisateur
     * @return          les détails de l'utilisateur pour Spring Security
     * @throws UsernameNotFoundException    si aucun utilisateur
     *                                      ne correspond à cet email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Aucun utilisateur avec l'email : " + email));

        // Construire les autorités à partir du rôle
        List<SimpleGrantedAuthority> autorites = List.of(
                new SimpleGrantedAuthority(utilisateur.getRole().name()));

        // Retourner un UserDetails standard de Spring
        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .authorities(autorites)
                .disabled(!utilisateur.isActif())
                .build();
    }
}