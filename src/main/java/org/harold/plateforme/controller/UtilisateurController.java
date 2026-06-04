package org.harold.plateforme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.utilisateur.UtilisateurCreateDTO;
import org.harold.plateforme.dto.utilisateur.UtilisateurDTO;
import org.harold.plateforme.dto.utilisateur.UtilisateurUpdateDTO;
import org.harold.plateforme.security.SecurityUtils;
import org.harold.plateforme.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller de gestion des utilisateurs.
 *
 * <p>Accessible uniquement par l'administrateur.
 * Gère la création, modification et consultation
 * des comptes utilisateurs. L'identité de l'admin
 * est extraite du token JWT via SecurityUtils.</p>
 *
 * @author Harold
 * @version 1.0
 */
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    /**
     * Crée un nouvel utilisateur.
     *
     * @param dto       les données de création
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO de l'utilisateur créé
     */
    @PostMapping
    public ResponseEntity<UtilisateurDTO> creer(
            @Valid @RequestBody UtilisateurCreateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(utilisateurService.creer(
                        dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Modifie un utilisateur existant.
     *
     * @param id        identifiant de l'utilisateur à modifier
     * @param dto       les nouvelles données
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO de l'utilisateur modifié
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurUpdateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(utilisateurService.modifier(
                id, dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return  HTTP 200 avec la liste des utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<UtilisateurDTO>> getAll() {
        return ResponseEntity.ok(utilisateurService.getAll());
    }

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id    identifiant de l'utilisateur
     * @return      HTTP 200 avec le DTO de l'utilisateur
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getById(id));
    }
}