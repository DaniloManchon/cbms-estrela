package com.estrela.cbms.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Endereco {
    private String cep;

    private String logradouro;

    private String numero;

    private String bairro;

    private String localidade;

    private String uf;
}
