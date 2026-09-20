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

    public Beneficiario salvar(Beneficiario beneficiario) {

        Optional<Beneficiario> existente = beneficiarioRepository.findByCpf(beneficiario.getCpf());

        if (existente.isPresent()) {
            // Se é uma edição (beneficiário com ID) e o CPF já existe, verifica se é o mesmo beneficiário
            if (beneficiario.getId() != null && existente.get().getId().equals(beneficiario.getId())) {
                // É o mesmo beneficiário sendo editado, permitir atualização
            } else {
                throw new RuntimeException("Já existe um beneficiário cadastrado com este CPF.");
            }
        }

        // Se for novo, gera código de barras
        if (beneficiario.getId() == null) {
            beneficiario.setCodigoBarras("EST" + System.currentTimeMillis());
        }

        return beneficiarioRepository.save(beneficiario);
    }

    public Beneficiario registrarColeta(String cpf) {
        Beneficiario beneficiario = beneficiarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado para o CPF informado."));

        beneficiario.getColetas().add(new Coleta(LocalDateTime.now()));
        return beneficiarioRepository.save(beneficiario);
    }

    public List<Beneficiario> listarTodos() {
        return beneficiarioRepository.findAll();
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
        return beneficiarioRepository.findByNomeCompletoContainingIgnoreCaseOrCpfContainingOrCodigoBarrasContaining(termo, termo, termo);
    }

    public Beneficiario buscarPorId(String id) {
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado."));
        
        inicializarObjetosAninhados(beneficiario);
        
        if (beneficiario.getColetas() != null) {
            beneficiario.getColetas().sort((c1, c2) -> c2.getDataColeta().compareTo(c1.getDataColeta()));
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
    }
}
