package org.harold.plateforme.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.harold.plateforme.dto.etudiant.FicheEtudiantResponsableDTO;
import org.harold.plateforme.dto.etudiant.NoteEtudiantDTO;
import org.harold.plateforme.dto.etudiant.SemestreDetailDTO;
import org.harold.plateforme.dto.etudiant.GroupeMatieresDetailDTO;
import org.harold.plateforme.dto.kpi.ComparaisonPromoDTO;
import org.harold.plateforme.dto.kpi.EntreeComparaisonDTO;
import org.harold.plateforme.dto.kpi.KpiAnnuelDTO;
import org.harold.plateforme.dto.kpi.KpiMatiereDTO;
import org.harold.plateforme.dto.kpi.SimulationSemestreResultDTO;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utilitaire pour la génération de rapports PDF.
 *
 * <p>Utilise la librairie iText 8 pour générer des PDFs
 * à partir des DTOs. La mise en forme est simple en V1 :
 * en-tête, titre, tableaux et texte sans couleurs ni graphiques.
 * Les graphiques et couleurs sont prévus en V2.</p>
 *
 * <p>Toutes les méthodes sont statiques et retournent
 * un byte[] représentant le fichier PDF généré.</p>
 *
 * @author Harold
 * @version 1.0
 */
public class PdfGeneratorUtils {

    /** Format de date utilisé dans les rapports. */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constructeur privé — classe utilitaire statique uniquement.
     */
    private PdfGeneratorUtils() {}

    // ===== MÉTHODES PRIVÉES UTILITAIRES =====

    /**
     * Crée un document PDF et retourne le flux de sortie.
     *
     * @param outputStream  le flux de sortie du PDF
     * @return              le document iText prêt à être rempli
     */
    private static Document creerDocument(ByteArrayOutputStream outputStream) {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        return new Document(pdfDoc);
    }

