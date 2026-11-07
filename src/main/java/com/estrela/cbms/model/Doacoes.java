package com.estrela.cbms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doacoes {

    private Long id;
    private LocalDate dataDoacao;
    private String descricaoDoacao;

    public Doacoes(LocalDate dataDoacao, String descricaoDoacao) {
        this.dataDoacao = dataDoacao;
        this.descricaoDoacao = descricaoDoacao;
    }

}
