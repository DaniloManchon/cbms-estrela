package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@Document(collection = "beneficiarios")
public class Beneficiario {

    @JsonIgnore
    @Id
    private String id;

    @NotBlank(message = "O nome completo é obrigatório")
    @JsonProperty("nome_completo")
    private String nomeCompleto;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "O CPF deve seguir o padrão 000.000.000-00")
    @Indexed(unique = true)
    private String cpf;

    private String celular;

    @JsonProperty("data_nascimento")
    private String dataNascimento;

    @Indexed(unique = true)
    @JsonProperty("codigo_barras")
    private String codigoBarras;

    private String foto;

    @JsonProperty("identificacao_familiar")
    private List<IdentificacaoFamiliar> identificacaoFamiliar;

    private Renda renda;

    private Moradia moradia;

    @JsonProperty("educacao_bens")
    private EducacaoBens educacaoBens;

    private String obs;

    private List<Coleta> coletas;

    @JsonProperty("ativo")
    private Boolean ativo = true;

    @JsonProperty("motivo_inativacao")
    private String motivoInativacao;

    private List<Doacoes> doacoes;
}
