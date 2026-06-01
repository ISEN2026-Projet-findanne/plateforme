package org.harold.plateforme.util;

import java.util.Map;

/**
 * Utilitaire pour le calcul des notes et moyennes.
 *
 * <p>Contient toutes les formules de calcul des notes finales
 * et des moyennes selon les règles métier définies :
 * prise en compte des coefficients CC/EF/TP,
 * remplacement de l'EF par le rattrapage,
 * calcul des moyennes pondérées par groupe et semestre.</p>
 *
 * <p>Toutes les méthodes sont statiques car elles ne dépendent
 * d'aucun état — ce sont des fonctions mathématiques pures.</p>
 *
 * @author Harold
 * @version 1.0
 */
public class CalculNoteUtils {

    /**
     * Constructeur privé — classe utilitaire statique uniquement.
     */
    private CalculNoteUtils() {}

    /**
     * Calcule la note finale d'un étudiant dans une matière
     * sans rattrapage.
     *
     * <p>Formule : (CC*coeff_CC + EF*coeff_EF + TP*coeff_TP)
     * / somme(coeffs)</p>
     *
     * <p>Si une note est null (non saisie) elle est ignorée
     * du calcul ainsi que son coefficient.</p>
     *
     * @param noteCC        note de contrôle continu (null si non saisie)
     * @param coeffCC       coefficient du CC
     * @param noteEF        note d'examen final (null si non saisie)
     * @param coeffEF       coefficient de l'EF
     * @param noteTP        note de TP (null si pas de TP)
     * @param coeffTP       coefficient du TP (0 si pas de TP)
     * @return              la note finale arrondie à 2 décimales
     *                      ou null si aucune note n'est disponible
     */
    public static Double calculerNoteMatiere(
            Double noteCC, Double coeffCC,
            Double noteEF, Double coeffEF,
            Double noteTP, Double coeffTP) {

        double sommeNotesPonderees = 0.0;
        double sommeCoeffs = 0.0;

        if (noteCC != null && coeffCC != null) {
            sommeNotesPonderees += noteCC * coeffCC;
            sommeCoeffs += coeffCC;
        }
        if (noteEF != null && coeffEF != null) {
            sommeNotesPonderees += noteEF * coeffEF;
            sommeCoeffs += coeffEF;
        }
        if (noteTP != null && coeffTP != null && coeffTP > 0) {
            sommeNotesPonderees += noteTP * coeffTP;
            sommeCoeffs += coeffTP;
        }

        if (sommeCoeffs == 0.0) return null;
        return CalculKpiUtils.arrondir(sommeNotesPonderees / sommeCoeffs);
    }

    /**
     * Calcule la note finale d'un étudiant dans une matière
     * avec rattrapage.
     *
     * <p>Le rattrapage remplace intégralement la note EF
     * dans le calcul.</p>
     *
     * <p>Formule : (CC*coeff_CC + RAT*coeff_EF + TP*coeff_TP)
     * / somme(coeffs)</p>
     *
     * @param noteCC            note de contrôle continu
     * @param coeffCC           coefficient du CC
     * @param noteRattrapage    note de rattrapage (remplace EF)
     * @param coeffEF           coefficient de l'EF (utilisé pour RAT)
     * @param noteTP            note de TP (null si pas de TP)
     * @param coeffTP           coefficient du TP (0 si pas de TP)
     * @return                  la note finale arrondie à 2 décimales
     *                          ou null si aucune note disponible
     */
    public static Double calculerNoteMatiereAvecRattrapage(
            Double noteCC, Double coeffCC,
            Double noteRattrapage, Double coeffEF,
            Double noteTP, Double coeffTP) {

        // Le rattrapage remplace l'EF → on réutilise calculerNoteMatiere
        return calculerNoteMatiere(
                noteCC, coeffCC,
                noteRattrapage, coeffEF,
                noteTP, coeffTP);
    }

    /**
     * Calcule la moyenne d'un groupe de matières pour un étudiant.
     *
     * <p>Formule : somme(note_matiere * coeff_matiere)
     * / somme(coeff_matieres)</p>
     *
     * <p>La Map contient les notes finales associées
     * à leurs coefficients. Les entrées avec note null
     * sont ignorées du calcul.</p>
     *
     * @param notesEtCoeffs Map de note finale → coefficient
     *                      pour chaque matière du groupe
     * @return              la moyenne du groupe arrondie à 2 décimales
     *                      ou null si aucune note disponible
     */
    public static Double calculerMoyenneGroupe(
            Map<Double, Double> notesEtCoeffs) {

        if (notesEtCoeffs == null || notesEtCoeffs.isEmpty()) return null;

        double sommeNotesPonderees = 0.0;
        double sommeCoeffs = 0.0;

        for (Map.Entry<Double, Double> entry : notesEtCoeffs.entrySet()) {
            Double note = entry.getKey();
            Double coeff = entry.getValue();
            if (note != null && coeff != null) {
                sommeNotesPonderees += note * coeff;
                sommeCoeffs += coeff;
            }
        }

        if (sommeCoeffs == 0.0) return null;
        return CalculKpiUtils.arrondir(sommeNotesPonderees / sommeCoeffs);
    }

