package com.estrela.cbms.service;

import com.estrela.cbms.model.*;
import com.estrela.cbms.repository.ColetaRepository;
import com.estrela.cbms.repository.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResponsavelService {

    @Autowired
    private ResponsavelRepository responsavelRepository;

    @Autowired
    private ColetaRepository coletaRepository;

    public Responsavel salvar(Responsavel responsavel) {
        if (responsavel.getId() != null) {
            Responsavel atual = responsavelRepository.findById(responsavel.getId())
                    .orElseThrow(() -> new RuntimeException("Responsável não encontrado para atualização."));
            // Preserva a lista de coletas que não vem do formulário
            responsavel.setColetas(atual.getColetas());
        }

        Optional<Responsavel> existente = responsavelRepository.findByCpf(responsavel.getCpf());
        
        if (existente.isPresent()) {
            // Se for um novo cadastro OU se o CPF pertence a OUTRO ID, bloqueia
            if (responsavel.getId() == null || !existente.get().getId().equals(responsavel.getId())) {
                throw new RuntimeException("Já existe um responsável cadastrado com este CPF.");
            }
        }

        // Gerar código de barras automaticamente se não existir
        if (responsavel.getCodigoBarras() == null || responsavel.getCodigoBarras().trim().isEmpty()) {
            responsavel.setCodigoBarras("EST" + System.currentTimeMillis());
        } else {
            // Se informado manualmente, validar unicidade
            Optional<Responsavel> existenteCodigo = responsavelRepository.findByCodigoBarras(responsavel.getCodigoBarras().trim());
            if (existenteCodigo.isPresent()) {
                if (responsavel.getId() == null || !existenteCodigo.get().getId().equals(responsavel.getId())) {
                    throw new RuntimeException("Já existe um responsável cadastrado com este Código de Barras.");
                }
            }
        }

        return responsavelRepository.save(responsavel);
    }

    public Coleta registrarColeta(String cpf) {
        Responsavel responsavel = responsavelRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado para o CPF informado."));
        
        Coleta novaColeta = new Coleta(LocalDateTime.now(), responsavel);
        return coletaRepository.save(novaColeta);
    }

    public List<Responsavel> listarTodos() {
        return responsavelRepository.findAll();
    }

    public Optional<Responsavel> buscarPorCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            return Optional.empty();
        }
        return responsavelRepository.findByCodigoBarras(codigoBarras.trim());
    }

    public List<Responsavel> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }
        return responsavelRepository.findByNomeCompletoContainingIgnoreCaseOrCpfContainingOrCodigoBarrasContaining(termo, termo, termo);
    }

    public Responsavel buscarPorId(Long id) {
        Responsavel responsavel = responsavelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado."));
        
        inicializarObjetosAninhados(responsavel);
        
        // Garante a ordenação das coletas da mais recente para a mais antiga
        if (responsavel.getColetas() != null) {
            responsavel.getColetas().sort((c1, c2) -> c2.getDataColeta().compareTo(c1.getDataColeta()));
        }
        
        return responsavel;
    }

    public void inicializarObjetosAninhados(Responsavel responsavel) {
        if (responsavel.getRenda() == null) {
            responsavel.setRenda(new Renda());
        }
        if (responsavel.getRenda().getFontesRenda() == null) {
            responsavel.getRenda().setFontesRenda(new FontesRenda());
        }
        
        if (responsavel.getMoradia() == null) {
            responsavel.setMoradia(new Moradia());
        }
        if (responsavel.getMoradia().getEndereco() == null) {
            responsavel.getMoradia().setEndereco(new Endereco());
        }
        if (responsavel.getMoradia().getServicos() == null) {
            responsavel.getMoradia().setServicos(new Servicos());
        }
        
        if (responsavel.getEducacaoBens() == null) {
            responsavel.setEducacaoBens(new EducacaoBens());
        }
        if (responsavel.getEducacaoBens().getBens() == null) {
            responsavel.getEducacaoBens().setBens(new Bens());
        }
    }
}
