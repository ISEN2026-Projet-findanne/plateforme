package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.harold.plateforme.dto.csv.CsvImportResultDTO;
import org.harold.plateforme.dto.csv.CsvMappingDTO;
import org.harold.plateforme.entity.MappingCsv;
import org.harold.plateforme.entity.Note;
import org.harold.plateforme.entity.TypeEvaluation;
import org.harold.plateforme.exception.CsvImportException;
import org.harold.plateforme.repository.EtudiantRepository;
import org.harold.plateforme.repository.GroupeClasseRepository;
import org.harold.plateforme.repository.InscriptionRepository;
import org.harold.plateforme.repository.MappingCsvRepository;
import org.harold.plateforme.repository.MatiereRepository;
import org.harold.plateforme.repository.NoteRepository;
import org.harold.plateforme.repository.TypeEvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service d'import CSV/XLSX des données pédagogiques.
 *
 * <p>Gère l'import des étudiants depuis un CSV dédié
 * et l'import des notes depuis les fichiers THEIA (xlsx)
 * et Aurion (csv latin-1). Utilise un mapping adaptatif
 * des colonnes via le dictionnaire MappingCsv.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CsvService {

    private final EtudiantService etudiantService;
    private final NoteService noteService;
    private final MatiereService matiereService;
    private final GroupeClasseService groupeClasseService;
    private final AnneeAcademiqueService anneeAcademiqueService;
    private final PromotionService promotionService;
    private final EtudiantRepository etudiantRepository;
    private final MatiereRepository matiereRepository;
    private final GroupeClasseRepository groupeClasseRepository;
    private final NoteRepository noteRepository;
    private final TypeEvaluationRepository typeEvaluationRepository;
    private final InscriptionRepository inscriptionRepository;
    private final MappingCsvRepository mappingCsvRepository;

    /** Format de date attendu dans le CSV étudiant. */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ===== IMPORT ETUDIANTS =====

    /**
     * Importe une liste d'étudiants depuis un fichier CSV.
     *
     * <p>Format attendu : nom, prenom, numeroEtudiant,
     * email, dateNaissance, niveau.
     * La promotion et l'année académique sont passées
     * en paramètre.</p>
     *
     * @param fichier           le fichier CSV
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le rapport d'import
     * @throws CsvImportException   si le fichier est invalide
     */
    @Transactional
    public CsvImportResultDTO importerEtudiants(
            MultipartFile fichier,
            Long promotionId,
            Long anneeAcademiqueId) {

        CsvImportResultDTO rapport = new CsvImportResultDTO();
        rapport.setFormatDetecte("ETUDIANTS");
        List<String> erreurs = new ArrayList<>();
        List<String> avertissements = new ArrayList<>();

        int nbLues = 0;
        int nbInsereees = 0;
        int nbIgnorees = 0;
        int nbErreurs = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        fichier.getInputStream(),
                        StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                nbLues++;
                int numeroLigne = (int) record.getRecordNumber() + 1;

                try {
                    // Récupérer les champs
                    String nom = record.get("nom");
                    String prenom = record.get("prenom");
                    String numeroEtudiant =
                            record.get("numeroetudiants");
                    String email = getChampOptional(
                            record, "email");
                    String dateNaissanceStr = getChampOptional(
                            record, "datenaissance");
                    String niveau = record.get("niveau");

                    // Valider champs obligatoires
                    if (nom == null || nom.isEmpty() ||
                            prenom == null || prenom.isEmpty() ||
                            numeroEtudiant == null ||
                            numeroEtudiant.isEmpty() ||
                            niveau == null || niveau.isEmpty()) {
                        erreurs.add("Ligne " + numeroLigne
                                + " : champs obligatoires manquants");
                        nbErreurs++;
                        continue;
                    }

                    // Vérifier si étudiant existe déjà
                    if (etudiantRepository.existsByNumeroEtudiant(
                            numeroEtudiant)) {
                        // Vérifier si inscription existe
                        Optional<org.harold.plateforme.entity.Etudiant>
                                etudiantOpt = etudiantRepository
                                .findByNumeroEtudiant(numeroEtudiant);

                        if (etudiantOpt.isPresent() &&
                                inscriptionRepository
                                        .existsByEtudiantIdAndPromotionIdAndAnneeAcademiqueId(
                                                etudiantOpt.get().getId(),
                                                promotionId,
                                                anneeAcademiqueId)) {
                            avertissements.add("Ligne " + numeroLigne
                                    + " : étudiant " + numeroEtudiant
                                    + " déjà inscrit — ignoré");
                            nbIgnorees++;
                            continue;
                        }
                    }

                    // Parser date de naissance
                    LocalDate dateNaissance = null;
                    if (dateNaissanceStr != null &&
                            !dateNaissanceStr.isEmpty()) {
                        try {
                            dateNaissance = LocalDate.parse(
                                    dateNaissanceStr, DATE_FORMATTER);
                        } catch (DateTimeParseException e) {
                            avertissements.add("Ligne " + numeroLigne
                                    + " : date de naissance invalide"
                                    + " — ignorée");
                        }
                    }

                    // Créer le DTO de création
                    org.harold.plateforme.dto.etudiant
                            .EtudiantCreateDTO dto =
                            new org.harold.plateforme.dto.etudiant
                                    .EtudiantCreateDTO();
                    dto.setNom(nom);
                    dto.setPrenom(prenom);
                    dto.setNumeroEtudiant(numeroEtudiant);
                    dto.setEmail(email);
                    dto.setDateNaissance(dateNaissance);
                    dto.setNiveau(niveau);
                    dto.setPromotionId(promotionId);
                    dto.setAnneeAcademiqueId(anneeAcademiqueId);

                    // Créer l'étudiant
                    etudiantService.creer(dto, 0L, "csv-import");
                    nbInsereees++;

                } catch (Exception e) {
                    erreurs.add("Ligne " + numeroLigne
                            + " : " + e.getMessage());
                    nbErreurs++;
                }
            }

        } catch (IOException e) {
            throw new CsvImportException(
                    "Erreur lecture fichier CSV : " + e.getMessage());
        }

        // Construire le rapport
        rapport.setNbLignesLues(nbLues);
        rapport.setNbLignesInsereees(nbInsereees);
        rapport.setNbLignesIgnorees(nbIgnorees);
        rapport.setNbLignesEnErreur(nbErreurs);
        rapport.setSucces(nbErreurs == 0);
        rapport.setErreurs(erreurs);
        rapport.setAvertissements(avertissements);
        rapport.setColonnesNonReconnues(new ArrayList<>());

        return rapport;
    }

    // ===== IMPORT NOTES =====

    /**
     * Importe des notes depuis un fichier THEIA ou Aurion.
     *
     * <p>Détecte automatiquement le format du fichier
     * et délègue au parseur approprié.</p>
     *
     * @param fichier           le fichier à importer
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param enseignantId      identifiant de l'enseignant
     * @return                  le rapport d'import
     */
    @Transactional
    public CsvImportResultDTO importerNotes(
            MultipartFile fichier,
            Long matiereId,
            Long anneeAcademiqueId,
            Long enseignantId) {

        String nomFichier = fichier.getOriginalFilename();

        if (nomFichier != null &&
                nomFichier.toLowerCase().endsWith(".xlsx")) {
            return importerNotesTheia(
                    fichier, matiereId,
                    anneeAcademiqueId, enseignantId);
        } else {
            return importerNotesAurion(
                    fichier, matiereId,
                    anneeAcademiqueId, enseignantId);
        }
    }

    /**
     * Importe des notes depuis un fichier THEIA (xlsx).
     *
     * <p>Colonnes : Plateforme, Nom, Prénom, Groupes,
     * Matricule, Moyenne standard...
     * Matricule = numeroEtudiant.
     * NC = note non communiquée → null.</p>
     *
     * @param fichier           le fichier xlsx
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param enseignantId      identifiant de l'enseignant
     * @return                  le rapport d'import
     */
    private CsvImportResultDTO importerNotesTheia(
            MultipartFile fichier,
            Long matiereId,
            Long anneeAcademiqueId,
            Long enseignantId) {

        CsvImportResultDTO rapport = new CsvImportResultDTO();
        rapport.setFormatDetecte("THEIA");
        List<String> erreurs = new ArrayList<>();
        List<String> avertissements = new ArrayList<>();

        int nbLues = 0;
        int nbInsereees = 0;
        int nbIgnorees = 0;
        int nbErreurs = 0;

        try (Workbook workbook = new XSSFWorkbook(
                fichier.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            // Lire l'en-tête
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colonnes =
                    lireEnTeteExcel(headerRow);

            // Identifier les colonnes THEIA
            Integer colMatricule = trouverColonne(
                    colonnes, "matricule", "ETUDIANTS");
            Integer colGroupe = trouverColonne(
                    colonnes, "groupes", "ETUDIANTS");
            Integer colNote = trouverColonneNote(colonnes);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                nbLues++;

                try {
                    String numeroEtudiant = getCellValue(
                            row, colMatricule);
                    String groupeNom = getCellValue(
                            row, colGroupe);
                    String noteStr = getCellValue(row, colNote);

                    // NC = non communiqué → ignorer
                    if ("NC".equalsIgnoreCase(noteStr) ||
                            noteStr == null ||
                            noteStr.isEmpty()) {
                        avertissements.add("Ligne " + (i + 1)
                                + " : note NC pour "
                                + numeroEtudiant + " — ignorée");
                        nbIgnorees++;
                        continue;
                    }

                    // Convertir la note (virgule → point)
                    Double valeur = parseNote(noteStr);
                    if (valeur == null) {
                        erreurs.add("Ligne " + (i + 1)
                                + " : note invalide '"
                                + noteStr + "'");
                        nbErreurs++;
                        continue;
                    }

                    // Insérer la note
                    boolean insere = insererNote(
                            numeroEtudiant, matiereId,
                            anneeAcademiqueId, enseignantId,
                            valeur, groupeNom,
                            Note.TypeNote.EXAMEN_FINAL);

                    if (insere) nbInsereees++;
                    else nbIgnorees++;

                } catch (Exception e) {
                    erreurs.add("Ligne " + (i + 1)
                            + " : " + e.getMessage());
                    nbErreurs++;
                }
            }

        } catch (IOException e) {
            throw new CsvImportException(
                    "Erreur lecture fichier THEIA : "
                            + e.getMessage());
        }

        construireRapport(rapport, nbLues, nbInsereees,
                nbIgnorees, nbErreurs, erreurs, avertissements);
        return rapport;
    }

    /**
     * Importe des notes depuis un fichier Aurion (csv latin-1).
     *
     * <p>Colonnes : id.Apprenant, Code.Épreuve,
     * Libellé.Épreuve, Note numérique...
     * id.Apprenant = numeroEtudiant.
     * Encodage latin-1, séparateur virgule décimale.</p>
     *
     * @param fichier           le fichier csv
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param enseignantId      identifiant de l'enseignant
     * @return                  le rapport d'import
     */
    private CsvImportResultDTO importerNotesAurion(
            MultipartFile fichier,
            Long matiereId,
            Long anneeAcademiqueId,
            Long enseignantId) {

        CsvImportResultDTO rapport = new CsvImportResultDTO();
        rapport.setFormatDetecte("AURION");
        List<String> erreurs = new ArrayList<>();
        List<String> avertissements = new ArrayList<>();

        int nbLues = 0;
        int nbInsereees = 0;
        int nbIgnorees = 0;
        int nbErreurs = 0;

        avertissements.add(
                "Encodage latin-1 détecté automatiquement");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        fichier.getInputStream(),
                        Charset.forName("latin-1")));
             CSVParser parser = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                nbLues++;
                int numeroLigne =
                        (int) record.getRecordNumber() + 1;

                try {
                    // Colonnes Aurion
                    String numeroEtudiant = getChampMapped(
                            record, "id.apprenant",
                            "numeroetudiants", "NOTES");
                    String noteStr = getChampMapped(
                            record, "note numerique",
                            "note.valeur", "NOTES");
                    String nonNote = getChampOptional(
                            record, "non note");

                    // Vérifier si non noté
                    if ("vrai".equalsIgnoreCase(nonNote) ||
                            "true".equalsIgnoreCase(nonNote)) {
                        avertissements.add("Ligne " + numeroLigne
                                + " : étudiant " + numeroEtudiant
                                + " non noté — ignoré");
                        nbIgnorees++;
                        continue;
                    }

                    if (noteStr == null || noteStr.isEmpty()) {
                        nbIgnorees++;
                        continue;
                    }

                    // Convertir la note
                    Double valeur = parseNote(noteStr);
                    if (valeur == null) {
                        erreurs.add("Ligne " + numeroLigne
                                + " : note invalide '"
                                + noteStr + "'");
                        nbErreurs++;
                        continue;
                    }

                    // Insérer la note
                    boolean insere = insererNote(
                            numeroEtudiant, matiereId,
                            anneeAcademiqueId, enseignantId,
                            valeur, null,
                            Note.TypeNote.EXAMEN_FINAL);

                    if (insere) nbInsereees++;
                    else nbIgnorees++;

                } catch (Exception e) {
                    erreurs.add("Ligne " + numeroLigne
                            + " : " + e.getMessage());
                    nbErreurs++;
                }
            }

        } catch (IOException e) {
            throw new CsvImportException(
                    "Erreur lecture fichier Aurion : "
                            + e.getMessage());
        }

        construireRapport(rapport, nbLues, nbInsereees,
                nbIgnorees, nbErreurs, erreurs, avertissements);
        return rapport;
    }

    // ===== MAPPING ADAPTATIF =====

    /**
     * Sauvegarde un mapping manuel validé par l'utilisateur.
     *
     * <p>Enrichit le dictionnaire MappingCsv pour éviter
     * de redemander le même mapping à l'avenir.</p>
     *
     * @param dto   le mapping validé par l'utilisateur
     */
    @Transactional
    public void sauvegarderMapping(CsvMappingDTO dto) {
        Optional<MappingCsv> existant = mappingCsvRepository
                .findByColonneSourceAndContexte(
                        dto.getColonneSource(), dto.getContexte());

        if (existant.isPresent()) {
            // Mettre à jour le mapping existant
            MappingCsv mapping = existant.get();
            mapping.setChampInterne(dto.getChampInterne());
            mapping.setNbUtilisations(
                    mapping.getNbUtilisations() + 1);
            mapping.setDerniereUtilisation(
                    java.time.LocalDateTime.now());
            mappingCsvRepository.save(mapping);
        } else {
            // Créer un nouveau mapping
            MappingCsv mapping = new MappingCsv();
            mapping.setColonneSource(dto.getColonneSource());
            mapping.setChampInterne(dto.getChampInterne());
            mapping.setContexte(dto.getContexte());
            mapping.setNbUtilisations(1);
            mapping.setDerniereUtilisation(
                    java.time.LocalDateTime.now());
            mappingCsvRepository.save(mapping);
        }
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Insère une note en base pour un étudiant.
     *
     * @param numeroEtudiant    numéro de l'étudiant
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param enseignantId      identifiant de l'enseignant
     * @param valeur            valeur de la note
     * @param groupeNom         nom du groupe (optionnel)
     * @param typeNote          type de note
     * @return                  true si insérée, false si ignorée
     */
    private boolean insererNote(
            String numeroEtudiant,
            Long matiereId,
            Long anneeAcademiqueId,
            Long enseignantId,
            Double valeur,
            String groupeNom,
            Note.TypeNote typeNote) {

        // Trouver l'étudiant
        Optional<org.harold.plateforme.entity.Etudiant> etudiantOpt =
                etudiantRepository.findByNumeroEtudiant(
                        numeroEtudiant);

        if (etudiantOpt.isEmpty()) return false;

        Long etudiantId = etudiantOpt.get().getId();

        // Vérifier si la note existe déjà
        if (noteRepository
                .existsByEtudiantIdAndMatiereIdAndAnneeAcademiqueIdAndType(
                        etudiantId, matiereId,
                        anneeAcademiqueId, typeNote)) {
            return false;
        }

        // Trouver le groupe si fourni
        Long groupeClasseId = null;
        if (groupeNom != null && !groupeNom.isEmpty()) {
            List<org.harold.plateforme.entity.GroupeClasse> groupes =
                    groupeClasseRepository
                            .findByMatiereIdAndAnneeAcademiqueId(
                                    matiereId, anneeAcademiqueId);
            groupeClasseId = groupes.stream()
                    .filter(g -> g.getNom().contains(
                            groupeNom.substring(
                                    Math.max(0,
                                            groupeNom.length() - 3))))
                    .map(org.harold.plateforme.entity.GroupeClasse::getId)
                    .findFirst()
                    .orElse(null);
        }

        if (groupeClasseId == null && !groupeClasseRepository
                .findByMatiereIdAndAnneeAcademiqueId(
                        matiereId, anneeAcademiqueId).isEmpty()) {
            groupeClasseId = groupeClasseRepository
                    .findByMatiereIdAndAnneeAcademiqueId(
                            matiereId, anneeAcademiqueId)
                    .get(0).getId();
        }

        if (groupeClasseId == null) return false;

        // Créer le DTO de saisie
        org.harold.plateforme.dto.note.NoteCreateDTO noteDTO =
                new org.harold.plateforme.dto.note.NoteCreateDTO();
        noteDTO.setEtudiantId(etudiantId);
        noteDTO.setMatiereId(matiereId);
        noteDTO.setAnneeAcademiqueId(anneeAcademiqueId);
        noteDTO.setGroupeClasseId(groupeClasseId);
        noteDTO.setType(typeNote.name());
        noteDTO.setValeur(valeur);
        noteDTO.setEstRattrapage(false);

        noteService.saisir(noteDTO, enseignantId, "csv-import");
        return true;
    }

    /**
     * Parse une note depuis un String.
     *
     * <p>Gère le séparateur décimal virgule (français)
     * et point (anglais).</p>
     *
     * @param noteStr   la note en String
     * @return          la note en Double ou null si invalide
     */
    private Double parseNote(String noteStr) {
        if (noteStr == null || noteStr.isEmpty()) return null;
        try {
            return Double.parseDouble(
                    noteStr.replace(",", ".").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Lit l'en-tête d'un fichier Excel et retourne
     * un map colonne → index.
     *
     * @param headerRow     la ligne d'en-tête
     * @return              map nom colonne → index
     */
    private Map<String, Integer> lireEnTeteExcel(Row headerRow) {
        Map<String, Integer> colonnes = new HashMap<>();
        if (headerRow == null) return colonnes;
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            if (headerRow.getCell(i) != null) {
                colonnes.put(
                        headerRow.getCell(i).getStringCellValue()
                                .toLowerCase().trim(),
                        i);
            }
        }
        return colonnes;
    }

    /**
     * Trouve l'index d'une colonne en cherchant
     * d'abord dans le dictionnaire MappingCsv.
     *
     * @param colonnes      map colonnes du fichier
     * @param nomColonne    nom de la colonne recherchée
     * @param contexte      contexte du mapping
     * @return              l'index de la colonne ou 0
     */
    private Integer trouverColonne(
            Map<String, Integer> colonnes,
            String nomColonne,
            String contexte) {

        // Chercher dans le dictionnaire
        Optional<MappingCsv> mapping = mappingCsvRepository
                .findByColonneSourceIgnoreCase(nomColonne);

        if (mapping.isPresent()) {
            String champInterne = mapping.get().getChampInterne();
            if (colonnes.containsKey(
                    champInterne.toLowerCase())) {
                return colonnes.get(
                        champInterne.toLowerCase());
            }
        }

        // Chercher directement
        if (colonnes.containsKey(nomColonne.toLowerCase())) {
            return colonnes.get(nomColonne.toLowerCase());
        }

        return 0;
    }

    /**
     * Trouve la colonne de note dans un fichier THEIA.
     *
     * <p>La colonne de note commence par "moyenne".</p>
     *
     * @param colonnes  map colonnes du fichier
     * @return          l'index de la colonne note ou 5
     */
    private Integer trouverColonneNote(
            Map<String, Integer> colonnes) {
        return colonnes.entrySet().stream()
                .filter(e -> e.getKey()
                        .toLowerCase().startsWith("moyenne"))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(5);
    }

    /**
     * Récupère la valeur d'une cellule Excel en String.
     *
     * @param row   la ligne Excel
     * @param index l'index de la cellule
     * @return      la valeur en String ou null
     */
    private String getCellValue(Row row, Integer index) {
        if (index == null || row.getCell(index) == null) {
            return null;
        }
        return switch (row.getCell(index).getCellType()) {
            case STRING -> row.getCell(index)
                    .getStringCellValue().trim();
            case NUMERIC -> String.valueOf(
                    row.getCell(index).getNumericCellValue());
            default -> null;
        };
    }

    /**
     * Récupère un champ optionnel depuis un CSVRecord.
     *
     * @param record    le record CSV
     * @param champ     le nom du champ
     * @return          la valeur ou null si absent
     */
    private String getChampOptional(
            CSVRecord record,
            String champ) {
        try {
            return record.get(champ);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Récupère un champ depuis un CSVRecord en cherchant
     * d'abord dans le dictionnaire MappingCsv.
     *
     * @param record            le record CSV
     * @param nomColonne        nom de la colonne source
     * @param champInterne      champ interne par défaut
     * @param contexte          contexte du mapping
     * @return                  la valeur ou null
     */
    private String getChampMapped(
            CSVRecord record,
            String nomColonne,
            String champInterne,
            String contexte) {

        // Chercher dans le dictionnaire
        Optional<MappingCsv> mapping = mappingCsvRepository
                .findByColonneSourceIgnoreCase(nomColonne);

        String colonne = mapping.isPresent()
                ? mapping.get().getChampInterne()
                : nomColonne;

        try {
            return record.get(colonne);
        } catch (IllegalArgumentException e) {
            try {
                return record.get(nomColonne);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }

    /**
     * Construit le rapport d'import final.
     *
     * @param rapport       le DTO à compléter
     * @param nbLues        nombre de lignes lues
     * @param nbInsereees   nombre de lignes insérées
     * @param nbIgnorees    nombre de lignes ignorées
     * @param nbErreurs     nombre de lignes en erreur
     * @param erreurs       liste des erreurs
     * @param avertissements liste des avertissements
     */
    private void construireRapport(
            CsvImportResultDTO rapport,
            int nbLues,
            int nbInsereees,
            int nbIgnorees,
            int nbErreurs,
            List<String> erreurs,
            List<String> avertissements) {
        rapport.setNbLignesLues(nbLues);
        rapport.setNbLignesInsereees(nbInsereees);
        rapport.setNbLignesIgnorees(nbIgnorees);
        rapport.setNbLignesEnErreur(nbErreurs);
        rapport.setSucces(nbErreurs == 0);
        rapport.setErreurs(erreurs);
        rapport.setAvertissements(avertissements);
        rapport.setColonnesNonReconnues(new ArrayList<>());
    }
}