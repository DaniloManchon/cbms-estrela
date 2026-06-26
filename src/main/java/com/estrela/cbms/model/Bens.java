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
public class Bens {
    private boolean geladeira;

    @JsonProperty("maquina_lavar")
    private boolean maquinaLavar;

    private boolean computador;

    private boolean carro;

    private boolean motocicleta;
}
