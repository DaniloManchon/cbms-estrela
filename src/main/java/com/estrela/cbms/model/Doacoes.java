package com.estrela.cbms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doacoes {

    private LocalDateTime dataDoacao;
    private String descricaoDoacao;

}
