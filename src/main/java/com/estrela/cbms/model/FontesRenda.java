package com.estrela.cbms.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class FontesRenda {
    private String formal;

    private String informal;

    private String pensao;

    private String bpc;

    private String outros;
}
