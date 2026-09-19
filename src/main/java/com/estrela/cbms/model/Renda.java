package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Renda {
    @JsonProperty("renda_bruta")
    private String rendaBruta;

    @JsonProperty("fontes_renda")
    private FontesRenda fontesRenda;
}
