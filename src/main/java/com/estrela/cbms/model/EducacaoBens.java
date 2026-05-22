package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Data;

@Data
@Embeddable
public class EducacaoBens {
    @JsonProperty("escolaridade_responsavel")
    private String escolaridadeResponsavel;

    @JsonProperty("qtd_estudantes")
    private String qtdEstudantes;

    @Embedded
    private Bens bens;
}
