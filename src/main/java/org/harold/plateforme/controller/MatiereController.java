package org.harold.plateforme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.matiere.GroupeMatieresCreateDTO;
import org.harold.plateforme.dto.matiere.GroupeMatieresDTO;
import org.harold.plateforme.dto.matiere.GroupeMatieresUpdateDTO;
import org.harold.plateforme.dto.matiere.MatiereCreateDTO;
import org.harold.plateforme.dto.matiere.MatiereDTO;
import org.harold.plateforme.dto.matiere.MatiereUpdateDTO;
import org.harold.plateforme.dto.matiere.TypeEvaluationCreateDTO;
import org.harold.plateforme.dto.matiere.TypeEvaluationDTO;
import org.harold.plateforme.dto.matiere.TypeEvaluationUpdateDTO;
import org.harold.plateforme.security.SecurityUtils;
import org.harold.plateforme.service.MatiereService;
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
 * Controller de gestion des matières.
 *
 * <p>Gère les groupes de matières, les matières
 * et les types d'évaluation. L'identité de l'utilisateur
 * est extraite du token JWT via SecurityUtils.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/matieres")
@RequiredArgsConstructor
public class MatiereController {

    private final MatiereService matiereService;

    // ===== GROUPES DE MATIERES =====

    /**
     * Crée un nouveau groupe de matières.
     *
     * @param dto       les données de création
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO du groupe créé
     */
    @PostMapping("/groupes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupeMatieresDTO> creerGroupe(
            @Valid @RequestBody GroupeMatieresCreateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matiereService.creerGroupeMatieres(
                        dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Modifie un groupe de matières existant.
     *
     * @param id        identifiant du groupe
     * @param dto       les nouvelles données
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO du groupe modifié
     */
    @PutMapping("/groupes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupeMatieresDTO> modifierGroupe(
            @PathVariable Long id,
            @Valid @RequestBody GroupeMatieresUpdateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                matiereService.modifierGroupeMatieres(
                        id, dto, adminId,
                        request.getRemoteAddr()));
    }

    /**
     * Récupère les groupes de matières d'une classe.
     *
     * @param classeId  identifiant de la classe
     * @return          HTTP 200 avec la liste des groupes
     */
    @GetMapping("/groupes/classe/{classeId}")
    @PreAuthorize("hasAnyRole('ADMIN','Responsable')")
    public ResponseEntity<List<GroupeMatieresDTO>> getGroupesByClasse(
            @PathVariable Long classeId) {
        return ResponseEntity.ok(
                matiereService.getGroupesByClasse(classeId));
    }

    // ===== MATIERES =====

    /**
     * Crée une nouvelle matière.
     *
     * @param dto       les données de création
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO de la matière créée
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatiereDTO> creer(
            @Valid @RequestBody MatiereCreateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matiereService.creerMatiere(
                        dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Modifie une matière existante.
     *
     * @param id        identifiant de la matière
     * @param dto       les nouvelles données
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO de la matière modifiée
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatiereDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody MatiereUpdateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(matiereService.modifierMatiere(
                id, dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Récupère une matière par son identifiant.
     *
     * @param id    identifiant de la matière
     * @return      HTTP 200 avec le DTO de la matière
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','ENSEIGNANT')")
    public ResponseEntity<MatiereDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(matiereService.getById(id));
    }

    /**
     * Récupère toutes les matières d'une classe.
     *
     * @param classeId  identifiant de la classe
     * @return          HTTP 200 avec la liste des matières
     */
    @GetMapping("/classe/{classeId}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<List<MatiereDTO>> getByClasse(
            @PathVariable Long classeId) {
        return ResponseEntity.ok(
                matiereService.getByClasse(classeId));
    }

    /**
     * Récupère les matières d'une classe pour un semestre.
     *
     * @param classeId          identifiant de la classe
     * @param numeroSemestre    numéro du semestre (1 ou 2)
     * @return                  HTTP 200 avec la liste des matières
     */
    @GetMapping("/classe/{classeId}/semestre/{numeroSemestre}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<List<MatiereDTO>> getByClasseAndSemestre(
            @PathVariable Long classeId,
            @PathVariable Integer numeroSemestre) {
        return ResponseEntity.ok(
                matiereService.getByClasseAndSemestre(
                        classeId, numeroSemestre));
    }

    /**
     * Récupère les matières de l'enseignant connecté.
     *
     * <p>L'identité provient du token JWT : l'enseignant
     * accède uniquement à ses propres matières.</p>
     *
     * @return  HTTP 200 avec la liste de ses matières
     */
    @GetMapping("/mes-matieres")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<List<MatiereDTO>> getMesMatieres() {
        Long enseignantId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                matiereService.getByEnseignant(enseignantId));
    }

    /**
     * Récupère les matières d'un enseignant donné.
     *
     * <p>Réservé à l'administrateur et au responsable
     * pour consulter les matières d'un enseignant précis.</p>
     *
     * @param enseignantId  identifiant de l'enseignant ciblé
     * @return              HTTP 200 avec la liste des matières
     */
    @GetMapping("/enseignant/{enseignantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE','ENSEIGNANT')")
    public ResponseEntity<List<MatiereDTO>> getByEnseignant(
            @PathVariable Long enseignantId) {
        return ResponseEntity.ok(
                matiereService.getByEnseignant(enseignantId));
    }

    // ===== TYPES D'EVALUATION =====

    /**
     * Crée un type d'évaluation pour une matière.
     *
     * @param dto       les données de création
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 201 avec le DTO du type créé
     */
    @PostMapping("/types-evaluation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TypeEvaluationDTO> creerTypeEvaluation(
            @Valid @RequestBody TypeEvaluationCreateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matiereService.creerTypeEvaluation(
                        dto, adminId, request.getRemoteAddr()));
    }

    /**
     * Modifie un type d'évaluation existant.
     *
     * @param id        identifiant du type
     * @param dto       les nouvelles données
     * @param request   la requête HTTP pour récupérer l'IP
     * @return          HTTP 200 avec le DTO du type modifié
     */
    @PutMapping("/types-evaluation/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TypeEvaluationDTO> modifierTypeEvaluation(
            @PathVariable Long id,
            @Valid @RequestBody TypeEvaluationUpdateDTO dto,
            HttpServletRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                matiereService.modifierTypeEvaluation(
                        id, dto, adminId,
                        request.getRemoteAddr()));
    }

    /**
     * Récupère les types d'évaluation d'une matière.
     *
     * @param matiereId     identifiant de la matière
     * @return              HTTP 200 avec la liste des types
     */
    @GetMapping("/{matiereId}/types-evaluation")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<List<TypeEvaluationDTO>> getTypesByMatiere(
            @PathVariable Long matiereId) {
        return ResponseEntity.ok(
                matiereService.getTypesByMatiere(matiereId));
    }

}