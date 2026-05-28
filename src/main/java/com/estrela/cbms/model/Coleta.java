package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coleta coleta = (Coleta) o;
        return Objects.equals(id, coleta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
