package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class EducacaoBens {
    @JsonProperty("escolaridade_beneficiario")
    private String escolaridadeBeneficiario;

    @Min(value = 0, message = "A quantidade de estudantes não pode ser negativa")
    @JsonProperty("qtd_estudantes")
    private Integer qtdEstudantes;

    @Embedded
    private Bens bens;
}
