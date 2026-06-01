package org.harold.plateforme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.groupe.AssignationEnseignantDTO;
import org.harold.plateforme.dto.groupe.AssignationEtudiantDTO;
import org.harold.plateforme.dto.groupe.GroupeClasseCreateDTO;
import org.harold.plateforme.dto.groupe.GroupeClasseDTO;
import org.harold.plateforme.dto.groupe.GroupeClasseUpdateDTO;
import org.harold.plateforme.service.GroupeClasseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * l'assignation des étudiants et des enseignants.</p>
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
     * @param adminId   identifiant de l'admin connecté
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO du groupe créé
     */
    @PostMapping
    public ResponseEntity<GroupeClasseDTO> creer(
            @Valid @RequestBody GroupeClasseCreateDTO dto,
            @RequestParam Long adminId,
            HttpServletRequest request) {
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
     * @param adminId   identifiant de l'admin connecté
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO du groupe modifié
     */
    @PutMapping("/{id}")
    public ResponseEntity<GroupeClasseDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody GroupeClasseUpdateDTO dto,
            @RequestParam Long adminId,
            HttpServletRequest request) {
        return ResponseEntity.ok(groupeClasseService.modifier(
                id, dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Assigne un étudiant à un groupe.
     *
     * @param dto       les données d'assignation
     * @param adminId   identifiant de l'admin connecté
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 si assignation réussie
     */
    @PostMapping("/assigner-etudiant")
    public ResponseEntity<Void> assignerEtudiant(
            @Valid @RequestBody AssignationEtudiantDTO dto,
            @RequestParam Long adminId,
            HttpServletRequest request) {
        groupeClasseService.assignerEtudiant(
                dto, adminId, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retire un étudiant d'un groupe.
     *
     * @param groupeId      identifiant du groupe
     * @param etudiantId    identifiant de l'étudiant
     * @param adminId       identifiant de l'admin connecté
     * @param request       la requête HTTP pour récupérer l'IP
     * @return              HTTP 204 si retrait réussi
     */
    @DeleteMapping("/{groupeId}/etudiants/{etudiantId}")
    public ResponseEntity<Void> retirerEtudiant(
            @PathVariable Long groupeId,
            @PathVariable Long etudiantId,
            @RequestParam Long adminId,
            HttpServletRequest request) {
        groupeClasseService.retirerEtudiant(
                etudiantId, groupeId,
                adminId, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    /**
     * Assigne un enseignant à un groupe pour une matière.
     *
     * @param dto       les données d'assignation
     * @param adminId   identifiant de l'admin connecté
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 si assignation réussie
     */
    @PostMapping("/assigner-enseignant")
    public ResponseEntity<Void> assignerEnseignant(
            @Valid @RequestBody AssignationEnseignantDTO dto,
            @RequestParam Long adminId,
            HttpServletRequest request) {
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
    public ResponseEntity<List<GroupeClasseDTO>> getByEtudiant(
            @PathVariable Long etudiantId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                groupeClasseService.getByEtudiantAndAnnee(
                        etudiantId, anneeAcademiqueId));
    }

    /**
     * Récupère les groupes d'un enseignant
     * pour une année académique.
     *
     * @param enseignantId      identifiant de l'enseignant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des groupes
     */
    @GetMapping("/enseignant/{enseignantId}")
    public ResponseEntity<List<GroupeClasseDTO>> getByEnseignant(
            @PathVariable Long enseignantId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                groupeClasseService.getByEnseignantAndAnnee(
                        enseignantId, anneeAcademiqueId));
    }
}