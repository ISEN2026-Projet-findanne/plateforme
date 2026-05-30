package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enseignant_matiere_groupe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnseignantMatiereGroupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_classe_id", nullable = false)
    private GroupeClasse groupeClasse;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_enseignement", nullable = false)
    private TypeEnseignement typeEnseignement;

    public enum TypeEnseignement {
        CM,
        TD,
        TP
    }
}