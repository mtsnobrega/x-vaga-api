package com.riobranco.xsquad.vaga_api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vagas")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nomeVaga; // ex: "A10"

    @Column(nullable = false)
    private BigDecimal precoHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVaga statusVaga;

    private LocalDateTime createdAt;

    @PrePersist
    void aoCriar() {
        this.createdAt = LocalDateTime.now();
        if (this.statusVaga == null) {
            this.statusVaga = StatusVaga.DISPONIVEL;
        }
    }

    // Getters e Setters
    public Long getId() { return id;}
    public void setId(Long id) { this.id = id; }

    public String getNomeVaga() { return nomeVaga; }
    public void setNomeVaga(String nomeVaga) { this.nomeVaga = nomeVaga; }

    public BigDecimal getPrecoHora() { return precoHora; }
    public void setPrecoHora(BigDecimal precoHora) { this.precoHora = precoHora; }

    public StatusVaga getStatus() { return statusVaga; }
    public void setStatus(StatusVaga status) { this.statusVaga = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
