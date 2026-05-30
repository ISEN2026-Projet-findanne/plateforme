package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groupe_matieres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupeMatieres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private Double coefficient;
}