package com.estrela.cbms.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Servicos {
    private String agua;

    private String esgoto;

    private String lixo;

    private String eletricidade;
}
