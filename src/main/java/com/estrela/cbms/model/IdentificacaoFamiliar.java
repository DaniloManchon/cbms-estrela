package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
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
