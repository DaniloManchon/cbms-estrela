package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Responsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("nome_completo")
    private String nomeCompleto;

    @Column(unique = true)
    private String cpf;

    private String celular;

    @JsonProperty("data_nascimento")
    private String dataNascimento;

    @ElementCollection
    @CollectionTable(name = "identificacao_familiar", joinColumns = @JoinColumn(name = "responsavel_id"))
    @JsonProperty("identificacao_familiar")
    private List<IdentificacaoFamiliar> identificacaoFamiliar;

    @Embedded
    private Renda renda;

    @Embedded
    private Moradia moradia;

    @Embedded
    @JsonProperty("educacao_bens")
    private EducacaoBens educacaoBens;

    private String obs;

    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coleta> coletas;
}

