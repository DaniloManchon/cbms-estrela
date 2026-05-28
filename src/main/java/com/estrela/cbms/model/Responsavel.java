package com.estrela.cbms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Responsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome completo é obrigatório")
    @JsonProperty("nome_completo")
    private String nomeCompleto;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "O CPF deve seguir o padrão 000.000.000-00")
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
    @AttributeOverrides({
        @AttributeOverride(name = "fontesRenda.tipo", column = @Column(name = "renda_fonte_tipo")),
        @AttributeOverride(name = "fontesRenda.outros", column = @Column(name = "renda_fonte_outros"))
    })
    private Renda renda;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "tipo", column = @Column(name = "moradia_tipo"))
    })
    private Moradia moradia;

    @Embedded
    @JsonProperty("educacao_bens")
    private EducacaoBens educacaoBens;

    private String obs;

    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coleta> coletas;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Responsavel that = (Responsavel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
