package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mapping_csv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MappingCsv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "colonne_source", nullable = false)
    private String colonneSource;

    @Column(name = "champ_interne", nullable = false)
    private String champInterne;

    private String contexte;

    @Column(name = "nb_utilisations", nullable = false)
    private Integer nbUtilisations = 0;

    @Column(name = "derniere_utilisation")
    private LocalDateTime derniereUtilisation;
}