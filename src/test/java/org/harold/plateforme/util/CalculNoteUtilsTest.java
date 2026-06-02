package org.harold.plateforme.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests unitaires de CalculNoteUtils.
 *
 * <p>Vérifie les formules de calcul des notes finales,
 * des moyennes et des règles de validation.</p>
 *
 * @author Harold
 * @version 1.0
 */
@DisplayName("CalculNoteUtils — calculs de notes et moyennes")
class CalculNoteUtilsTest {

    // ===== NOTE MATIERE SANS RATTRAPAGE =====

    @Nested
    @DisplayName("calculerNoteMatiere")
    class CalculerNoteMatiere {

        @Test
        @DisplayName("CC + EF pondérés retournent la bonne moyenne")
        void avecCCetEF_retourneMoyennePonderee() {
            // (12×1 + 9×2) / 3 = 10.0
            Double resultat = CalculNoteUtils.calculerNoteMatiere(
                    12.0, 1.0,
                    9.0, 2.0,
                    null, null);
            assertEquals(10.0, resultat, 0.01);
        }

        @Test
        @DisplayName("CC + EF + TP pondérés retournent la bonne moyenne")
        void avecCCetEFetTP_retourneMoyennePonderee() {
            // (12×1 + 9×2 + 15×1) / 4 = 45/4 = 11.25
            Double resultat = CalculNoteUtils.calculerNoteMatiere(
                    12.0, 1.0,
                    9.0, 2.0,
                    15.0, 1.0);
            assertEquals(11.25, resultat, 0.01);
        }

        @Test
        @DisplayName("Note CC manquante : ignorée du calcul")
        void avecCCnull_ignoreLeCC() {
            // Seul l'EF compte : (9×2)/2 = 9.0
            Double resultat = CalculNoteUtils.calculerNoteMatiere(
                    null, 1.0,
                    9.0, 2.0,
                    null, null);
            assertEquals(9.0, resultat, 0.01);
        }

        @Test
        @DisplayName("TP avec coefficient 0 : ignoré du calcul")
        void avecCoeffTPzero_ignoreLeTP() {
            // TP ignoré car coeff = 0 : (12×1 + 9×2)/3 = 10.0
            Double resultat = CalculNoteUtils.calculerNoteMatiere(
                    12.0, 1.0,
                    9.0, 2.0,
                    15.0, 0.0);
            assertEquals(10.0, resultat, 0.01);
        }

        @Test
        @DisplayName("Aucune note disponible : retourne null")
        void sansAucuneNote_retourneNull() {
            Double resultat = CalculNoteUtils.calculerNoteMatiere(
                    null, 1.0,
                    null, 2.0,
                    null, null);
            assertNull(resultat);
        }
        // ===== NOTE MATIERE AVEC RATTRAPAGE =====

        @Nested
        @DisplayName("calculerNoteMatiereAvecRattrapage")
        class CalculerNoteMatiereAvecRattrapage {

            @Test
            @DisplayName("Le rattrapage remplace l'EF dans le calcul")
            void leRattrapageRemplaceLEF() {
                // Amine simulé : (12×1 + 11×2)/3 = 34/3 = 11.33
                Double resultat = CalculNoteUtils
                        .calculerNoteMatiereAvecRattrapage(
                                12.0, 1.0,
                                11.0, 2.0,
                                null, null);
                assertEquals(11.33, resultat, 0.01);
            }

            @Test
            @DisplayName("Rattrapage avec TP : le TP est conservé")
            void avecTP_conserveLeTP() {
                // (12×1 + 11×2 + 8×1)/4 = 42/4 = 10.5
                Double resultat = CalculNoteUtils
                        .calculerNoteMatiereAvecRattrapage(
                                12.0, 1.0,
                                11.0, 2.0,
                                8.0, 1.0);
                assertEquals(10.5, resultat, 0.01);
            }
        }

        // ===== MOYENNE GROUPE =====

        @Nested
        @DisplayName("calculerMoyenneGroupe")
        class CalculerMoyenneGroupe {

            @Test
            @DisplayName("Moyenne pondérée de plusieurs matières")
            void plusieursMatieresPonderees() {
                // (14×2 + 8×1) / 3 = 36/3 = 12.0
                Map<Double, Double> notes = new HashMap<>();
                notes.put(14.0, 2.0);
                notes.put(8.0, 1.0);
                Double resultat = CalculNoteUtils.calculerMoyenneGroupe(notes);
                assertEquals(12.0, resultat, 0.01);
            }

            @Test
            @DisplayName("Map vide : retourne null")
            void mapVide_retourneNull() {
                Double resultat = CalculNoteUtils.calculerMoyenneGroupe(
                        new HashMap<>());
                assertNull(resultat);
            }

            @Test
            @DisplayName("Map null : retourne null")
            void mapNull_retourneNull() {
                Double resultat = CalculNoteUtils.calculerMoyenneGroupe(null);
                assertNull(resultat);
            }
        }

        // ===== MOYENNE ANNUELLE =====

        @Nested
        @DisplayName("calculerMoyenneAnnuelle")
        class CalculerMoyenneAnnuelle {

            @Test
            @DisplayName("Moyenne des deux semestres à 50/50")
            void deuxSemestres_moyenne5050() {
                // (12×0.5) + (8×0.5) = 10.0
                Double resultat = CalculNoteUtils.calculerMoyenneAnnuelle(
                        12.0, 8.0);
                assertEquals(10.0, resultat, 0.01);
            }

