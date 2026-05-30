package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "responsable_promotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponsablePromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_academique_id", nullable = false)
    private AnneeAcademique anneeAcademique;

    @Column(name = "date_prise_en_charge")
    private LocalDate datePriseEnCharge;

    @PrePersist
    protected void onCreate() {
        this.datePriseEnCharge = LocalDate.now();
    }
}