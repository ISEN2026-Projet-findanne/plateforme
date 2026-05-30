package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type_evaluation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypeEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEval type;

    @Column(nullable = false)
    private Double coefficient;

    private String libelle;

    public enum TypeEval {
        CC,
        EXAMEN_FINAL,
        TP,
        RATTRAPAGE
    }
}