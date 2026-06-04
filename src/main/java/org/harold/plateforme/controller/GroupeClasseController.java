package org.harold.plateforme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.groupe.AssignationEnseignantDTO;
import org.harold.plateforme.dto.groupe.AssignationEtudiantDTO;
import org.harold.plateforme.dto.groupe.GroupeClasseCreateDTO;
import org.harold.plateforme.dto.groupe.GroupeClasseDTO;
import org.harold.plateforme.dto.groupe.GroupeClasseUpdateDTO;
import org.harold.plateforme.security.SecurityUtils;
import org.harold.plateforme.service.GroupeClasseService;
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
 * Controller de gestion des groupes TD/TP/CM.
 *
 * <p>Gère la création et modification des groupes,
 * l'assignation des étudiants et des enseignants.
 * L'identité de l'utilisateur est extraite du token JWT.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/groupes")
@RequiredArgsConstructor
public class GroupeClasseController {

    private final GroupeClasseService groupeClasseService;

    /**
     * Crée un nouveau groupe TD/TP/CM.
     *
     * @param dto       les données de création
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO du groupe créé
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupeClasseDTO> creer(
            @Valid @RequestBody GroupeClasseCreateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupeClasseService.creer(
                        dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Modifie un groupe existant.
     *
     * @param id        identifiant du groupe
     * @param dto       les nouvelles données
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO du groupe modifié
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupeClasseDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody GroupeClasseUpdateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(groupeClasseService.modifier(
                id, dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Assigne un étudiant à un groupe.
     *
     * @param dto       les données d'assignation
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 si assignation réussie
     */
    @PostMapping("/assigner-etudiant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignerEtudiant(
            @Valid @RequestBody AssignationEtudiantDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        groupeClasseService.assignerEtudiant(
                dto, adminId, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retire un étudiant d'un groupe.
     *
     * @param groupeId      identifiant du groupe
     * @param etudiantId    identifiant de l'étudiant
     * @param request       la requête HTTP pour récupérer l'IP
     * @return              HTTP 204 si retrait réussi
     */
    @DeleteMapping("/{groupeId}/etudiants/{etudiantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> retirerEtudiant(
            @PathVariable Long groupeId,
            @PathVariable Long etudiantId,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        groupeClasseService.retirerEtudiant(
                etudiantId, groupeId,
                adminId, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    /**
     * Assigne un enseignant à un groupe pour une matière.
     *
     * @param dto       les données d'assignation
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 si assignation réussie
     */
    @PostMapping("/assigner-enseignant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignerEnseignant(
            @Valid @RequestBody AssignationEnseignantDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        groupeClasseService.assignerEnseignant(
                dto, adminId, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Récupère un groupe par son identifiant.
     *
     * @param id    identifiant du groupe
     * @return      HTTP 200 avec le DTO du groupe
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<GroupeClasseDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(groupeClasseService.getById(id));
    }

    /**
     * Récupère les groupes d'une matière
     * pour une année académique.
     *
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des groupes
     */
    @GetMapping("/matiere/{matiereId}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<List<GroupeClasseDTO>> getByMatiere(
            @PathVariable Long matiereId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                groupeClasseService.getByMatiereAndAnnee(
                        matiereId, anneeAcademiqueId));
    }

    /**
     * Récupère les groupes d'un étudiant
     * pour une année académique.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des groupes
     */
    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','ENSEIGNANT')")
    public ResponseEntity<List<GroupeClasseDTO>> getByEtudiant(
            @PathVariable Long etudiantId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                groupeClasseService.getByEtudiantAndAnnee(
                        etudiantId, anneeAcademiqueId));
    }

    /**
     * Récupère les groupes de l'enseignant connecté
     * pour une année académique.
     *
     * <p>L'identité provient du token JWT.</p>
     *
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste de ses groupes
     */
    @GetMapping("/mes-groupes")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<List<GroupeClasseDTO>> getMesGroupes(
            @RequestParam Long anneeAcademiqueId) {
        Long enseignantId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                groupeClasseService.getByEnseignantAndAnnee(
                        enseignantId, anneeAcademiqueId));
    }

    /**
     * Récupère les groupes d'un enseignant donné
     * pour une année académique.
     *
     * <p>Réservé à l'administrateur et au responsable.</p>
     *
     * @param enseignantId      identifiant de l'enseignant ciblé
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des groupes
     */
    @GetMapping("/enseignant/{enseignantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE','ENSEIGNANT')")
    public ResponseEntity<List<GroupeClasseDTO>> getByEnseignant(
            @PathVariable Long enseignantId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                groupeClasseService.getByEnseignantAndAnnee(
                        enseignantId, anneeAcademiqueId));
    }
}