    /**
     * Ajoute l'en-tête standard à un document PDF.
     *
     * <p>L'en-tête contient le nom de la plateforme et la date
     * de génération du rapport.</p>
     *
     * @param document  le document iText
     * @param titre     le titre du rapport
     */
    private static void ajouterEntete(Document document, String titre) {
        document.add(new Paragraph("Plateforme de Suivi Pédagogique")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16)
                .setBold());
        document.add(new Paragraph(titre)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14)
                .setBold());
        document.add(new Paragraph("Généré le : " +
                LocalDate.now().format(DATE_FORMATTER))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10));
        document.add(new Paragraph("\n"));
    }

    /**
     * Crée une cellule d'en-tête de tableau.
     *
     * @param texte     le texte de la cellule
     * @return          la cellule formatée
     */
    private static Cell celluleEntete(String texte) {
        return new Cell()
                .add(new Paragraph(texte).setBold())
                .setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * Crée une cellule de données de tableau.
     *
     * @param texte     le texte de la cellule
     * @return          la cellule formatée
     */
    private static Cell celluleDonnee(String texte) {
        return new Cell()
                .add(new Paragraph(texte != null ? texte : "—"))
                .setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * Formate une note Double en String.
     *
     * <p>Retourne "—" si la note est null.</p>
     *
     * @param note  la note à formater
     * @return      la note formatée ou "—"
     */
    private static String formaterNote(Double note) {
        if (note == null) return "—";
        return String.format("%.2f", note);
    }

    /**
     * Formate un pourcentage Double en String.
     *
     * @param taux  le taux à formater
     * @return      le taux formaté avec % ou "—"
     */
    private static String formaterTaux(Double taux) {
        if (taux == null) return "—";
        return String.format("%.2f%%", taux);
    }

    /**
     * Formate un statut Boolean en String lisible.
     *
     * @param valide    le statut à formater
     * @return          "Validé", "Non validé" ou "—"
     */
    private static String formaterStatut(Boolean valide) {
        if (valide == null) return "—";
        return valide ? "Validé" : "Non validé";
    }

    // ===== MÉTHODES PUBLIQUES =====

    /**
     * Génère le rapport PDF complet d'un étudiant.
     *
     * <p>Contient les informations de base, les moyennes,
     * les statuts de validation, le score de risque
     * et le détail des notes par semestre et par matière.</p>
     *
     * @param fiche     la fiche complète de l'étudiant
     * @return          le PDF généré en byte[]
     */
    public static byte[] genererRapportEtudiant(
            FicheEtudiantResponsableDTO fiche) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = creerDocument(outputStream);

        // En-tête
        ajouterEntete(document, "Fiche Étudiant");

        // Informations de base
        document.add(new Paragraph("Informations générales")
                .setBold().setFontSize(12));
        Table infoTable = new Table(UnitValue.createPercentArray(2))
                .useAllAvailableWidth();
        infoTable.addCell(celluleDonnee("Nom : " + fiche.getNom()
                + " " + fiche.getPrenom()));
        infoTable.addCell(celluleDonnee("N° Étudiant : "
                + fiche.getNumeroEtudiant()));
        infoTable.addCell(celluleDonnee("Promotion : "
                + fiche.getPromotionNom()));
        infoTable.addCell(celluleDonnee("Niveau : " + fiche.getNiveau()));
        infoTable.addCell(celluleDonnee("Année académique : "
                + fiche.getAnneeAcademique()));
        infoTable.addCell(celluleDonnee("Email : " + fiche.getEmail()));
        document.add(infoTable);
        document.add(new Paragraph("\n"));

        // Moyennes et statuts
        document.add(new Paragraph("Résultats")
                .setBold().setFontSize(12));
        Table resultatsTable = new Table(
                UnitValue.createPercentArray(4))
                .useAllAvailableWidth();
        resultatsTable.addCell(celluleEntete("Moyenne S1"));
        resultatsTable.addCell(celluleEntete("Moyenne S2"));
        resultatsTable.addCell(celluleEntete("Moyenne annuelle"));
        resultatsTable.addCell(celluleEntete("Statut"));
        resultatsTable.addCell(celluleDonnee(
                formaterNote(fiche.getMoyenneS1())));
        resultatsTable.addCell(celluleDonnee(
                formaterNote(fiche.getMoyenneS2())));
        resultatsTable.addCell(celluleDonnee(
                formaterNote(fiche.getMoyenneGenerale())));
        resultatsTable.addCell(celluleDonnee(
                formaterStatut(fiche.getAnneeValidee())));
        document.add(resultatsTable);
        document.add(new Paragraph("\n"));

        // Score de risque
        document.add(new Paragraph("Score de risque : "
                + formaterNote(fiche.getScoreRisque())
                + " / 100 — Niveau : "
                + (fiche.getNiveauRisque() != null
                ? fiche.getNiveauRisque() : "—"))
                .setFontSize(11));
        document.add(new Paragraph("\n"));

        // Détail par semestre
        if (fiche.getSemestreS1() != null) {
            ajouterDetailSemestre(document, fiche.getSemestreS1());
        }
        if (fiche.getSemestreS2() != null) {
            ajouterDetailSemestre(document, fiche.getSemestreS2());
        }

        document.close();
        return outputStream.toByteArray();
    }

    /**
     * Ajoute le détail d'un semestre au document PDF.
     *
     * <p>Méthode privée utilisée par genererRapportEtudiant()
     * pour chaque semestre.</p>
     *
     * @param document  le document iText
     * @param semestre  le détail du semestre à ajouter
     */
    private static void ajouterDetailSemestre(
            Document document,
            SemestreDetailDTO semestre) {

        document.add(new Paragraph("Semestre " + semestre.getNumero()
                + " — Moyenne : " + formaterNote(semestre.getMoyenne())
                + " — " + formaterStatut(semestre.getValide()))
                .setBold().setFontSize(11));

        if (semestre.getGroupesMatieres() != null) {
            for (GroupeMatieresDetailDTO groupe
                    : semestre.getGroupesMatieres()) {

                document.add(new Paragraph("  Groupe : "
                        + groupe.getNom()
                        + " — Moyenne : "
                        + formaterNote(groupe.getMoyenne())
                        + " — "
                        + formaterStatut(groupe.getValide()))
                        .setFontSize(10));

                // Tableau des matières du groupe
                Table matiereTable = new Table(
                        UnitValue.createPercentArray(
                                new float[]{3, 1, 1, 1, 1, 1, 2}))
                        .useAllAvailableWidth();
                matiereTable.addCell(celluleEntete("Matière"));
                matiereTable.addCell(celluleEntete("CC"));
                matiereTable.addCell(celluleEntete("EF"));
                matiereTable.addCell(celluleEntete("TP"));
                matiereTable.addCell(celluleEntete("RAT"));
                matiereTable.addCell(celluleEntete("Finale"));
                matiereTable.addCell(celluleEntete("Statut"));

                if (groupe.getMatieres() != null) {
                    for (NoteEtudiantDTO note : groupe.getMatieres()) {
                        matiereTable.addCell(
                                celluleDonnee(note.getMatiereNom()));
                        matiereTable.addCell(
                                celluleDonnee(formaterNote(note.getNoteCC())));
                        matiereTable.addCell(
                                celluleDonnee(formaterNote(note.getNoteEF())));
                        matiereTable.addCell(
                                celluleDonnee(formaterNote(note.getNoteTP())));
                        matiereTable.addCell(
                                celluleDonnee(formaterNote(
                                        note.getNoteRattrapage())));
                        matiereTable.addCell(
                                celluleDonnee(formaterNote(
                                        note.getNoteFinale())));
                        matiereTable.addCell(
                                celluleDonnee(formaterStatut(note.getValide())));
                    }
                }
                document.add(matiereTable);
                document.add(new Paragraph("\n"));
            }
        }
    }

    /**
     * Génère le rapport PDF des KPI d'une matière.
     *
     * <p>Contient les 8 KPI de la matière :
     * moyenne, min, max, écart-type, médiane,
     * taux de réussite, taux d'échec, taux de rattrapage.</p>
     *
     * @param kpi   les KPI de la matière
     * @return      le PDF généré en byte[]
     */
    public static byte[] genererRapportMatiere(KpiMatiereDTO kpi) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = creerDocument(outputStream);

        ajouterEntete(document, "Rapport Matière — " + kpi.getMatiereNom());

        // Informations matière
        document.add(new Paragraph("Matière : " + kpi.getMatiereNom()
                + " (" + kpi.getMatiereCode() + ")")
                .setFontSize(11));
        document.add(new Paragraph("Promotion : " + kpi.getPromotionNom()
                + (kpi.getGroupeNom() != null
                ? " — Groupe : " + kpi.getGroupeNom() : ""))
                .setFontSize(11));
        document.add(new Paragraph("Année académique : "
                + kpi.getAnneeAcademique())
                .setFontSize(11));
        document.add(new Paragraph("Nombre d'étudiants : "
                + kpi.getNbEtudiants())
                .setFontSize(11));
        document.add(new Paragraph("\n"));

        // Tableau des 8 KPI
        document.add(new Paragraph("Statistiques")
                .setBold().setFontSize(12));
        Table kpiTable = new Table(
                UnitValue.createPercentArray(2))
                .useAllAvailableWidth();
        kpiTable.addCell(celluleDonnee("Moyenne"));
        kpiTable.addCell(celluleDonnee(formaterNote(kpi.getMoyenne())));
        kpiTable.addCell(celluleDonnee("Minimum"));
        kpiTable.addCell(celluleDonnee(formaterNote(kpi.getMinimum())));
        kpiTable.addCell(celluleDonnee("Maximum"));
        kpiTable.addCell(celluleDonnee(formaterNote(kpi.getMaximum())));
        kpiTable.addCell(celluleDonnee("Écart-type"));
        kpiTable.addCell(celluleDonnee(formaterNote(kpi.getEcartType())));
        kpiTable.addCell(celluleDonnee("Médiane"));
        kpiTable.addCell(celluleDonnee(formaterNote(kpi.getMediane())));
        kpiTable.addCell(celluleDonnee("Taux de réussite"));
        kpiTable.addCell(celluleDonnee(formaterTaux(kpi.getTauxReussite())));
        kpiTable.addCell(celluleDonnee("Taux d'échec"));
        kpiTable.addCell(celluleDonnee(formaterTaux(kpi.getTauxEchec())));
        kpiTable.addCell(celluleDonnee("Taux de rattrapage"));
        kpiTable.addCell(celluleDonnee(
                formaterTaux(kpi.getTauxRattrapage())));
        document.add(kpiTable);

        document.close();
        return outputStream.toByteArray();
    }

    /**
     * Génère le rapport PDF des KPI annuels d'une promotion.
     *
     * <p>Contient les 7 KPI annuels :
     * moyenne, min, max, écart-type, médiane,
     * taux de réussite, taux d'échec.</p>
     *
     * @param kpi   les KPI annuels de la promotion
     * @return      le PDF généré en byte[]
     */
    public static byte[] genererRapportPromotion(KpiAnnuelDTO kpi) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = creerDocument(outputStream);

        ajouterEntete(document,
                "Rapport Promotion — " + kpi.getPromotionNom());

        document.add(new Paragraph("Promotion : " + kpi.getPromotionNom())
                .setFontSize(11));
        document.add(new Paragraph("Année académique : "
                + kpi.getAnneeAcademique()).setFontSize(11));
        document.add(new Paragraph("Nombre d'étudiants : "
                + kpi.getNbEtudiants()).setFontSize(11));
        document.add(new Paragraph("\n"));

        // Tableau des 7 KPI
        document.add(new Paragraph("Statistiques annuelles")
                .setBold().setFontSize(12));
        Table kpiTable = new Table(
                UnitValue.createPercentArray(2))
                .useAllAvailableWidth();
        kpiTable.addCell(celluleDonnee("Moyenne annuelle"));
        kpiTable.addCell(celluleDonnee(formaterNote(kpi.getMoyenne())));
        kpiTable.addCell(celluleDonnee("Minimum"));
        kpiTable.addCell(celluleDonnee(formaterNote(kpi.getMinimum())));
        kpiTable.addCell(celluleDonnee("Maximum"));
        kpiTable.addCell(celluleDonnee(formaterNote(kpi.getMaximum())));
        kpiTable.addCell(celluleDonnee("Écart-type"));
        kpiTable.addCell(celluleDonnee(formaterNote(kpi.getEcartType())));
        kpiTable.addCell(celluleDonnee("Médiane"));
        kpiTable.addCell(celluleDonnee(formaterNote(kpi.getMediane())));
        kpiTable.addCell(celluleDonnee("Taux de réussite"));
        kpiTable.addCell(celluleDonnee(
                formaterTaux(kpi.getTauxReussite())));
        kpiTable.addCell(celluleDonnee("Taux d'échec"));
        kpiTable.addCell(celluleDonnee(formaterTaux(kpi.getTauxEchec())));
        document.add(kpiTable);

        document.close();
        return outputStream.toByteArray();
    }

    /**
     * Génère le rapport PDF d'une comparaison inter-promos
     * ou inter-années.
     *
     * <p>Contient un tableau comparatif avec les KPI
     * de chaque promotion ou année académique.</p>
     *
     * @param comparaison   les données de comparaison
     * @return              le PDF généré en byte[]
     */
    public static byte[] genererRapportComparaison(
            ComparaisonPromoDTO comparaison) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = creerDocument(outputStream);

        ajouterEntete(document, "Rapport de Comparaison");

        document.add(new Paragraph("Type : "
                + comparaison.getTypeComparaison())
                .setFontSize(11));
        document.add(new Paragraph("Niveau : "
                + comparaison.getNiveauComparaison())
                .setFontSize(11));
        if (comparaison.getMatiereNom() != null) {
            document.add(new Paragraph("Matière : "
                    + comparaison.getMatiereNom())
                    .setFontSize(11));
        }
        document.add(new Paragraph("\n"));

        // Tableau comparatif
        Table table = new Table(
                UnitValue.createPercentArray(
                        new float[]{2, 1, 1, 1, 1, 1, 1, 1}))
                .useAllAvailableWidth();
        table.addCell(celluleEntete("Promotion / Année"));
        table.addCell(celluleEntete("Moyenne"));
        table.addCell(celluleEntete("Min"));
        table.addCell(celluleEntete("Max"));
        table.addCell(celluleEntete("Écart-type"));
        table.addCell(celluleEntete("Médiane"));
        table.addCell(celluleEntete("Réussite"));
        table.addCell(celluleEntete("Échec"));

        if (comparaison.getEntrees() != null) {
            for (EntreeComparaisonDTO entree : comparaison.getEntrees()) {
                String label = entree.getPromotionNom() != null
                        ? entree.getPromotionNom()
                        : entree.getAnneeAcademique();
                table.addCell(celluleDonnee(label));
                table.addCell(celluleDonnee(
                        formaterNote(entree.getMoyenne())));
                table.addCell(celluleDonnee(
                        formaterNote(entree.getMinimum())));
                table.addCell(celluleDonnee(
                        formaterNote(entree.getMaximum())));
                table.addCell(celluleDonnee(
                        formaterNote(entree.getEcartType())));
                table.addCell(celluleDonnee(
                        formaterNote(entree.getMediane())));
                table.addCell(celluleDonnee(
                        formaterTaux(entree.getTauxReussite())));
                table.addCell(celluleDonnee(
                        formaterTaux(entree.getTauxEchec())));
            }
        }
        document.add(table);

        document.close();
        return outputStream.toByteArray();
    }

    /**
     * Génère le rapport PDF d'une simulation pédagogique.
     *
     * <p>Contient le résultat de la simulation au niveau
     * matière, groupe de matières et semestre avec
     * la comparaison avant/après simulation.</p>
     *
     * @param simulation    le résultat de la simulation
     * @return              le PDF généré en byte[]
     */
    public static byte[] genererRapportSimulation(
            SimulationSemestreResultDTO simulation) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = creerDocument(outputStream);

        ajouterEntete(document, "Rapport de Simulation Pédagogique");

        // Informations générales
        document.add(new Paragraph("Étudiant : "
                + simulation.getEtudiantNom()
                + " " + simulation.getEtudiantPrenom()
                + " (" + simulation.getNumeroEtudiant() + ")")
                .setFontSize(11));
        document.add(new Paragraph("Semestre : "
                + simulation.getNumeroSemestre())
                .setFontSize(11));
        document.add(new Paragraph("Note simulée : "
                + formaterNote(simulation.getNoteSimulee()) + " / 20")
                .setFontSize(11));
        document.add(new Paragraph("\n"));

        // Tableau comparatif avant/après simulation
        document.add(new Paragraph("Résultats de la simulation")
                .setBold().setFontSize(12));
        Table table = new Table(
                UnitValue.createPercentArray(
                        new float[]{3, 2, 2, 2, 2}))
                .useAllAvailableWidth();
        table.addCell(celluleEntete("Niveau"));
        table.addCell(celluleEntete("Avant simulation"));
        table.addCell(celluleEntete("Après simulation"));
        table.addCell(celluleEntete("Statut avant"));
        table.addCell(celluleEntete("Statut après"));

        // Niveau matière
        table.addCell(celluleDonnee(
                "Matière : " + simulation.getMatiereNom()));
        table.addCell(celluleDonnee(
                formaterNote(simulation.getNoteMatiereActuelle())));
        table.addCell(celluleDonnee(
                formaterNote(simulation.getNoteMatiereSimulee())));
        table.addCell(celluleDonnee(
                simulation.isMatiereValideActuellement()
                        ? "Validé" : "Non validé"));
        table.addCell(celluleDonnee(
                simulation.isMatiereValideAvecSimulation()
                        ? "Validé" : "Non validé"));

        // Niveau groupe de matières
        table.addCell(celluleDonnee(
                "Groupe : " + simulation.getGroupeMatieresNom()));
        table.addCell(celluleDonnee(
                formaterNote(simulation.getMoyenneGroupeActuelle())));
        table.addCell(celluleDonnee(
                formaterNote(simulation.getMoyenneGroupeSimulee())));
        table.addCell(celluleDonnee(
                simulation.isGroupeValideActuellement()
                        ? "Validé" : "Non validé"));
        table.addCell(celluleDonnee(
                simulation.isGroupeValideAvecSimulation()
                        ? "Validé" : "Non validé"));

        // Niveau semestre
        table.addCell(celluleDonnee(
                "Semestre " + simulation.getNumeroSemestre()));
        table.addCell(celluleDonnee(
                formaterNote(simulation.getMoyenneSemestreActuelle())));
        table.addCell(celluleDonnee(
                formaterNote(simulation.getMoyenneSemestreSimulee())));
        table.addCell(celluleDonnee(
                simulation.isSemestreValideActuellement()
                        ? "Validé" : "Non validé"));
        table.addCell(celluleDonnee(
                simulation.isSemestreValideAvecSimulation()
                        ? "Validé" : "Non validé"));

        document.add(table);

        document.close();
        return outputStream.toByteArray();
    }
}