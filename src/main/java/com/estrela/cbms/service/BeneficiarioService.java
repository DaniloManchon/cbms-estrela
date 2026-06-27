package com.estrela.cbms.service;

import com.estrela.cbms.model.*;
import com.estrela.cbms.repository.ColetaRepository;
import com.estrela.cbms.repository.BeneficiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BeneficiarioService {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private ColetaRepository coletaRepository;

    public Beneficiario salvar(Beneficiario beneficiario) {
        if (beneficiario.getId() != null) {
            Beneficiario atual = beneficiarioRepository.findById(beneficiario.getId())
                    .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado para atualização."));
            // Preserva a lista de coletas que não vem do formulário
            beneficiario.setColetas(atual.getColetas());
        }

        Optional<Beneficiario> existente = beneficiarioRepository.findByCpf(beneficiario.getCpf());
        
        if (existente.isPresent()) {
            // Se for um novo cadastro OU se o CPF pertence a OUTRO ID, bloqueia
            if (beneficiario.getId() == null || !existente.get().getId().equals(beneficiario.getId())) {
                throw new RuntimeException("Já existe um beneficiário cadastrado com este CPF.");
            }
        }

        // Gerar código de barras automaticamente se não existir
        if (beneficiario.getCodigoBarras() == null || beneficiario.getCodigoBarras().trim().isEmpty()) {
            beneficiario.setCodigoBarras("EST" + System.currentTimeMillis());
        } else {
            // Se informado manualmente, validar unicidade
            Optional<Beneficiario> existenteCodigo = beneficiarioRepository.findByCodigoBarras(beneficiario.getCodigoBarras().trim());
            if (existenteCodigo.isPresent()) {
                if (beneficiario.getId() == null || !existenteCodigo.get().getId().equals(beneficiario.getId())) {
                    throw new RuntimeException("Já existe um beneficiário cadastrado com este Código de Barras.");
                }
            }
        }

        return beneficiarioRepository.save(beneficiario);
    }

    public Coleta registrarColeta(String cpf) {
        Beneficiario beneficiario = beneficiarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado para o CPF informado."));
        
        Coleta novaColeta = new Coleta(LocalDateTime.now(), beneficiario);
        return coletaRepository.save(novaColeta);
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

    public Beneficiario buscarPorId(Long id) {
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado."));
        
        inicializarObjetosAninhados(beneficiario);
        
        // Garante a ordenação das coletas da mais recente para a mais antiga
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
            beneficiario.setMoradia(new Moradia());
        }
        if (beneficiario.getMoradia().getEndereco() == null) {
            beneficiario.getMoradia().setEndereco(new Endereco());
        }
        if (beneficiario.getMoradia().getServicos() == null) {
            beneficiario.getMoradia().setServicos(new Servicos());
        }
        
        if (beneficiario.getEducacaoBens() == null) {
            beneficiario.setEducacaoBens(new EducacaoBens());
        }
        if (beneficiario.getEducacaoBens().getBens() == null) {
            beneficiario.getEducacaoBens().setBens(new Bens());
        }
    }
}
