package org.harold.plateforme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.anneeacademique.AnneeAcademiqueCreateDTO;
import org.harold.plateforme.dto.anneeacademique.AnneeAcademiqueDTO;
import org.harold.plateforme.security.SecurityUtils;
import org.harold.plateforme.service.AnneeAcademiqueService;
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
 * Controller de gestion des années académiques.
 *
 * <p>Gère la création, modification et activation
 * des années académiques. La création et l'activation
 * sont réservées à l'admin. L'identité de l'utilisateur
 * est extraite du token JWT via SecurityUtils.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/annees-academiques")
@RequiredArgsConstructor
public class AnneeAcademiqueController {

    private final AnneeAcademiqueService anneeAcademiqueService;

    /**
     * Crée une nouvelle année académique.
     *
     * @param dto       les données de création
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO de l'année créée
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnneeAcademiqueDTO> creer(
            @Valid @RequestBody AnneeAcademiqueCreateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(anneeAcademiqueService.creer(
                        dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Modifie une année académique existante.
     *
     * @param id        identifiant de l'année à modifier
     * @param dto       les nouvelles données
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO de l'année modifiée
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnneeAcademiqueDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody AnneeAcademiqueCreateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(anneeAcademiqueService.modifier(
                id, dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Active une année académique.
     *
     * <p>Désactive automatiquement toutes les autres années.
     * Une seule année peut être active à la fois.</p>
     *
     * @param id        identifiant de l'année à activer
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO de l'année activée
     */
    @PutMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnneeAcademiqueDTO> activer(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(anneeAcademiqueService.activer(
                id, adminId, request.getRemoteAddr()));
    }

    /**
     * Récupère toutes les années académiques.
     *
     * @return  HTTP 200 avec la liste des années académiques
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<List<AnneeAcademiqueDTO>> getAll() {
        return ResponseEntity.ok(anneeAcademiqueService.getAll());
    }

    /**
     * Récupère l'année académique active.
     *
     * @return  HTTP 200 avec le DTO de l'année active
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<AnneeAcademiqueDTO> getActive() {
        return ResponseEntity.ok(anneeAcademiqueService.getActive());
    }
}