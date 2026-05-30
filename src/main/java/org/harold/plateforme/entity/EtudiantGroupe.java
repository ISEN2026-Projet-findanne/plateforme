package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "etudiant_groupe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantGroupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_classe_id", nullable = false)
    private GroupeClasse groupeClasse;
}