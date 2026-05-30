package org.harold.plateforme.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "etudiant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(name = "numero_etudiant", nullable = false, unique = true)
    private String numeroEtudiant;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(unique = true)
    private String email;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}