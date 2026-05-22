package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Coleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataColeta;

    private String observacao;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    @JsonIgnore // Evita loop infinito no JSON
    private Responsavel responsavel;

    public Coleta(LocalDateTime dataColeta, Responsavel responsavel) {
        this.dataColeta = dataColeta;
        this.responsavel = responsavel;
    }
}
