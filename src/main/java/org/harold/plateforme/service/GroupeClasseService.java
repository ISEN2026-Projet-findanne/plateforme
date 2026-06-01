package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.groupe.AssignationEnseignantDTO;
import org.harold.plateforme.dto.groupe.AssignationEtudiantDTO;
import org.harold.plateforme.dto.groupe.GroupeClasseCreateDTO;
import org.harold.plateforme.dto.groupe.GroupeClasseDTO;
import org.harold.plateforme.dto.groupe.GroupeClasseUpdateDTO;
import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.entity.EnseignantMatiereGroupe;
import org.harold.plateforme.entity.Etudiant;
import org.harold.plateforme.entity.EtudiantGroupe;
import org.harold.plateforme.entity.GroupeClasse;
import org.harold.plateforme.entity.Matiere;
import org.harold.plateforme.entity.Utilisateur;
import org.harold.plateforme.exception.DuplicateResourceException;
import org.harold.plateforme.exception.ResourceNotFoundException;
import org.harold.plateforme.exception.ValidationException;
import org.harold.plateforme.mapper.GroupeClasseMapper;
import org.harold.plateforme.repository.EnseignantMatiereGroupeRepository;
import org.harold.plateforme.repository.EtudiantGroupeRepository;
import org.harold.plateforme.repository.EtudiantRepository;
import org.harold.plateforme.repository.GroupeClasseRepository;
import org.harold.plateforme.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des groupes TD/TP/CM.
 *
 * <p>Gère la création et modification des groupes,
 * l'assignation des étudiants et des enseignants.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class GroupeClasseService {

    private final GroupeClasseRepository groupeClasseRepository;
    private final EtudiantGroupeRepository etudiantGroupeRepository;
    private final EnseignantMatiereGroupeRepository enseignantMatiereGroupeRepository;
    private final EtudiantRepository etudiantRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final GroupeClasseMapper groupeClasseMapper;
    private final MatiereService matiereService;
    private final AnneeAcademiqueService anneeAcademiqueService;
    private final AuditService auditService;

    // ===== CRÉATION GROUPE =====

    /**
     * Crée un nouveau groupe TD/TP/CM.
     *
     * <p>Vérifie l'unicité du nom pour cette matière
     * et cette année académique.</p>
     *
     * @param dto               les données de création
     * @param utilisateurId     l'admin qui crée le groupe
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO du groupe créé
     * @throws ResourceNotFoundException    si matière ou année inexistante
     * @throws DuplicateResourceException   si le nom existe déjà
     * @throws ValidationException          si le type est invalide
     */
    @Transactional
    public GroupeClasseDTO creer(
            GroupeClasseCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Vérifier unicité du nom
        if (groupeClasseRepository
                .existsByNomAndMatiereIdAndAnneeAcademiqueId(
                        dto.getNom(),
                        dto.getMatiereId(),
                        dto.getAnneeAcademiqueId())) {
            throw new DuplicateResourceException(
                    "GroupeClasse", "nom", dto.getNom());
        }

        // 2. Valider le type
        GroupeClasse.TypeGroupe typeGroupe;
        try {
            typeGroupe = GroupeClasse.TypeGroupe.valueOf(dto.getType());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "type",
                    "Type invalide. Valeurs acceptées : TD, TP, CM");
        }

        // 3. Récupérer les entités liées
        Matiere matiere = matiereService.getEntityById(
                dto.getMatiereId());

        // 4. Créer l'entité
        GroupeClasse groupe = new GroupeClasse();
        groupe.setNom(dto.getNom());
        groupe.setType(typeGroupe);
        groupe.setCapacite(dto.getCapacite());
        groupe.setMatiere(matiere);
        groupe.setAnneeAcademique(
                anneeAcademiqueService.getEntityById(
                        dto.getAnneeAcademiqueId()));

        // 5. Sauvegarder
        GroupeClasse sauvegarde = groupeClasseRepository.save(groupe);

        // 6. Calculer nbEtudiants (0 à la création)
        GroupeClasseDTO groupeDTO =
                groupeClasseMapper.toDTO(sauvegarde, 0);

        // 7. Audit
        auditService.enregistrer(
                utilisateurId,
                "GroupeClasse",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                groupeDTO,
                ipAddress);

        return groupeDTO;
    }

    // ===== MODIFICATION GROUPE =====

    /**
     * Modifie un groupe existant.
     *
     * <p>Seuls le nom et la capacité sont modifiables.
     * La matière et l'année académique ne sont pas modifiables.</p>
     *
     * @param id                identifiant du groupe
     * @param dto               les nouvelles données
     * @param utilisateurId     l'admin qui effectue la modification
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO du groupe modifié
     * @throws ResourceNotFoundException    si le groupe n'existe pas
     * @throws DuplicateResourceException   si le nouveau nom existe déjà
     */
    @Transactional
    public GroupeClasseDTO modifier(
            Long id,
            GroupeClasseUpdateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer le groupe existant
        GroupeClasse groupe = getEntityById(id);

        // 2. Vérifier unicité du nom si changé
        if (!groupe.getNom().equals(dto.getNom()) &&
                groupeClasseRepository
                        .existsByNomAndMatiereIdAndAnneeAcademiqueId(
                                dto.getNom(),
                                groupe.getMatiere().getId(),
                                groupe.getAnneeAcademique().getId())) {
            throw new DuplicateResourceException(
                    "GroupeClasse", "nom", dto.getNom());
        }

        // 3. Calculer nbEtudiants pour l'audit
        int nbEtudiants = getNbEtudiants(id);
        GroupeClasseDTO avant = groupeClasseMapper.toDTO(
                groupe, nbEtudiants);

        // 4. Mettre à jour
        groupe.setNom(dto.getNom());
        groupe.setCapacite(dto.getCapacite());
        GroupeClasse sauvegarde = groupeClasseRepository.save(groupe);

        // 5. Audit
        GroupeClasseDTO apres = groupeClasseMapper.toDTO(
                sauvegarde, nbEtudiants);
        auditService.enregistrer(
                utilisateurId,
                "GroupeClasse",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                apres,
                ipAddress);

        return apres;
    }

    // ===== ASSIGNATION ETUDIANT =====

    /**
     * Assigne un étudiant à un groupe.
     *
     * <p>Vérifie que l'étudiant n'est pas déjà dans
     * ce groupe et que la capacité n'est pas dépassée.</p>
     *
     * @param dto               les données d'assignation
     * @param utilisateurId     l'admin qui effectue l'assignation
     * @param ipAddress         adresse IP de l'admin
     * @throws ResourceNotFoundException    si étudiant ou groupe inexistant
     * @throws DuplicateResourceException   si déjà assigné
     * @throws ValidationException          si capacité dépassée
     */
    @Transactional
    public void assignerEtudiant(
            AssignationEtudiantDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Vérifier que l'étudiant n'est pas déjà dans ce groupe
        if (etudiantGroupeRepository
                .existsByEtudiantIdAndGroupeClasseId(
                        dto.getEtudiantId(),
                        dto.getGroupeClasseId())) {
            throw new DuplicateResourceException(
                    "EtudiantGroupe",
                    "etudiantId+groupeClasseId",
                    dto.getEtudiantId() + "+"
                            + dto.getGroupeClasseId());
        }

        // 2. Récupérer les entités
        GroupeClasse groupe = getEntityById(dto.getGroupeClasseId());
        Etudiant etudiant = etudiantRepository
                .findById(dto.getEtudiantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Etudiant", "id", dto.getEtudiantId()));

        // 3. Vérifier la capacité si définie
        if (groupe.getCapacite() != null) {
            int nbEtudiants = getNbEtudiants(groupe.getId());
            if (nbEtudiants >= groupe.getCapacite()) {
                throw new ValidationException(
                        "Le groupe " + groupe.getNom()
                                + " a atteint sa capacité maximale de "
                                + groupe.getCapacite() + " étudiants");
            }
        }

        // 4. Créer l'assignation
        EtudiantGroupe etudiantGroupe = new EtudiantGroupe();
        etudiantGroupe.setEtudiant(etudiant);
        etudiantGroupe.setGroupeClasse(groupe);
        etudiantGroupeRepository.save(etudiantGroupe);

        // 5. Audit
        auditService.enregistrer(
                utilisateurId,
                "EtudiantGroupe",
                etudiantGroupe.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                dto,
                ipAddress);
    }

    /**
     * Retire un étudiant d'un groupe.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param groupeClasseId    identifiant du groupe
     * @param utilisateurId     l'admin qui effectue le retrait
     * @param ipAddress         adresse IP de l'admin
     * @throws ResourceNotFoundException    si l'assignation n'existe pas
     */
    @Transactional
    public void retirerEtudiant(
            Long etudiantId,
            Long groupeClasseId,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer l'assignation
        EtudiantGroupe etudiantGroupe = etudiantGroupeRepository
                .findByEtudiantIdAndGroupeClasseId(
                        etudiantId, groupeClasseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EtudiantGroupe",
                        "etudiantId+groupeClasseId",
                        etudiantId + "+" + groupeClasseId));

        // 2. Audit avant suppression
        auditService.enregistrer(
                utilisateurId,
                "EtudiantGroupe",
                etudiantGroupe.getId(),
                AuditLog.TypeAction.DELETE,
                etudiantGroupe,
                null,
                ipAddress);

        // 3. Supprimer
        etudiantGroupeRepository.delete(etudiantGroupe);
    }

    // ===== ASSIGNATION ENSEIGNANT =====

    /**
     * Assigne un enseignant à un groupe pour une matière.
     *
     * <p>Vérifie que l'assignation n'existe pas déjà.</p>
     *
     * @param dto               les données d'assignation
     * @param utilisateurId     l'admin qui effectue l'assignation
     * @param ipAddress         adresse IP de l'admin
     * @throws DuplicateResourceException   si déjà assigné
     * @throws ValidationException          si type enseignement invalide
     */
    @Transactional
    public void assignerEnseignant(
            AssignationEnseignantDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Vérifier que l'assignation n'existe pas déjà
        if (enseignantMatiereGroupeRepository
                .existsByUtilisateurIdAndMatiereIdAndGroupeClasseId(
                        dto.getEnseignantId(),
                        dto.getMatiereId(),
                        dto.getGroupeClasseId())) {
            throw new DuplicateResourceException(
                    "EnseignantMatiereGroupe",
                    "enseignantId+matiereId+groupeClasseId",
                    dto.getEnseignantId() + "+"
                            + dto.getMatiereId() + "+"
                            + dto.getGroupeClasseId());
        }

        // 2. Valider le type d'enseignement
        EnseignantMatiereGroupe.TypeEnseignement typeEnseignement;
        try {
            typeEnseignement = EnseignantMatiereGroupe
                    .TypeEnseignement.valueOf(dto.getTypeEnseignement());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "typeEnseignement",
                    "Type invalide. Valeurs acceptées : CM, TD, TP");
        }

        // 3. Récupérer les entités
        Utilisateur enseignant = utilisateurRepository
                .findById(dto.getEnseignantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur", "id", dto.getEnseignantId()));
        Matiere matiere = matiereService.getEntityById(
                dto.getMatiereId());
        GroupeClasse groupe = getEntityById(dto.getGroupeClasseId());

        // 4. Créer l'assignation
        EnseignantMatiereGroupe assignation =
                new EnseignantMatiereGroupe();
        assignation.setUtilisateur(enseignant);
        assignation.setMatiere(matiere);
        assignation.setGroupeClasse(groupe);
        assignation.setTypeEnseignement(typeEnseignement);
        enseignantMatiereGroupeRepository.save(assignation);

        // 5. Audit
        auditService.enregistrer(
                utilisateurId,
                "EnseignantMatiereGroupe",
                assignation.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                dto,
                ipAddress);
    }

    // ===== CONSULTATION =====

    /**
     * Récupère un groupe par son identifiant.
     *
     * <p>Retourne l'entité pour les autres services.</p>
     *
     * @param id    identifiant du groupe
     * @return      l'entité GroupeClasse
     * @throws ResourceNotFoundException    si le groupe n'existe pas
     */
    @Transactional(readOnly = true)
    public GroupeClasse getEntityById(Long id) {
        return groupeClasseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GroupeClasse", "id", id));
    }

    /**
     * Récupère un groupe par son identifiant en DTO.
     *
     * @param id    identifiant du groupe
     * @return      le DTO du groupe
     */
    @Transactional(readOnly = true)
    public GroupeClasseDTO getById(Long id) {
        GroupeClasse groupe = getEntityById(id);
        return groupeClasseMapper.toDTO(
                groupe, getNbEtudiants(id));
    }

    /**
     * Récupère tous les groupes d'une matière
     * pour une année académique.
     *
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  liste des groupes
     */
    @Transactional(readOnly = true)
    public List<GroupeClasseDTO> getByMatiereAndAnnee(
            Long matiereId,
            Long anneeAcademiqueId) {
        return groupeClasseRepository
                .findByMatiereIdAndAnneeAcademiqueId(
                        matiereId, anneeAcademiqueId)
                .stream()
                .map(g -> groupeClasseMapper.toDTO(
                        g, getNbEtudiants(g.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les groupes d'un étudiant
     * pour une année académique.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  liste des groupes
     */
    @Transactional(readOnly = true)
    public List<GroupeClasseDTO> getByEtudiantAndAnnee(
            Long etudiantId,
            Long anneeAcademiqueId) {
        return etudiantGroupeRepository
                .findByEtudiantIdAndAnneeAcademiqueId(
                        etudiantId, anneeAcademiqueId)
                .stream()
                .map(eg -> groupeClasseMapper.toDTO(
                        eg.getGroupeClasse(),
                        getNbEtudiants(eg.getGroupeClasse().getId())))
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les groupes d'un enseignant
     * pour une année académique.
     *
     * @param enseignantId      identifiant de l'enseignant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  liste des groupes
     */
    @Transactional(readOnly = true)
    public List<GroupeClasseDTO> getByEnseignantAndAnnee(
            Long enseignantId,
            Long anneeAcademiqueId) {
        return enseignantMatiereGroupeRepository
                .findByEnseignantIdAndAnneeAcademiqueId(
                        enseignantId, anneeAcademiqueId)
                .stream()
                .map(emg -> groupeClasseMapper.toDTO(
                        emg.getGroupeClasse(),
                        getNbEtudiants(
                                emg.getGroupeClasse().getId())))
                .collect(Collectors.toList());
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Calcule le nombre d'étudiants dans un groupe.
     *
     * @param groupeClasseId    identifiant du groupe
     * @return                  nombre d'étudiants
     */
    private int getNbEtudiants(Long groupeClasseId) {
        return etudiantGroupeRepository
                .findByGroupeClasseId(groupeClasseId)
                .size();
    }
}