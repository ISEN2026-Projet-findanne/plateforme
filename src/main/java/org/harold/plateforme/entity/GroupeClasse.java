package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groupe_classe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupeClasse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_academique_id", nullable = false)
    private AnneeAcademique anneeAcademique;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeGroupe type;

    private Integer capacite;

    public enum TypeGroupe {
        TD,
        TP,
        CM
    }
}