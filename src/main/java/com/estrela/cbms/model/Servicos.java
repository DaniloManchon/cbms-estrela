package com.estrela.cbms.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Servicos {
    private String agua;

    private String esgoto;

    private String lixo;

    private String eletricidade;
}
