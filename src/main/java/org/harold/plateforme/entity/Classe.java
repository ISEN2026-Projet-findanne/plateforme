package org.harold.plateforme.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String niveau;

    private String description;
}