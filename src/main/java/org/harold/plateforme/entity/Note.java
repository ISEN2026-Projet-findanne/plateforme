package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "note")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_academique_id", nullable = false)
    private AnneeAcademique anneeAcademique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_classe_id", nullable = false)
    private GroupeClasse groupeClasse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saisi_par", nullable = false)
    private Utilisateur saisiPar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNote type;

    @Column(nullable = false)
    private Double valeur;

    @Column(name = "est_rattrapage", nullable = false)
    private boolean estRattrapage = false;

    @Column(name = "saisie_le", updatable = false)
    private LocalDateTime saisieLe;

    @PrePersist
    protected void onCreate() {
        this.saisieLe = LocalDateTime.now();
    }

    public enum TypeNote {
        CC,
        EXAMEN_FINAL,
        TP,
        RATTRAPAGE
    }
}