package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "matiere")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Matiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_matieres_id", nullable = false)
    private GroupeMatieres groupeMatieres;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Double coefficient;

    @Column(name = "a_tp", nullable = false)
    private boolean aTp = false;

    @Column(name = "a_cm", nullable = false)
    private boolean aCm = false;
}