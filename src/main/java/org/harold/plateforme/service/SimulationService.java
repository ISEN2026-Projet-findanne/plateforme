package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.kpi.SimulationMatiereResultDTO;
import org.harold.plateforme.dto.kpi.SimulationRequestDTO;
import org.harold.plateforme.dto.kpi.SimulationSemestreResultDTO;
import org.harold.plateforme.entity.AnneeAcademique;
import org.harold.plateforme.entity.Etudiant;
import org.harold.plateforme.entity.GroupeMatieres;
import org.harold.plateforme.entity.Matiere;
import org.harold.plateforme.entity.Note;
import org.harold.plateforme.entity.TypeEvaluation;
import org.harold.plateforme.repository.GroupeMatieresRepository;
import org.harold.plateforme.repository.NoteRepository;
import org.harold.plateforme.repository.SemestreRepository;
import org.harold.plateforme.repository.TypeEvaluationRepository;
import org.harold.plateforme.util.CalculNoteUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de simulation pédagogique.
 *
 * <p>Simule l'impact d'une note de rattrapage sur la validation
 * d'un étudiant au niveau matière et semestre.
 * La simulation est une prévisualisation uniquement —
 * aucune donnée n'est sauvegardée en base.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SimulationService {

    private final NoteRepository noteRepository;
    private final TypeEvaluationRepository typeEvaluationRepository;
    private final GroupeMatieresRepository groupeMatieresRepository;
    private final SemestreRepository semestreRepository;
    private final NoteService noteService;
    private final MatiereService matiereService;
    private final EtudiantService etudiantService;
    private final AnneeAcademiqueService anneeAcademiqueService;

    // ===== SIMULATION MATIERE =====

    /**
     * Simule l'impact d'une note de rattrapage au niveau matière.
     *
     * <p>Remplace la note EF par la note simulée et recalcule
     * la note matière. Retourne la comparaison avant/après.
     * Aucune donnée n'est sauvegardée.</p>
     *
     * @param dto   les paramètres de simulation
     * @return      le résultat de simulation au niveau matière
     */
    @Transactional(readOnly = true)
    public SimulationMatiereResultDTO simulerMatiere(
            SimulationRequestDTO dto) {

        // 1. Récupérer les entités
        Etudiant etudiant = etudiantService
                .getEntityById(dto.getEtudiantId());
        Matiere matiere = matiereService
                .getEntityById(dto.getMatiereId());

        // 2. Récupérer les notes actuelles
        List<Note> notes = noteRepository
                .findByEtudiantIdAndMatiereIdAndAnneeAcademiqueId(
                        dto.getEtudiantId(),
                        dto.getMatiereId(),
                        dto.getAnneeAcademiqueId());

        Double noteCC = getNoteValeur(notes, Note.TypeNote.CC);
        Double noteEF = getNoteValeur(
                notes, Note.TypeNote.EXAMEN_FINAL);
        Double noteTP = getNoteValeur(notes, Note.TypeNote.TP);

        // 3. Récupérer les coefficients
        List<TypeEvaluation> types = typeEvaluationRepository
                .findByMatiereId(dto.getMatiereId());

        Double coeffCC = getCoeffParType(
                types, TypeEvaluation.TypeEval.CC);
        Double coeffEF = getCoeffParType(
                types, TypeEvaluation.TypeEval.EXAMEN_FINAL);
        Double coeffTP = getCoeffParType(
                types, TypeEvaluation.TypeEval.TP);

        // 4. Calculer note actuelle
        Double noteMatiereActuelle = noteService.calculerNoteFinale(
                dto.getEtudiantId(),
                dto.getMatiereId(),
                dto.getAnneeAcademiqueId());

        // 5. Calculer note simulée
        // La note simulée remplace l'EF intégralement
        Double noteMatiereSimulee = CalculNoteUtils
                .calculerNoteMatiereAvecRattrapage(
                        noteCC, coeffCC,
                        dto.getNoteSimulee(), coeffEF,
                        noteTP, coeffTP);

        // 6. Construire le résultat
        SimulationMatiereResultDTO resultat =
                new SimulationMatiereResultDTO();
        resultat.setEtudiantNom(etudiant.getNom());
        resultat.setEtudiantPrenom(etudiant.getPrenom());
        resultat.setMatiereNom(matiere.getNom());
        resultat.setNoteEFActuelle(noteEF);
        resultat.setNoteSimulee(dto.getNoteSimulee());
        resultat.setNoteMatiereActuelle(noteMatiereActuelle);
        resultat.setNoteMatiereSimulee(noteMatiereSimulee);
        resultat.setMatiereValideActuellement(
                noteMatiereActuelle != null &&
                        noteMatiereActuelle >= 10.0);
        resultat.setMatiereValideAvecSimulation(
                noteMatiereSimulee != null &&
                        noteMatiereSimulee >= 10.0);

        return resultat;
    }

    // ===== SIMULATION SEMESTRE =====

    /**
     * Simule l'impact d'une note de rattrapage au niveau semestre.
     *
     * <p>Remplace la note EF par la note simulée et recalcule
     * la note matière, la moyenne du groupe de matières
     * et la moyenne du semestre. Aucune donnée n'est sauvegardée.</p>
     *
     * @param dto   les paramètres de simulation
     * @return      le résultat de simulation au niveau semestre
     */
    @Transactional(readOnly = true)
    public SimulationSemestreResultDTO simulerSemestre(
            SimulationRequestDTO dto) {

        // 1. Récupérer les entités
        Etudiant etudiant = etudiantService
                .getEntityById(dto.getEtudiantId());
        Matiere matiere = matiereService
                .getEntityById(dto.getMatiereId());
        AnneeAcademique annee = anneeAcademiqueService
                .getEntityById(dto.getAnneeAcademiqueId());

        // 2. Récupérer le groupe de matières de la matière
        GroupeMatieres groupe = matiere.getGroupeMatieres();

        // 3. Récupérer le semestre du groupe
        int numeroSemestre = groupe.getSemestre().getNumero();

        // 4. Simuler au niveau matière d'abord
        SimulationMatiereResultDTO simMatiere =
                simulerMatiere(dto);

        // 5. Calculer moyenne groupe actuelle
        List<Matiere> matieresDuGroupe = matiereService
                .getMatieresByGroupeId(groupe.getId());

        Map<Double, Double> notesGroupeActuelles =
                new HashMap<>();
        Map<Double, Double> notesGroupeSimulees =
                new HashMap<>();

        for (Matiere m : matieresDuGroupe) {
            Double noteFinale = noteService.calculerNoteFinale(
                    dto.getEtudiantId(),
                    m.getId(),
                    dto.getAnneeAcademiqueId());

            if (noteFinale != null) {
                notesGroupeActuelles.put(
                        noteFinale, m.getCoefficient());

                // Pour la matière simulée on utilise
                // la note simulée
                if (m.getId().equals(dto.getMatiereId())) {
                    notesGroupeSimulees.put(
                            simMatiere.getNoteMatiereSimulee(),
                            m.getCoefficient());
                } else {
                    notesGroupeSimulees.put(
                            noteFinale, m.getCoefficient());
                }
            }
        }

        Double moyenneGroupeActuelle = CalculNoteUtils
                .calculerMoyenneGroupe(notesGroupeActuelles);
        Double moyenneGroupeSimulee = CalculNoteUtils
                .calculerMoyenneGroupe(notesGroupeSimulees);

        // 6. Calculer moyenne semestre actuelle et simulée
        List<GroupeMatieres> tousLesGroupes =
                groupeMatieresRepository.findBySemestreId(
                        groupe.getSemestre().getId());

        Map<Double, Double> moyennesGroupesActuelles =
                new HashMap<>();
        Map<Double, Double> moyennesGroupesSimulees =
                new HashMap<>();
        List<Double> listeMoyennesActuelles = new ArrayList<>();
        List<Double> listeMoyennesSimulees = new ArrayList<>();

        for (GroupeMatieres g : tousLesGroupes) {
            Double moyGroupe;

            if (g.getId().equals(groupe.getId())) {
                // Ce groupe contient la matière simulée
                moyGroupe = moyenneGroupeActuelle;
                if (moyGroupe != null) {
                    moyennesGroupesActuelles.put(
                            moyGroupe, g.getCoefficient());
                    listeMoyennesActuelles.add(moyGroupe);
                }
                if (moyenneGroupeSimulee != null) {
                    moyennesGroupesSimulees.put(
                            moyenneGroupeSimulee,
                            g.getCoefficient());
                    listeMoyennesSimulees.add(moyenneGroupeSimulee);
                }
            } else {
                // Autres groupes — pas de simulation
                List<Matiere> autresMatieres = matiereService
                        .getMatieresByGroupeId(g.getId());
                Map<Double, Double> notesAutreGroupe =
                        new HashMap<>();

                for (Matiere m : autresMatieres) {
                    Double note = noteService.calculerNoteFinale(
                            dto.getEtudiantId(),
                            m.getId(),
                            dto.getAnneeAcademiqueId());
                    if (note != null) {
                        notesAutreGroupe.put(
                                note, m.getCoefficient());
                    }
                }

                moyGroupe = CalculNoteUtils
                        .calculerMoyenneGroupe(notesAutreGroupe);
                if (moyGroupe != null) {
                    moyennesGroupesActuelles.put(
                            moyGroupe, g.getCoefficient());
                    moyennesGroupesSimulees.put(
                            moyGroupe, g.getCoefficient());
                    listeMoyennesActuelles.add(moyGroupe);
                    listeMoyennesSimulees.add(moyGroupe);
                }
            }
        }

        Double moyenneSemestreActuelle = CalculNoteUtils
                .calculerMoyenneSemestre(moyennesGroupesActuelles);
        Double moyenneSemestreSimulee = CalculNoteUtils
                .calculerMoyenneSemestre(moyennesGroupesSimulees);

        // 7. Construire le résultat
        SimulationSemestreResultDTO resultat =
                new SimulationSemestreResultDTO();
        resultat.setEtudiantNom(etudiant.getNom());
        resultat.setEtudiantPrenom(etudiant.getPrenom());
        resultat.setNumeroEtudiant(etudiant.getNumeroEtudiant());
        resultat.setAnneeAcademique(annee.getAnnee());
        resultat.setNumeroSemestre(numeroSemestre);
        resultat.setNoteSimulee(dto.getNoteSimulee());

        // Niveau matière
        resultat.setMatiereNom(matiere.getNom());
        resultat.setNoteMatiereActuelle(
                simMatiere.getNoteMatiereActuelle());
        resultat.setNoteMatiereSimulee(
                simMatiere.getNoteMatiereSimulee());
        resultat.setMatiereValideActuellement(
                simMatiere.getMatiereValideActuellement());
        resultat.setMatiereValideAvecSimulation(
                simMatiere.getMatiereValideAvecSimulation());

        // Niveau groupe
        resultat.setGroupeMatieresNom(groupe.getNom());
        resultat.setMoyenneGroupeActuelle(moyenneGroupeActuelle);
        resultat.setMoyenneGroupeSimulee(moyenneGroupeSimulee);
        resultat.setGroupeValideActuellement(
                CalculNoteUtils.estGroupeValide(
                        moyenneGroupeActuelle));
        resultat.setGroupeValideAvecSimulation(
                CalculNoteUtils.estGroupeValide(
                        moyenneGroupeSimulee));

        // Niveau semestre
        resultat.setMoyenneSemestreActuelle(moyenneSemestreActuelle);
        resultat.setMoyenneSemestreSimulee(moyenneSemestreSimulee);
        resultat.setSemestreValideActuellement(
                CalculNoteUtils.estSemestreValide(
                        listeMoyennesActuelles));
        resultat.setSemestreValideAvecSimulation(
                CalculNoteUtils.estSemestreValide(
                        listeMoyennesSimulees));

        return resultat;
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Extrait la valeur d'une note par type depuis une liste.
     *
     * @param notes     liste des notes
     * @param type      le type recherché
     * @return          la valeur ou null
     */
    private Double getNoteValeur(
            List<Note> notes,
            Note.TypeNote type) {
        return notes.stream()
                .filter(n -> n.getType() == type)
                .map(Note::getValeur)
                .findFirst()
                .orElse(null);
    }

    /**
     * Extrait le coefficient d'un type d'évaluation.
     *
     * @param types     liste des types d'évaluation
     * @param type      le type recherché
     * @return          le coefficient ou null
     */
    private Double getCoeffParType(
            List<TypeEvaluation> types,
            TypeEvaluation.TypeEval type) {
        return types.stream()
                .filter(t -> t.getType() == type)
                .map(TypeEvaluation::getCoefficient)
                .findFirst()
                .orElse(null);
    }
}