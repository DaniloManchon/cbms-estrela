package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Renda {
    @JsonProperty("renda_bruta")
    private String rendaBruta;

    @Embedded
    @JsonProperty("fontes_renda")
    private FontesRenda fontesRenda;
}
