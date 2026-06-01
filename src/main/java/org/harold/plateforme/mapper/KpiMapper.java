package org.harold.plateforme.mapper;

import org.harold.plateforme.dto.kpi.KpiAnnuelDTO;
import org.harold.plateforme.dto.kpi.KpiMatiereDTO;
import org.harold.plateforme.dto.kpi.KpiSemestreDTO;
import org.harold.plateforme.util.CalculKpiUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper pour la construction des DTOs KPI.
 *
 * <p>Utilise CalculKpiUtils pour calculer les statistiques
 * depuis les listes de notes finales fournies par le KpiService.</p>
 *
 * <p>Pas de ModelMapper ici car les KPI ne sont pas une conversion
 * directe d'entité vers DTO mais un calcul statistique.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Component
public class KpiMapper {

    /**
     * Construit un KpiMatiereDTO depuis les listes de notes
     * avant et après rattrapage.
     *
     * <p>Les notes avant rattrapage servent uniquement
     * au calcul du taux de rattrapage.
     * Toutes les autres statistiques utilisent les notes
     * après rattrapage.</p>
     *
     * @param notesApresRattrapage  notes finales après rattrapage
     * @param notesAvantRattrapage  notes finales avant rattrapage
     * @param matiereId             identifiant de la matière
     * @param matiereNom            nom de la matière
     * @param matiereCode           code de la matière
     * @param anneeAcademiqueId     identifiant de l'année académique
     * @param anneeAcademique       libellé de l'année académique
     * @param promotionNom          nom de la promotion
     * @param groupeNom             nom du groupe (null si niveau promotion)
     * @return                      le KpiMatiereDTO calculé
     */
    public KpiMatiereDTO toKpiMatiereDTO(
            List<Double> notesApresRattrapage,
            List<Double> notesAvantRattrapage,
            Long matiereId,
            String matiereNom,
            String matiereCode,
            Long anneeAcademiqueId,
            String anneeAcademique,
            String promotionNom,
            String groupeNom) {

        KpiMatiereDTO dto = new KpiMatiereDTO();

        // Informations matière
        dto.setMatiereId(matiereId);
        dto.setMatiereNom(matiereNom);
        dto.setMatiereCode(matiereCode);
        dto.setAnneeAcademiqueId(anneeAcademiqueId);
        dto.setAnneeAcademique(anneeAcademique);
        dto.setPromotionNom(promotionNom);
        dto.setGroupeNom(groupeNom);
        dto.setNbEtudiants(notesApresRattrapage != null
                ? notesApresRattrapage.size() : 0);

        // 8 KPI calculés depuis les listes de notes
        dto.setMoyenne(CalculKpiUtils.calculerMoyenne(notesApresRattrapage));
        dto.setMinimum(CalculKpiUtils.calculerMinimum(notesApresRattrapage));
        dto.setMaximum(CalculKpiUtils.calculerMaximum(notesApresRattrapage));
        dto.setEcartType(CalculKpiUtils.calculerEcartType(notesApresRattrapage));
        dto.setMediane(CalculKpiUtils.calculerMediane(notesApresRattrapage));
        dto.setTauxReussite(CalculKpiUtils.calculerTauxReussite(notesApresRattrapage));
        dto.setTauxEchec(CalculKpiUtils.calculerTauxEchec(notesApresRattrapage));
        dto.setTauxRattrapage(CalculKpiUtils.calculerTauxRattrapage(notesAvantRattrapage));

        return dto;
    }

    /**
     * Construit un KpiSemestreDTO depuis les moyennes semestrielles
     * des étudiants après rattrapage.
     *
     * <p>Pas de taux de rattrapage au niveau semestre —
     * ce KPI s'applique uniquement au niveau matière.</p>
     *
     * @param moyennesSemestre  moyennes semestrielles après rattrapage
     * @param numeroSemestre    numéro du semestre (1 ou 2)
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param anneeAcademique   libellé de l'année académique
     * @param promotionNom      nom de la promotion
     * @param groupeNom         nom du groupe (null si niveau promotion)
     * @return                  le KpiSemestreDTO calculé
     */
    public KpiSemestreDTO toKpiSemestreDTO(
            List<Double> moyennesSemestre,
            Integer numeroSemestre,
            Long anneeAcademiqueId,
            String anneeAcademique,
            String promotionNom,
            String groupeNom) {

        KpiSemestreDTO dto = new KpiSemestreDTO();

        // Informations semestre
        dto.setNumeroSemestre(numeroSemestre);
        dto.setAnneeAcademiqueId(anneeAcademiqueId);
        dto.setAnneeAcademique(anneeAcademique);
        dto.setPromotionNom(promotionNom);
        dto.setGroupeNom(groupeNom);
        dto.setNbEtudiants(moyennesSemestre != null
                ? moyennesSemestre.size() : 0);

        // 7 KPI — sans taux de rattrapage
        dto.setMoyenne(CalculKpiUtils.calculerMoyenne(moyennesSemestre));
        dto.setMinimum(CalculKpiUtils.calculerMinimum(moyennesSemestre));
        dto.setMaximum(CalculKpiUtils.calculerMaximum(moyennesSemestre));
        dto.setEcartType(CalculKpiUtils.calculerEcartType(moyennesSemestre));
        dto.setMediane(CalculKpiUtils.calculerMediane(moyennesSemestre));
        dto.setTauxReussite(CalculKpiUtils.calculerTauxReussite(moyennesSemestre));
        dto.setTauxEchec(CalculKpiUtils.calculerTauxEchec(moyennesSemestre));

        return dto;
    }

    /**
     * Construit un KpiAnnuelDTO depuis les moyennes annuelles
     * des étudiants après rattrapage.
     *
     * <p>Pas de taux de rattrapage au niveau annuel —
     * ce KPI s'applique uniquement au niveau matière.</p>
     *
     * @param moyennesAnnuelles liste des moyennes annuelles après rattrapage
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param anneeAcademique   libellé de l'année académique
     * @param promotionNom      nom de la promotion
     * @param groupeNom         nom du groupe (null si niveau promotion)
     * @return                  le KpiAnnuelDTO calculé
     */
    public KpiAnnuelDTO toKpiAnnuelDTO(
            List<Double> moyennesAnnuelles,
            Long anneeAcademiqueId,
            String anneeAcademique,
            String promotionNom,
            String groupeNom) {

        KpiAnnuelDTO dto = new KpiAnnuelDTO();

        // Informations année
        dto.setAnneeAcademiqueId(anneeAcademiqueId);
        dto.setAnneeAcademique(anneeAcademique);
        dto.setPromotionNom(promotionNom);
        dto.setGroupeNom(groupeNom);
        dto.setNbEtudiants(moyennesAnnuelles != null
                ? moyennesAnnuelles.size() : 0);

        // 7 KPI — sans taux de rattrapage
        dto.setMoyenne(CalculKpiUtils.calculerMoyenne(moyennesAnnuelles));
        dto.setMinimum(CalculKpiUtils.calculerMinimum(moyennesAnnuelles));
        dto.setMaximum(CalculKpiUtils.calculerMaximum(moyennesAnnuelles));
        dto.setEcartType(CalculKpiUtils.calculerEcartType(moyennesAnnuelles));
        dto.setMediane(CalculKpiUtils.calculerMediane(moyennesAnnuelles));
        dto.setTauxReussite(CalculKpiUtils.calculerTauxReussite(moyennesAnnuelles));
        dto.setTauxEchec(CalculKpiUtils.calculerTauxEchec(moyennesAnnuelles));

        return dto;
    }
}