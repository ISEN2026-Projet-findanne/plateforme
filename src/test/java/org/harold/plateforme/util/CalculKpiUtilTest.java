package org.harold.plateforme.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de CalculKpiUtils.
 *
 * <p>Vérifie les calculs statistiques (moyenne, min, max,
 * médiane, écart-type) et les taux (réussite, échec,
 * rattrapage), y compris les cas limites.</p>
 *
 * @author Harold
 * @version 1.0
 */
@DisplayName("CalculKpiUtils — statistiques et taux")
class CalculKpiUtilsTest {

    // ===== MOYENNE =====

    @Nested
    @DisplayName("calculerMoyenne")
    class CalculerMoyenne {

        @Test
        @DisplayName("Moyenne de plusieurs notes")
        void plusieursNotes() {
            // (10 + 12 + 14) / 3 = 12.0
            Double resultat = CalculKpiUtils.calculerMoyenne(
                    Arrays.asList(10.0, 12.0, 14.0));
            assertEquals(12.0, resultat, 0.01);
        }

        @Test
        @DisplayName("Moyenne d'une seule note = la note elle-même")
        void uneSeuleNote() {
            assertEquals(15.0,
                    CalculKpiUtils.calculerMoyenne(
                            Arrays.asList(15.0)), 0.01);
        }

        @Test
        @DisplayName("Liste vide : retourne null")
        void listeVide() {
            assertNull(CalculKpiUtils.calculerMoyenne(
                    Arrays.asList()));
        }

        @Test
        @DisplayName("Liste null : retourne null")
        void listeNull() {
            assertNull(CalculKpiUtils.calculerMoyenne(null));
        }
    }

    // ===== MINIMUM / MAXIMUM =====

    @Nested
    @DisplayName("calculerMinimum / calculerMaximum")
    class MinMax {

        @Test
        @DisplayName("Minimum d'une liste")
        void minimum() {
            assertEquals(5.33,
                    CalculKpiUtils.calculerMinimum(
                            Arrays.asList(13.67, 5.33, 9.5)), 0.01);
        }

        @Test
        @DisplayName("Maximum d'une liste")
        void maximum() {
            assertEquals(13.67,
                    CalculKpiUtils.calculerMaximum(
                            Arrays.asList(13.67, 5.33, 9.5)), 0.01);
        }

        @Test
        @DisplayName("Min sur liste vide : null")
        void minListeVide() {
            assertNull(CalculKpiUtils.calculerMinimum(
                    Arrays.asList()));
        }

        @Test
        @DisplayName("Max sur liste null : null")
        void maxListeNull() {
            assertNull(CalculKpiUtils.calculerMaximum(null));
        }
    }

    // ===== MEDIANE =====

    @Nested
    @DisplayName("calculerMediane")
    class CalculerMediane {

        @Test
        @DisplayName("Médiane d'un nombre impair de notes")
        void nombreImpair() {
            // trié : [8, 12, 16] → médiane = 12
            assertEquals(12.0,
                    CalculKpiUtils.calculerMediane(
                            Arrays.asList(16.0, 8.0, 12.0)), 0.01);
        }

        @Test
        @DisplayName("Médiane d'un nombre pair = moyenne des 2 centrales")
        void nombrePair() {
            // trié : [8, 10, 12, 16] → (10+12)/2 = 11.0
            assertEquals(11.0,
                    CalculKpiUtils.calculerMediane(
                            Arrays.asList(16.0, 8.0, 12.0, 10.0)), 0.01);
        }

        @Test
        @DisplayName("Médiane d'une seule note")
        void uneSeuleNote() {
            assertEquals(14.0,
                    CalculKpiUtils.calculerMediane(
                            Arrays.asList(14.0)), 0.01);
        }

        @Test
        @DisplayName("Liste vide : retourne null")
        void listeVide() {
            assertNull(CalculKpiUtils.calculerMediane(
                    Arrays.asList()));
        }
    }
    // ===== ECART-TYPE =====

    @Nested
    @DisplayName("calculerEcartType")
    class CalculerEcartType {

