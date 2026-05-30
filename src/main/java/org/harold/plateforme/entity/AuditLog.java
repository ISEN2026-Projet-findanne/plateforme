package org.harold.plateforme.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private String entite;

    @Column(name = "entite_id", nullable = false)
    private Long entiteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAction action;

    @Column(name = "avant_json", columnDefinition = "TEXT")
    private String avantJson;

    @Column(name = "apres_json", columnDefinition = "TEXT")
    private String apresJson;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum TypeAction {
        CREATE,
        UPDATE,
        DELETE
    }
}