package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Moradia {
    private Endereco endereco;

    private String tipo;

    @Min(value = 0, message = "O número de cômodos não pode ser negativo")
    @JsonProperty("numero_comodos")
    private Integer numeroComodos;

    private String material;

    private Servicos servicos;
}