        @Test
        @DisplayName("Écart-type d'une série connue")
        void serieConnue() {
            // notes [10, 12, 14] : moyenne 12
            // variance = ((-2)² + 0² + 2²)/3 = (4+0+4)/3 = 2.667
            // écart-type = √2.667 ≈ 1.63
            Double resultat = CalculKpiUtils.calculerEcartType(
                    Arrays.asList(10.0, 12.0, 14.0));
            assertEquals(1.63, resultat, 0.01);
        }

        @Test
        @DisplayName("Écart-type de notes identiques = 0")
        void notesIdentiques() {
            // toutes égales → aucune dispersion
            assertEquals(0.0,
                    CalculKpiUtils.calculerEcartType(
                            Arrays.asList(12.0, 12.0, 12.0)), 0.01);
        }

        @Test
        @DisplayName("Liste vide : retourne null")
        void listeVide() {
            assertNull(CalculKpiUtils.calculerEcartType(
                    Arrays.asList()));
        }
    }

    // ===== TAUX DE REUSSITE =====

    @Nested
    @DisplayName("calculerTauxReussite")
    class CalculerTauxReussite {

        @Test
        @DisplayName("La moitié des notes >= 10 → 50%")
        void moitieReussite() {
            // 5.33 < 10, 13.67 >= 10 → 1 sur 2 = 50%
            assertEquals(50.0,
                    CalculKpiUtils.calculerTauxReussite(
                            Arrays.asList(5.33, 13.67)), 0.01);
        }

        @Test
        @DisplayName("La note exactement à 10 compte comme réussite")
        void noteA10EstReussite() {
            // 10.0 est >= 10 → 100%
            assertEquals(100.0,
                    CalculKpiUtils.calculerTauxReussite(
                            Arrays.asList(10.0, 15.0)), 0.01);
        }

        @Test
        @DisplayName("Toutes en échec → 0%")
        void toutesEnEchec() {
            assertEquals(0.0,
                    CalculKpiUtils.calculerTauxReussite(
                            Arrays.asList(5.0, 8.0)), 0.01);
        }

        @Test
        @DisplayName("Liste vide : retourne null")
        void listeVide() {
            assertNull(CalculKpiUtils.calculerTauxReussite(
                    Arrays.asList()));
        }
    }

    // ===== TAUX D'ECHEC =====

    @Nested
    @DisplayName("calculerTauxEchec")
    class CalculerTauxEchec {

        @Test
        @DisplayName("Échec = complément de la réussite")
        void complementReussite() {
            // 1 sur 2 en échec → 50%
            assertEquals(50.0,
                    CalculKpiUtils.calculerTauxEchec(
                            Arrays.asList(5.33, 13.67)), 0.01);
        }

        @Test
        @DisplayName("Toutes réussies → 0% d'échec")
        void aucunEchec() {
            assertEquals(0.0,
                    CalculKpiUtils.calculerTauxEchec(
                            Arrays.asList(10.0, 15.0)), 0.01);
        }

        @Test
        @DisplayName("Liste vide : retourne null")
        void listeVide() {
            assertNull(CalculKpiUtils.calculerTauxEchec(
                    Arrays.asList()));
        }
    }

    // ===== TAUX DE RATTRAPAGE =====

    @Nested
    @DisplayName("calculerTauxRattrapage")
    class CalculerTauxRattrapage {

        @Test
        @DisplayName("Notes < 10 avant rattrapage → convoqués")
        void convoquesEnRattrapage() {
            // 5.33 < 10 (convoqué), 13.67 >= 10 → 1 sur 2 = 50%
            assertEquals(50.0,
                    CalculKpiUtils.calculerTauxRattrapage(
                            Arrays.asList(5.33, 13.67)), 0.01);
        }

        @Test
        @DisplayName("Note exactement à 10 : pas de rattrapage")
        void noteA10PasDeRattrapage() {
            // 10.0 n'est pas < 10 → 0%
            assertEquals(0.0,
                    CalculKpiUtils.calculerTauxRattrapage(
                            Arrays.asList(10.0, 15.0)), 0.01);
        }

        @Test
        @DisplayName("Liste vide : retourne null")
        void listeVide() {
            assertNull(CalculKpiUtils.calculerTauxRattrapage(
                    Arrays.asList()));
        }
    }
}