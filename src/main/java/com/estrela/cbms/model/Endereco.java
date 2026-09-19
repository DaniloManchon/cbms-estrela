package com.estrela.cbms.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String localidade;

    private String uf;
}
