package org.harold.plateforme.util;

import java.util.Collections;
import java.util.List;

/**
 * Utilitaire pour le calcul des statistiques KPI.
 *
 * <p>Toutes les méthodes travaillent sur des listes de notes
 * finales calculées. La distinction entre notes avant et après
 * rattrapage est gérée par le KpiService qui fournit
 * les bonnes listes.</p>
 *
 * @author Harold
 * @version 1.0
 */
public class CalculKpiUtils {

    /**
     * Constructeur privé — classe utilitaire statique uniquement.
     */
    private CalculKpiUtils() {}

    /**
     * Calcule la moyenne d'une liste de notes finales.
     *
     * @param notes liste de notes finales après rattrapage
     * @return      la moyenne arrondie à 2 décimales ou null si liste vide
     */
    public static Double calculerMoyenne(List<Double> notes) {
        if (notes == null || notes.isEmpty()) return null;
        double moyenne = notes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        return arrondir(moyenne);
    }

    /**
     * Calcule la note minimale d'une liste de notes finales.
     *
     * @param notes liste de notes finales après rattrapage
     * @return      le minimum ou null si liste vide
     */
    public static Double calculerMinimum(List<Double> notes) {
        if (notes == null || notes.isEmpty()) return null;
        return arrondir(Collections.min(notes));
    }

    /**
     * Calcule la note maximale d'une liste de notes finales.
     *
     * @param notes liste de notes finales après rattrapage
     * @return      le maximum ou null si liste vide
     */
    public static Double calculerMaximum(List<Double> notes) {
        if (notes == null || notes.isEmpty()) return null;
        return arrondir(Collections.max(notes));
    }

    /**
     * Calcule l'écart-type d'une liste de notes finales.
     *
     * @param notes liste de notes finales après rattrapage
     * @return      l'écart-type arrondi à 2 décimales ou null si liste vide
     */
    public static Double calculerEcartType(List<Double> notes) {
        if (notes == null || notes.isEmpty()) return null;
        double moyenne = calculerMoyenne(notes);
        double variance = notes.stream()
                .mapToDouble(n -> Math.pow(n - moyenne, 2))
                .average()
                .orElse(0.0);
        return arrondir(Math.sqrt(variance));
    }

    /**
     * Calcule la médiane d'une liste de notes finales.
     *
     * <p>Si le nombre de notes est pair on fait la moyenne
     * des deux valeurs centrales.</p>
     *
     * @param notes liste de notes finales après rattrapage
     * @return      la médiane arrondie à 2 décimales ou null si liste vide
     */
    public static Double calculerMediane(List<Double> notes) {
        if (notes == null || notes.isEmpty()) return null;
        List<Double> sorted = notes.stream().sorted().toList();
        int taille = sorted.size();
        if (taille % 2 == 0) {
            return arrondir((sorted.get(taille / 2 - 1) +
                    sorted.get(taille / 2)) / 2.0);
        }
        return arrondir(sorted.get(taille / 2));
    }

    /**
     * Calcule le taux de réussite après rattrapage.
     *
     * <p>Pourcentage d'étudiants ayant une note finale >= 10
     * après prise en compte du rattrapage.</p>
     *
     * @param notesApresRattrapage  liste de notes finales après rattrapage
     * @return                      le taux de réussite en % ou null si liste vide
     */
    public static Double calculerTauxReussite(List<Double> notesApresRattrapage) {
        if (notesApresRattrapage == null || notesApresRattrapage.isEmpty()) return null;
        long nbReussis = notesApresRattrapage.stream()
                .filter(n -> n >= 10.0)
                .count();
        return arrondir((nbReussis * 100.0) / notesApresRattrapage.size());
    }

    /**
     * Calcule le taux d'échec après rattrapage.
     *
     * <p>Pourcentage d'étudiants ayant une note finale < 10
     * après prise en compte du rattrapage.
     * Ce sont les étudiants en échec définitif.</p>
     *
     * @param notesApresRattrapage  liste de notes finales après rattrapage
     * @return                      le taux d'échec en % ou null si liste vide
     */
    public static Double calculerTauxEchec(List<Double> notesApresRattrapage) {
        if (notesApresRattrapage == null || notesApresRattrapage.isEmpty()) return null;
        return arrondir(100.0 - calculerTauxReussite(notesApresRattrapage));
    }

    /**
     * Calcule le taux de rattrapage.
     *
     * <p>Pourcentage d'étudiants convoqués en rattrapage,
     * c'est-à-dire ayant une note finale < 10 AVANT rattrapage.
     * S'applique uniquement au niveau matière.</p>
     *
     * @param notesAvantRattrapage  liste de notes finales avant rattrapage
     * @return                      le taux de rattrapage en % ou null si liste vide
     */
    public static Double calculerTauxRattrapage(List<Double> notesAvantRattrapage) {
        if (notesAvantRattrapage == null || notesAvantRattrapage.isEmpty()) return null;
        long nbRattrapages = notesAvantRattrapage.stream()
                .filter(n -> n < 10.0)
                .count();
        return arrondir((nbRattrapages * 100.0) / notesAvantRattrapage.size());
    }

    /**
     * Arrondit une valeur à deux décimales.
     *
     * @param valeur    la valeur à arrondir
     * @return          la valeur arrondie ou null
     */
    public static Double arrondir(Double valeur) {
        if (valeur == null) return null;
        return Math.round(valeur * 100.0) / 100.0;
    }
}