    /**
     * Calcule la moyenne semestrielle d'un étudiant.
     *
     * <p>Formule : somme(moyenne_groupe * coeff_groupe)
     * / somme(coeff_groupes)</p>
     *
     * @param moyennesGroupesEtCoeffs   Map de moyenne groupe → coefficient
     *                                  pour chaque groupe de matières
     * @return                          la moyenne semestrielle arrondie
     *                                  ou null si aucune donnée disponible
     */
    public static Double calculerMoyenneSemestre(
            Map<Double, Double> moyennesGroupesEtCoeffs) {

        // Même formule que calculerMoyenneGroupe
        return calculerMoyenneGroupe(moyennesGroupesEtCoeffs);
    }

    /**
     * Calcule la moyenne annuelle d'un étudiant.
     *
     * <p>Formule : (Moyenne_S1 * 50%) + (Moyenne_S2 * 50%)</p>
     *
     * @param moyenneS1     moyenne du semestre 1
     * @param moyenneS2     moyenne du semestre 2
     * @return              la moyenne annuelle arrondie à 2 décimales
     *                      ou null si S1 ou S2 non disponible
     */
    public static Double calculerMoyenneAnnuelle(
            Double moyenneS1,
            Double moyenneS2) {

        if (moyenneS1 == null || moyenneS2 == null) return null;
        return CalculKpiUtils.arrondir(
                (moyenneS1 * 0.5) + (moyenneS2 * 0.5));
    }

    /**
     * Détermine si un groupe de matières est validé.
     *
     * <p>Un groupe est validé si sa moyenne >= 10
     * après compensation entre matières du même groupe.</p>
     *
     * @param moyenneGroupe     la moyenne du groupe de matières
     * @return                  true si validé, false sinon,
     *                          null si moyenne non disponible
     */
    public static Boolean estGroupeValide(Double moyenneGroupe) {
        if (moyenneGroupe == null) return null;
        return moyenneGroupe >= 10.0;
    }

    /**
     * Détermine si un semestre est validé.
     *
     * <p>Un semestre est validé si TOUS les groupes de matières
     * ont une moyenne >= 10. Un seul groupe < 10 invalide
     * le semestre entier.</p>
     *
     * @param moyennesGroupes   liste des moyennes de tous les groupes
     * @return                  true si tous les groupes >= 10,
     *                          false si au moins un groupe < 10,
     *                          null si données non disponibles
     */
    public static Boolean estSemestreValide(
            java.util.List<Double> moyennesGroupes) {

        if (moyennesGroupes == null || moyennesGroupes.isEmpty()) return null;
        if (moyennesGroupes.contains(null)) return null;
        return moyennesGroupes.stream().allMatch(m -> m >= 10.0);
    }

    /**
     * Détermine si une année est validée.
     *
     * <p>Une année est validée si S1 ET S2 sont tous les deux validés.</p>
     *
     * @param s1Valide  statut de validation du semestre 1
     * @param s2Valide  statut de validation du semestre 2
     * @return          true si S1 ET S2 validés,
     *                  false sinon,
     *                  null si l'un des deux n'est pas disponible
     */
    public static Boolean estAnneeValidee(
            Boolean s1Valide,
            Boolean s2Valide) {

        if (s1Valide == null || s2Valide == null) return null;
        return s1Valide && s2Valide;
    }

    /**
     * Calcule la progression inter-semestrielle d'un étudiant.
     *
     * <p>Formule : Moyenne_S2 - Moyenne_S1</p>
     * <p>Positive = progression, négative = régression.</p>
     *
     * @param moyenneS1     moyenne du semestre 1
     * @param moyenneS2     moyenne du semestre 2
     * @return              la progression arrondie à 2 décimales
     *                      ou null si S1 ou S2 non disponible
     */
    public static Double calculerProgression(
            Double moyenneS1,
            Double moyenneS2) {

        if (moyenneS1 == null || moyenneS2 == null) return null;
        return CalculKpiUtils.arrondir(moyenneS2 - moyenneS1);
    }

    /**
     * Détermine la tendance d'un étudiant dans une matière
     * par rapport au semestre précédent.
     *
     * @param noteActuelle      note dans la matière ce semestre
     * @param notePrecedente    note dans la matière le semestre précédent
     * @return                  PROGRESSION, REGRESSION ou STABLE,
     *                          null si l'une des notes n'est pas disponible
     */
    public static String calculerTendance(
            Double noteActuelle,
            Double notePrecedente) {

        if (noteActuelle == null || notePrecedente == null) return null;
        double difference = noteActuelle - notePrecedente;
        if (difference > 0.5) return "PROGRESSION";
        if (difference < -0.5) return "REGRESSION";
        return "STABLE";
    }
}