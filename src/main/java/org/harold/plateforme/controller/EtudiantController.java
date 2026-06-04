package org.harold.plateforme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.etudiant.EtudiantCreateDTO;
import org.harold.plateforme.dto.etudiant.EtudiantDTO;
import org.harold.plateforme.dto.etudiant.EtudiantUpdateDTO;
import org.harold.plateforme.entity.Inscription;
import org.harold.plateforme.security.SecurityUtils;
import org.harold.plateforme.service.EtudiantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Controller de gestion des étudiants.
 *
 * <p>Gère la création, modification et consultation
 * des étudiants ainsi que leur mobilité entre promotions.
 * L'identité de l'utilisateur qui effectue l'action est
 * extraite du token JWT via SecurityUtils.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
public class EtudiantController {

    private final EtudiantService etudiantService;

    /**
     * Crée un nouvel étudiant et son inscription.
     *
     * @param dto       les données de création
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO de l'étudiant créé
     */
    @PostMapping
    public ResponseEntity<EtudiantDTO> creer(
            @Valid @RequestBody EtudiantCreateDTO dto,
            HttpServletRequest request) {
        Long utilisateurId = SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(etudiantService.creer(
                        dto, utilisateurId,
                        request.getRemoteAddr()));
    }

    /**
     * Modifie un étudiant existant.
     *
     * @param id        identifiant de l'étudiant
     * @param dto       les nouvelles données
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO de l'étudiant modifié
     */
    @PutMapping("/{id}")
    public ResponseEntity<EtudiantDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody EtudiantUpdateDTO dto,
            HttpServletRequest request) {
        Long utilisateurId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(etudiantService.modifier(
                id, dto, utilisateurId,
                request.getRemoteAddr()));
    }

    /**
     * Change la promotion d'un étudiant.
     *
     * <p>Désactive l'inscription active et crée
     * une nouvelle inscription.</p>
     *
     * @param id                identifiant de l'étudiant
     * @param promotionId       nouvelle promotion
     * @param anneeAcademiqueId année académique
     * @param niveau            nouveau niveau
     * @param motif             motif du changement
     * @param request           la requête HTTP pour récupérer l'IP
     * @return                  HTTP 200 avec le DTO mis à jour
     */
    @PutMapping("/{id}/changer-promotion")
    public ResponseEntity<EtudiantDTO> changerPromotion(
            @PathVariable Long id,
            @RequestParam Long promotionId,
            @RequestParam Long anneeAcademiqueId,
            @RequestParam String niveau,
            @RequestParam(required = false) String motif,
            HttpServletRequest request) {
        Long utilisateurId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(etudiantService.changerPromotion(
                id, promotionId, anneeAcademiqueId,
                niveau, motif, utilisateurId,
                request.getRemoteAddr()));
    }

    /**
     * Récupère un étudiant par son identifiant.
     *
     * @param id    identifiant de l'étudiant
     * @return      HTTP 200 avec le DTO de l'étudiant
     */
    @GetMapping("/{id}")
    public ResponseEntity<EtudiantDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.getById(id));
    }

    /**
     * Récupère les étudiants d'une promotion
     * pour une année académique.
     *
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des étudiants
     */
    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<List<EtudiantDTO>> getByPromotion(
            @PathVariable Long promotionId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(etudiantService.getByPromotion(
                promotionId, anneeAcademiqueId));
    }

    /**
     * Recherche des étudiants par nom ou prénom.
     *
     * @param query     le terme de recherche
     * @return          HTTP 200 avec la liste des étudiants trouvés
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<EtudiantDTO>> rechercher(
            @RequestParam String query) {
        return ResponseEntity.ok(
                etudiantService.rechercher(query));
    }

    /**
     * Récupère l'historique des inscriptions d'un étudiant.
     *
     * @param id    identifiant de l'étudiant
     * @return      HTTP 200 avec la liste des inscriptions
     */
    @GetMapping("/{id}/historique-inscriptions")
    public ResponseEntity<List<Inscription>> getHistorique(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                etudiantService.getHistoriqueInscriptions(id));
    }
}