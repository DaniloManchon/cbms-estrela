package com.estrela.cbms.service;

import com.estrela.cbms.model.*;
import com.estrela.cbms.repository.BeneficiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@Service
public class BeneficiarioService {

    private static final Comparator<Long> ID_NULOS_POR_ULTIMO = Comparator.nullsLast(Comparator.naturalOrder());

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
        if (existente.getVisitasDomiciliares() != null && !existente.getVisitasDomiciliares().isEmpty()) {
            beneficiario.setVisitasDomiciliares(existente.getVisitasDomiciliares());
        }

        // Se reativar o beneficiário, limpa o motivo de inativação
        if (Boolean.TRUE.equals(beneficiario.getAtivo()) && beneficiario.getMotivoInativacao() != null) {
            beneficiario.setMotivoInativacao(null);
        }

        return beneficiarioRepository.save(beneficiario);
    }

    public Beneficiario salvar(Beneficiario beneficiario) {
        if (beneficiario.getId() == null || beneficiario.getId().isBlank()) {
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
        numerarEOrdenarHistoricos(beneficiario);
        return beneficiarioRepository.save(beneficiario);
    }

    public Beneficiario registrarDoacao(Beneficiario beneficiario, LocalDate dataDoacao, String descricaoDoacao) {
        // Inicializa a lista de doações se for nula
        if (beneficiario.getDoacoes() == null) {
            beneficiario.setDoacoes(new ArrayList<>());
        }

        beneficiario.getDoacoes().add(new Doacao(dataDoacao, descricaoDoacao));
        numerarEOrdenarHistoricos(beneficiario);
        return beneficiarioRepository.save(beneficiario);
    }

    public Beneficiario registrarVisitaDomiciliar(Beneficiario beneficiario, LocalDate dataVisita, String nomeVisitante) {
        // Inicializa a lista de visitas domiciliares se for nula
        if (beneficiario.getVisitasDomiciliares() == null) {
            beneficiario.setVisitasDomiciliares(new ArrayList<>());
        }

        beneficiario.getVisitasDomiciliares().add(new VisitaDomiciliar(dataVisita, nomeVisitante));
        numerarEOrdenarHistoricos(beneficiario);
        return beneficiarioRepository.save(beneficiario);
    }

    public void deletarColeta(String beneficiarioId, int indice) {
        Beneficiario beneficiario = beneficiarioRepository.findById(beneficiarioId)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado."));

        if (beneficiario.getColetas() == null || beneficiario.getColetas().isEmpty()) {
            throw new RuntimeException("Nenhuma coleta para deletar.");
        }

        numerarEOrdenarHistoricos(beneficiario);

        if (indice < 0 || indice >= beneficiario.getColetas().size()) {
            throw new RuntimeException("Índice de coleta inválido.");
        }

        beneficiario.getColetas().remove(indice);
        numerarEOrdenarHistoricos(beneficiario);
        beneficiarioRepository.save(beneficiario);
    }

    public void deletarDoacao(String beneficiarioId, int indice) {
        Beneficiario beneficiario = beneficiarioRepository.findById(beneficiarioId)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado."));

        if (beneficiario.getDoacoes() == null || beneficiario.getDoacoes().isEmpty()) {
            throw new RuntimeException("Nenhuma doação para deletar.");
        }

        numerarEOrdenarHistoricos(beneficiario);

        if (indice < 0 || indice >= beneficiario.getDoacoes().size()) {
            throw new RuntimeException("Índice de doação inválido.");
        }

        beneficiario.getDoacoes().remove(indice);
        numerarEOrdenarHistoricos(beneficiario);
        beneficiarioRepository.save(beneficiario);
    }

    public void deletarVisitaDomiciliar(String beneficiarioId, int indice) {
        Beneficiario beneficiario = beneficiarioRepository.findById(beneficiarioId)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado."));

        if (beneficiario.getVisitasDomiciliares() == null || beneficiario.getVisitasDomiciliares().isEmpty()) {
            throw new RuntimeException("Nenhuma visita domiciliar para deletar.");
        }

        numerarEOrdenarHistoricos(beneficiario);

        if (indice < 0 || indice >= beneficiario.getVisitasDomiciliares().size()) {
            throw new RuntimeException("Índice de visita domiciliar inválido.");
        }

        beneficiario.getVisitasDomiciliares().remove(indice);
        numerarEOrdenarHistoricos(beneficiario);
        beneficiarioRepository.save(beneficiario);
    }

    // Numera os registros pela ordem cronológica (id 1 = mais antigo) e exibe do mais recente para o mais antigo
    private void numerarEOrdenarHistoricos(Beneficiario beneficiario) {
        if (beneficiario.getColetas() != null) {
            numerarPorOrdemCronologica(beneficiario.getColetas(),
                    Comparator.comparing(Coleta::getDataColeta).thenComparing(Coleta::getId, ID_NULOS_POR_ULTIMO),
                    Coleta::setId);
            beneficiario.getColetas().sort(Comparator.comparing(Coleta::getId, Comparator.reverseOrder()));
        }
        if (beneficiario.getDoacoes() != null) {
            numerarPorOrdemCronologica(beneficiario.getDoacoes(),
                    Comparator.comparing(Doacao::getDataDoacao).thenComparing(Doacao::getId, ID_NULOS_POR_ULTIMO),
                    Doacao::setId);
            beneficiario.getDoacoes().sort(Comparator.comparing(Doacao::getId, Comparator.reverseOrder()));
        }
        if (beneficiario.getVisitasDomiciliares() != null) {
            numerarPorOrdemCronologica(beneficiario.getVisitasDomiciliares(),
                    Comparator.comparing(VisitaDomiciliar::getDataVisita).thenComparing(VisitaDomiciliar::getId, ID_NULOS_POR_ULTIMO),
                    VisitaDomiciliar::setId);
            beneficiario.getVisitasDomiciliares().sort(Comparator.comparing(VisitaDomiciliar::getId, Comparator.reverseOrder()));
        }
    }

    // Registros com a mesma data mantêm a ordem em que foram registrados (desempate pelo id anterior)
    private static <T> void numerarPorOrdemCronologica(List<T> itens, Comparator<T> cronologico, BiConsumer<T, Long> definirId) {
        List<T> ordenados = itens.stream().sorted(cronologico).toList();
        for (int i = 0; i < ordenados.size(); i++) {
            definirId.accept(ordenados.get(i), (long) i + 1);
        }
    }

    public List<Beneficiario> listarTodos() {
        List<Beneficiario> beneficiarios = beneficiarioRepository.findAll();
        beneficiarios.forEach(this::numerarEOrdenarHistoricos);
        return beneficiarios;
    }

    public Optional<Beneficiario> buscarPorCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            return Optional.empty();
        }
        return beneficiarioRepository.findByCodigoBarrasIgnoreCase(codigoBarras.trim());
    }

    public List<Beneficiario> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }

        List<Beneficiario> beneficiarios = beneficiarioRepository.findByNomeCompletoContainingIgnoreCaseOrCpfContainingOrCodigoBarrasContaining(termo, termo, termo);
        beneficiarios.forEach(this::numerarEOrdenarHistoricos);
        return beneficiarios;
    }

    public Beneficiario buscarPorId(String id) {
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado."));

        inicializarObjetosAninhados(beneficiario);
        numerarEOrdenarHistoricos(beneficiario);

        return beneficiario;
    }

    public void deletarBeneficiario(String id) {
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado."));

        beneficiarioRepository.deleteById(id);
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

        if (beneficiario.getVisitasDomiciliares() == null) {
            beneficiario.setVisitasDomiciliares(new ArrayList<>());
        }
    }
}
