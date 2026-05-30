package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "promotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String filiere;

    @Column(name = "annee_debut")
    private Integer anneeDebut;

    private String description;
}

