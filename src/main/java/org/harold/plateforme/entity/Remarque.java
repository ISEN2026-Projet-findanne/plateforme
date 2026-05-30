package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "remarque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Remarque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_classe_id", nullable = false)
    private GroupeClasse groupeClasse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enseignant_id", nullable = false)
    private Utilisateur enseignant;

    @Column(nullable = false, length = 1000)
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeRemarque type;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum TypeRemarque {
        POSITIVE,
        NEGATIVE,
        NEUTRE
    }
}