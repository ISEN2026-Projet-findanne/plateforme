package org.harold.plateforme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.classe.ClasseCreateDTO;
import org.harold.plateforme.dto.classe.ClasseDTO;
import org.harold.plateforme.dto.classe.ClassePromotionDTO;
import org.harold.plateforme.security.SecurityUtils;
import org.harold.plateforme.service.ClasseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * Controller de gestion des classes.
 *
 * <p>Gère la création et modification des classes statiques
 * ainsi que leur attribution aux promotions. L'identité de
 * l'utilisateur est extraite du token JWT via SecurityUtils.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClasseController {

    private final ClasseService classeService;

    /**
     * Crée une nouvelle classe statique.
     *
     * @param dto       les données de création
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO de la classe créée
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClasseDTO> creer(
            @Valid @RequestBody ClasseCreateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(classeService.creer(
                        dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Modifie une classe existante.
     *
     * @param id        identifiant de la classe
     * @param dto       les nouvelles données
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO de la classe modifiée
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClasseDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ClasseCreateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(classeService.modifier(
                id, dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Attribue une classe à une promotion
     * pour une année académique.
     *
     * @param dto       les données d'attribution
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO de l'attribution
     */
    @PostMapping("/attribution")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassePromotionDTO> attribuer(
            @Valid @RequestBody ClassePromotionDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(classeService.attribuer(
                        dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Récupère toutes les classes.
     *
     * @return  HTTP 200 avec la liste des classes
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<List<ClasseDTO>> getAll() {
        return ResponseEntity.ok(classeService.getAll());
    }

    /**
     * Récupère une classe par son identifiant.
     *
     * @param id    identifiant de la classe
     * @return      HTTP 200 avec le DTO de la classe
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<ClasseDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(classeService.getById(id));
    }

    /**
     * Récupère les attributions d'une promotion
     * pour une année académique.
     *
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des attributions
     */
    @GetMapping("/promotion/{promotionId}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<List<ClassePromotionDTO>> getByPromotion(
            @PathVariable Long promotionId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                classeService.getByPromotionAndAnnee(
                        promotionId, anneeAcademiqueId));
    }
}