            @Test
            @DisplayName("S1 null : retourne null (vision stricte)")
            void s1Null_retourneNull() {
                Double resultat = CalculNoteUtils.calculerMoyenneAnnuelle(
                        null, 8.0);
                assertNull(resultat);
            }

            @Test
            @DisplayName("S2 null : retourne null (vision stricte)")
            void s2Null_retourneNull() {
                Double resultat = CalculNoteUtils.calculerMoyenneAnnuelle(
                        12.0, null);
                assertNull(resultat);
            }
        }
        // ===== VALIDATION GROUPE =====

        @Nested
        @DisplayName("estGroupeValide")
        class EstGroupeValide {

            @Test
            @DisplayName("Moyenne >= 10 : groupe validé")
            void moyenneSuperieureA10_valide() {
                assertTrue(CalculNoteUtils.estGroupeValide(10.0));
                assertTrue(CalculNoteUtils.estGroupeValide(15.5));
            }

            @Test
            @DisplayName("Moyenne < 10 : groupe non validé")
            void moyenneInferieureA10_nonValide() {
                assertFalse(CalculNoteUtils.estGroupeValide(9.99));
                assertFalse(CalculNoteUtils.estGroupeValide(5.0));
            }

            @Test
            @DisplayName("Moyenne null : retourne null")
            void moyenneNull_retourneNull() {
                assertNull(CalculNoteUtils.estGroupeValide(null));
            }
        }

        // ===== VALIDATION SEMESTRE =====

        @Nested
        @DisplayName("estSemestreValide")
        class EstSemestreValide {

            @Test
            @DisplayName("Tous les groupes >= 10 : semestre validé")
            void tousGroupesSuperieursA10_valide() {
                assertTrue(CalculNoteUtils.estSemestreValide(
                        List.of(10.0, 12.5, 14.0)));
            }

            @Test
            @DisplayName("Un seul groupe < 10 : semestre non validé")
            void unGroupeInferieurA10_nonValide() {
                // Pas de compensation entre groupes
                assertFalse(CalculNoteUtils.estSemestreValide(
                        List.of(15.0, 9.0, 14.0)));
            }

            @Test
            @DisplayName("Liste vide : retourne null")
            void listeVide_retourneNull() {
                assertNull(CalculNoteUtils.estSemestreValide(List.of()));
            }

            @Test
            @DisplayName("Liste null : retourne null")
            void listeNull_retourneNull() {
                assertNull(CalculNoteUtils.estSemestreValide(null));
            }
        }

        // ===== VALIDATION ANNEE =====

        @Nested
        @DisplayName("estAnneeValidee")
        class EstAnneeValidee {

            @Test
            @DisplayName("S1 et S2 validés : année validée")
            void s1EtS2Valides_valide() {
                assertTrue(CalculNoteUtils.estAnneeValidee(true, true));
            }

            @Test
            @DisplayName("Un semestre non validé : année non validée")
            void unSemestreNonValide_nonValide() {
                assertFalse(CalculNoteUtils.estAnneeValidee(true, false));
                assertFalse(CalculNoteUtils.estAnneeValidee(false, true));
            }

            @Test
            @DisplayName("Un statut null : retourne null")
            void unStatutNull_retourneNull() {
                assertNull(CalculNoteUtils.estAnneeValidee(true, null));
                assertNull(CalculNoteUtils.estAnneeValidee(null, true));
            }
        }

        // ===== PROGRESSION =====

        @Nested
        @DisplayName("calculerProgression")
        class CalculerProgression {

            @Test
            @DisplayName("Progression positive : S2 > S1")
            void progressionPositive() {
                // 14 - 10 = 4.0
                assertEquals(4.0,
                        CalculNoteUtils.calculerProgression(10.0, 14.0),
                        0.01);
            }

            @Test
            @DisplayName("Progression négative : S2 < S1")
            void progressionNegative() {
                // 8 - 12 = -4.0
                assertEquals(-4.0,
                        CalculNoteUtils.calculerProgression(12.0, 8.0),
                        0.01);
            }

            @Test
            @DisplayName("Un semestre null : retourne null")
            void unSemestreNull_retourneNull() {
                assertNull(CalculNoteUtils.calculerProgression(null, 14.0));
            }
        }

        // ===== TENDANCE =====

        @Nested
        @DisplayName("calculerTendance")
        class CalculerTendance {

            @Test
            @DisplayName("Hausse > 0.5 : PROGRESSION")
            void hausseSignificative_progression() {
                assertEquals("PROGRESSION",
                        CalculNoteUtils.calculerTendance(13.0, 12.0));
            }

            @Test
            @DisplayName("Baisse > 0.5 : REGRESSION")
            void baisseSignificative_regression() {
                assertEquals("REGRESSION",
                        CalculNoteUtils.calculerTendance(10.0, 12.0));
            }

            @Test
            @DisplayName("Variation faible : STABLE")
            void variationFaible_stable() {
                // différence de 0.3, dans la zone neutre [-0.5, 0.5]
                assertEquals("STABLE",
                        CalculNoteUtils.calculerTendance(12.3, 12.0));
            }

            @Test
            @DisplayName("Une note null : retourne null")
            void uneNoteNull_retourneNull() {
                assertNull(CalculNoteUtils.calculerTendance(null, 12.0));
            }
        }
    }
}