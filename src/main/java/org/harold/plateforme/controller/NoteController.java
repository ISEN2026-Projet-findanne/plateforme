package org.harold.plateforme.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.note.NoteCreateDTO;
import org.harold.plateforme.dto.note.NoteDTO;
import org.harold.plateforme.dto.note.NoteUpdateDTO;
import org.harold.plateforme.service.NoteService;
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
 * Controller de gestion des notes.
 *
 * <p>Gère la saisie, modification et consultation des notes.
 * La saisie et la modification sont réservées
 * à l'enseignant assigné à la matière.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    /**
     * Saisit une note pour un étudiant.
     *
     * @param dto           les données de la note
     * @param enseignantId  identifiant de l'enseignant connecté
     * @param request       la requête HTTP pour récupérer l'IP
     * @return              HTTP 201 avec le DTO de la note créée
     */
    @PostMapping
    public ResponseEntity<NoteDTO> saisir(
            @Valid @RequestBody NoteCreateDTO dto,
            @RequestParam Long enseignantId,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(noteService.saisir(
                        dto, enseignantId,
                        request.getRemoteAddr()));
    }

    /**
     * Modifie une note existante.
     *
     * @param id            identifiant de la note
     * @param dto           les nouvelles données
     * @param enseignantId  identifiant de l'enseignant connecté
     * @param request       la requête HTTP pour récupérer l'IP
     * @return              HTTP 200 avec le DTO de la note modifiée
     */
    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody NoteUpdateDTO dto,
            @RequestParam Long enseignantId,
            HttpServletRequest request) {
        return ResponseEntity.ok(noteService.modifier(
                id, dto, enseignantId,
                request.getRemoteAddr()));
    }

    /**
     * Récupère toutes les notes d'un étudiant
     * pour une année académique.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des notes
     */
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<NoteDTO>> getByEtudiant(
            @PathVariable Long etudiantId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                noteService.getByEtudiantAndAnnee(
                        etudiantId, anneeAcademiqueId));
    }

    /**
     * Récupère toutes les notes d'une matière
     * pour une année académique.
     *
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des notes
     */
    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<List<NoteDTO>> getByMatiere(
            @PathVariable Long matiereId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                noteService.getByMatiereAndAnnee(
                        matiereId, anneeAcademiqueId));
    }

    /**
     * Récupère les notes d'un groupe pour une matière
     * et une année académique.
     *
     * @param groupeId          identifiant du groupe
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des notes
     */
    @GetMapping("/groupe/{groupeId}/matiere/{matiereId}")
    public ResponseEntity<List<NoteDTO>> getByGroupeAndMatiere(
            @PathVariable Long groupeId,
            @PathVariable Long matiereId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                noteService.getByGroupeAndMatiereAndAnnee(
                        groupeId, matiereId, anneeAcademiqueId));
    }
}