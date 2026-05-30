package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "score_risque_historique")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRisqueHistorique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_academique_id", nullable = false)
    private AnneeAcademique anneeAcademique;

    @Column(name = "score_global", nullable = false)
    private Double scoreGlobal;

    @Column(name = "score_moyenne", nullable = false)
    private Double scoreMoyenne;

    @Column(name = "score_matieres", nullable = false)
    private Double scoreMatieres;

    @Column(name = "score_tendance", nullable = false)
    private Double scoreTendance;

    @Column(name = "score_remarques", nullable = false)
    private Double scoreRemarques;

    @Column(name = "calcule_le", updatable = false)
    private LocalDateTime calculeLe;

    @PrePersist
    protected void onCreate() {
        this.calculeLe = LocalDateTime.now();
    }
}