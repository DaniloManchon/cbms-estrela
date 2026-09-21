package com.estrela.cbms.service;

import com.estrela.cbms.model.*;
import com.estrela.cbms.repository.BeneficiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BeneficiarioService {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    public Beneficiario criarBeneficiario(Beneficiario beneficiario) {
        Optional<Beneficiario> existente = beneficiarioRepository.findByCpf(beneficiario.getCpf());

        if (existente.isPresent()) {
            throw new RuntimeException("Já existe um beneficiário cadastrado com este CPF.");
        }

        beneficiario.setId(null);
        beneficiario.setCodigoBarras("EST" + System.currentTimeMillis());

        return beneficiarioRepository.save(beneficiario);
    }

    public Beneficiario atualizarBeneficiario(Beneficiario beneficiario) {
        if (beneficiario.getId() == null) {
            throw new RuntimeException("ID do beneficiário é obrigatório para atualização.");
        }

        Beneficiario existente = beneficiarioRepository.findById(beneficiario.getId())
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado."));

        // Verifica se o CPF foi alterado e se já existe outro beneficiário com esse CPF
        if (!existente.getCpf().equals(beneficiario.getCpf())) {
            Optional<Beneficiario> outroComMesmoCpf = beneficiarioRepository.findByCpf(beneficiario.getCpf());
            if (outroComMesmoCpf.isPresent()) {
                throw new RuntimeException("Já existe um beneficiário cadastrado com este CPF.");
            }
        }

        // Preserva as coletas e doações existentes - nunca sobrescreve o histórico
        if (existente.getColetas() != null && !existente.getColetas().isEmpty()) {
            beneficiario.setColetas(existente.getColetas());
        }
        if (existente.getDoacoes() != null && !existente.getDoacoes().isEmpty()) {
            beneficiario.setDoacoes(existente.getDoacoes());
        }

        // Se reativar o beneficiário, limpa o motivo de inativação
        if (Boolean.TRUE.equals(beneficiario.getAtivo()) && beneficiario.getMotivoInativacao() != null) {
            beneficiario.setMotivoInativacao(null);
        }

        return beneficiarioRepository.save(beneficiario);
    }

    public Beneficiario salvar(Beneficiario beneficiario) {
        if (beneficiario.getId() == null) {
            return criarBeneficiario(beneficiario);
        } else {
            return atualizarBeneficiario(beneficiario);
        }
    }

    public Beneficiario registrarColeta(String cpf) {
        Beneficiario beneficiario = beneficiarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado para o CPF informado."));

        // Verifica se o beneficiário está ativo
        if (Boolean.FALSE.equals(beneficiario.getAtivo())) {
            throw new RuntimeException("Não é possível registrar coleta para beneficiário inativo.");
        }

        // Inicializa a lista de coletas se for nula
        if (beneficiario.getColetas() == null) {
            beneficiario.setColetas(new ArrayList<>());
        }

        beneficiario.getColetas().add(new Coleta(LocalDateTime.now()));
        return beneficiarioRepository.save(beneficiario);
    }

    public Beneficiario registrarDoacao(Beneficiario beneficiario, LocalDateTime dataDoacao, String descricaoDoacao) {
        // Inicializa a lista de doações se for nula
        if (beneficiario.getDoacoes() == null) {
            beneficiario.setDoacoes(new ArrayList<>());
        }

        beneficiario.getDoacoes().add(new Doacoes(dataDoacao, descricaoDoacao));
        return beneficiarioRepository.save(beneficiario);
    }

    public List<Beneficiario> listarTodos() {
        List<Beneficiario> beneficiarios = beneficiarioRepository.findAll();

        beneficiarios.forEach(b -> {
            if (b.getColetas() != null) {
                b.getColetas().sort((c1, c2) -> c2.getDataColeta().compareTo(c1.getDataColeta()));
            }
            if (b.getDoacoes() != null) {
                b.getDoacoes().sort((d1, d2) -> d2.getDataDoacao().compareTo(d1.getDataDoacao()));
            }
        });

        return beneficiarios;
    }

    public Optional<Beneficiario> buscarPorCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            return Optional.empty();
        }
        return beneficiarioRepository.findByCodigoBarras(codigoBarras.trim());
    }

    public List<Beneficiario> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }

        List<Beneficiario> beneficiarios = beneficiarioRepository.findByNomeCompletoContainingIgnoreCaseOrCpfContainingOrCodigoBarrasContaining(termo, termo, termo);

        beneficiarios.forEach(b -> {
            if (b.getColetas() != null) {
                b.getColetas().sort((c1, c2) -> c2.getDataColeta().compareTo(c1.getDataColeta()));
            }
            if (b.getDoacoes() != null) {
                b.getDoacoes().sort((d1, d2) -> d2.getDataDoacao().compareTo(d1.getDataDoacao()));
            }
        });

        return beneficiarios;
    }

    public Beneficiario buscarPorId(String id) {
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado."));

        inicializarObjetosAninhados(beneficiario);

        if (beneficiario.getColetas() != null) {
            beneficiario.getColetas().sort((c1, c2) -> c2.getDataColeta().compareTo(c1.getDataColeta()));
        }

        if (beneficiario.getDoacoes() != null) {
            beneficiario.getDoacoes().sort((d1, d2) -> d2.getDataDoacao().compareTo(d1.getDataDoacao()));
        }

        return beneficiario;
    }

    public void inicializarObjetosAninhados(Beneficiario beneficiario) {
        if (beneficiario.getRenda() == null) {
            beneficiario.setRenda(new Renda());
        }
        if (beneficiario.getRenda().getFontesRenda() == null) {
            beneficiario.getRenda().setFontesRenda(new FontesRenda());
        }

        if (beneficiario.getMoradia() == null) {
            Moradia moradia = new Moradia();
            moradia.setEndereco(new Endereco());
            moradia.setServicos(new Servicos());
            beneficiario.setMoradia(moradia);
        } else {
            if (beneficiario.getMoradia().getEndereco() == null) {
                beneficiario.getMoradia().setEndereco(new Endereco());
            }
            if (beneficiario.getMoradia().getServicos() == null) {
                beneficiario.getMoradia().setServicos(new Servicos());
            }
        }

        if (beneficiario.getEducacaoBens() == null) {
            EducacaoBens educacao = new EducacaoBens();
            educacao.setBens(new Bens());
            beneficiario.setEducacaoBens(educacao);
        } else if (beneficiario.getEducacaoBens().getBens() == null) {
            beneficiario.getEducacaoBens().setBens(new Bens());
        }

        if (beneficiario.getColetas() == null) {
            beneficiario.setColetas(new ArrayList<>());
        }

        if (beneficiario.getDoacoes() == null) {
            beneficiario.setDoacoes(new ArrayList<>());
        }
    }
}
