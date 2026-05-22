package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Bens {
    private String geladeira;

    @JsonProperty("maquina_lavar")
    private String maquinaLavar;

    private String computador;

    private String carro;

    private String motocicleta;
}
