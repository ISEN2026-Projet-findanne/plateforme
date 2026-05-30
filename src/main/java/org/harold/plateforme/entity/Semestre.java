package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;
import org.harold.plateforme.entity.Classe;

import java.time.LocalDate;

@Entity
@Table(name = "semestre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private Double coefficient = 0.5;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;
}