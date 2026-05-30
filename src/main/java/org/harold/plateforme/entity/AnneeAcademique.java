package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "annee_academique")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnneeAcademique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String annee;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private boolean active = false;
}