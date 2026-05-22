package com.estrela.cbms.service;

import com.estrela.cbms.model.Coleta;
import com.estrela.cbms.model.Responsavel;
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
        Optional<Responsavel> existente = responsavelRepository.findByCpf(responsavel.getCpf());
        if (existente.isPresent()) {
            throw new RuntimeException("Já existe um responsável cadastrado com este CPF.");
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

    public List<Responsavel> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }
        return responsavelRepository.findByNomeCompletoContainingIgnoreCaseOrCpfContaining(termo, termo);
    }

    public Responsavel buscarPorId(Long id) {
        Responsavel responsavel = responsavelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado."));
        
        // Garante a ordenação das coletas da mais recente para a mais antiga
        if (responsavel.getColetas() != null) {
            responsavel.getColetas().sort((c1, c2) -> c2.getDataColeta().compareTo(c1.getDataColeta()));
        }
        
        return responsavel;
    }
}
