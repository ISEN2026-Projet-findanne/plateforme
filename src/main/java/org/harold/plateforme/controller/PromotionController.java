package org.harold.plateforme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.promotion.PromotionCreateDTO;
import org.harold.plateforme.dto.promotion.PromotionDTO;
import org.harold.plateforme.dto.promotion.PromotionUpdateDTO;
import org.harold.plateforme.security.SecurityUtils;
import org.harold.plateforme.service.PromotionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller de gestion des promotions.
 *
 * <p>Gère la création, modification, suppression
 * et consultation des promotions. L'identité de
 * l'utilisateur est extraite du token JWT via SecurityUtils.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    /**
     * Crée une nouvelle promotion.
     *
     * @param dto       les données de création
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO de la promotion créée
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionDTO> creer(
            @Valid @RequestBody PromotionCreateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(promotionService.creer(
                        dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Modifie une promotion existante.
     *
     * @param id        identifiant de la promotion
     * @param dto       les nouvelles données
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO de la promotion modifiée
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody PromotionUpdateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(promotionService.modifier(
                id, dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Supprime une promotion.
     *
     * <p>Uniquement si aucun étudiant actif dans la promotion.</p>
     *
     * @param id        identifiant de la promotion
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 204 si suppression réussie
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        promotionService.supprimer(
                id, adminId, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère toutes les promotions.
     *
     * @return  HTTP 200 avec la liste des promotions
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<List<PromotionDTO>> getAll() {
        return ResponseEntity.ok(promotionService.getAll());
    }

    /**
     * Récupère une promotion par son identifiant.
     *
     * @param id    identifiant de la promotion
     * @return      HTTP 200 avec le DTO de la promotion
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<PromotionDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getById(id));
    }

    /**
     * Recherche des promotions par nom.
     *
     * @param nom   le terme de recherche
     * @return      HTTP 200 avec la liste des promotions trouvées
     */
    @GetMapping("/recherche")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<List<PromotionDTO>> rechercher(
            @RequestParam String nom) {
        return ResponseEntity.ok(
                promotionService.rechercherParNom(nom));
    }
}