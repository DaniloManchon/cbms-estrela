package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Data;

@Data
@Embeddable
public class Moradia {
    @Embedded
    private Endereco endereco;

    private String tipo;

    @JsonProperty("numero_comodos")
    private String numeroComodos;

    private String material;

    @Embedded
    private Servicos servicos;
}
