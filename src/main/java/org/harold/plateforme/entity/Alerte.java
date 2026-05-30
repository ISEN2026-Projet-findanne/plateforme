package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_academique_id", nullable = false)
    private AnneeAcademique anneeAcademique;

    @Column(name = "score_risque", nullable = false)
    private Double scoreRisque;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauAlerte niveau;

    @Column(nullable = false)
    private boolean lue = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum NiveauAlerte {
        FAIBLE,
        MODERE,
        ELEVE,
        CRITIQUE
    }
}