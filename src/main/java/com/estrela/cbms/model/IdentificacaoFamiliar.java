package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class IdentificacaoFamiliar {
    @JsonProperty("nome_completo")
    private String nomeCompleto;

    private String parentesco;

    @JsonProperty("data_nascimento")
    private String dataNascimento;

    @JsonProperty("ocupacao_escola")
    private String ocupacaoEscola;
}
