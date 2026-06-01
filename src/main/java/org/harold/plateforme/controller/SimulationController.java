package org.harold.plateforme.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.kpi.SimulationMatiereResultDTO;
import org.harold.plateforme.dto.kpi.SimulationRequestDTO;
import org.harold.plateforme.dto.kpi.SimulationSemestreResultDTO;
import org.harold.plateforme.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de simulation pédagogique.
 *
 * <p>Simule l'impact d'une note de rattrapage
 * au niveau matière et semestre.
 * Aucune donnée n'est sauvegardée en base —
 * c'est une prévisualisation uniquement.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    /**
     * Simule l'impact d'une note de rattrapage
     * au niveau matière.
     *
     * <p>Retourne la comparaison avant/après simulation
     * pour la note matière uniquement.
     * Destiné à l'enseignant.</p>
     *
     * @param dto   les paramètres de simulation
     * @return      HTTP 200 avec le résultat au niveau matière
     */
    @PostMapping("/matiere")
    public ResponseEntity<SimulationMatiereResultDTO> simulerMatiere(
            @Valid @RequestBody SimulationRequestDTO dto) {
        return ResponseEntity.ok(
                simulationService.simulerMatiere(dto));
    }

    /**
     * Simule l'impact d'une note de rattrapage
     * au niveau semestre.
     *
     * <p>Retourne la comparaison avant/après simulation
     * pour la matière, le groupe de matières et le semestre.
     * Destiné au responsable pédagogique.</p>
     *
     * @param dto   les paramètres de simulation
     * @return      HTTP 200 avec le résultat au niveau semestre
     */
    @PostMapping("/semestre")
    public ResponseEntity<SimulationSemestreResultDTO> simulerSemestre(
            @Valid @RequestBody SimulationRequestDTO dto) {
        return ResponseEntity.ok(
                simulationService.simulerSemestre(dto));
    }
}