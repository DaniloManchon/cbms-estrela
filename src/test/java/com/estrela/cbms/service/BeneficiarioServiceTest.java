package com.estrela.cbms.service;

import com.estrela.cbms.model.Coleta;
import com.estrela.cbms.model.Beneficiario;
import com.estrela.cbms.repository.BeneficiarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficiarioServiceTest {

    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @InjectMocks
    private BeneficiarioService beneficiarioService;

    private Beneficiario beneficiario;

    @BeforeEach
    void setUp() {
        beneficiario = new Beneficiario();
        beneficiario.setId("1");
        beneficiario.setNomeCompleto("João da Silva");
        beneficiario.setCpf("123.456.789-00");
    }

    // ===== TESTES PARA CRIAR NOVO BENEFICIÁRIO =====

    @Test
    @DisplayName("Deve criar novo beneficiário com sucesso")
    void criarBeneficiarioComSucesso() {
        beneficiario.setId(null);
        when(beneficiarioRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenReturn(beneficiario);

        Beneficiario criado = beneficiarioService.criarBeneficiario(beneficiario);

        assertNotNull(criado);
        assertEquals(beneficiario.getCpf(), criado.getCpf());
        assertTrue(criado.getCodigoBarras().startsWith("EST"));
        verify(beneficiarioRepository, times(1)).save(beneficiario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar beneficiário com CPF duplicado")
    void criarBeneficiarioComCpfDuplicado() {
        beneficiario.setId(null);
        Beneficiario existente = new Beneficiario();
        existente.setId("2");
        existente.setCpf("123.456.789-00");

        when(beneficiarioRepository.findByCpf(anyString())).thenReturn(Optional.of(existente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            beneficiarioService.criarBeneficiario(beneficiario);
        });

        assertEquals("Já existe um beneficiário cadastrado com este CPF.", exception.getMessage());
        verify(beneficiarioRepository, never()).save(any());
    }

    // ===== TESTES PARA ATUALIZAR BENEFICIÁRIO =====

    @Test
    @DisplayName("Deve atualizar beneficiário existente preservando coletas")
    void atualizarBeneficiarioPreservandoColetas() {
        List<Coleta> coletasExistentes = new ArrayList<>();
        coletasExistentes.add(new Coleta(LocalDateTime.now().minusDays(5)));
        coletasExistentes.add(new Coleta(LocalDateTime.now().minusDays(1)));

        Beneficiario beneficiarioExistente = new Beneficiario();
        beneficiarioExistente.setId("1");
        beneficiarioExistente.setNomeCompleto("João da Silva");
        beneficiarioExistente.setCpf("123.456.789-00");
        beneficiarioExistente.setColetas(coletasExistentes);

        when(beneficiarioRepository.findById("1")).thenReturn(Optional.of(beneficiarioExistente));
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Beneficiario paraAtualizar = new Beneficiario();
        paraAtualizar.setId("1");
        paraAtualizar.setNomeCompleto("João da Silva Atualizado");
        paraAtualizar.setCpf("123.456.789-00");
        paraAtualizar.setColetas(null);

        Beneficiario atualizado = beneficiarioService.atualizarBeneficiario(paraAtualizar);

        assertNotNull(atualizado);
        assertEquals(coletasExistentes, atualizado.getColetas());
        assertEquals("João da Silva Atualizado", atualizado.getNomeCompleto());
        assertEquals(2, atualizado.getColetas().size());
        verify(beneficiarioRepository, times(1)).save(paraAtualizar);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar sem ID")
    void atualizarBeneficiarioSemId() {
        beneficiario.setId(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            beneficiarioService.atualizarBeneficiario(beneficiario);
        });

        assertEquals("ID do beneficiário é obrigatório para atualização.", exception.getMessage());
        verify(beneficiarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar para CPF já existente em outro beneficiário")
    void atualizarParaCpfDeDoutrobeneficiario() {
        Beneficiario beneficiarioExistente = new Beneficiario();
        beneficiarioExistente.setId("1");
        beneficiarioExistente.setCpf("123.456.789-00");

        Beneficiario outroBeneficiario = new Beneficiario();
        outroBeneficiario.setId("2");
        outroBeneficiario.setCpf("999.999.999-99");

        when(beneficiarioRepository.findById("1")).thenReturn(Optional.of(beneficiarioExistente));
        when(beneficiarioRepository.findByCpf("999.999.999-99")).thenReturn(Optional.of(outroBeneficiario));

        Beneficiario paraAtualizar = new Beneficiario();
        paraAtualizar.setId("1");
        paraAtualizar.setCpf("999.999.999-99");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            beneficiarioService.atualizarBeneficiario(paraAtualizar);
        });

        assertEquals("Já existe um beneficiário cadastrado com este CPF.", exception.getMessage());
        verify(beneficiarioRepository, never()).save(any());
    }

    // ===== TESTES PARA FACHADA SALVAR =====

    @Test
    @DisplayName("Deve rotear para criarBeneficiario quando ID é nulo")
    void salvarRotaParaCriarQuandoIdNulo() {
        beneficiario.setId(null);
        when(beneficiarioRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenReturn(beneficiario);

        Beneficiario salvo = beneficiarioService.salvar(beneficiario);

        assertNotNull(salvo);
        assertTrue(salvo.getCodigoBarras().startsWith("EST"));
        verify(beneficiarioRepository, times(1)).save(beneficiario);
    }

    @Test
    @DisplayName("Deve rotear para atualizarBeneficiario quando ID não é nulo")
    void salvarRotaParaAtualizarQuandoIdPresente() {
        List<Coleta> coletas = new ArrayList<>();
        coletas.add(new Coleta(LocalDateTime.now()));
        beneficiario.setColetas(coletas);

        when(beneficiarioRepository.findById("1")).thenReturn(Optional.of(beneficiario));
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Beneficiario salvo = beneficiarioService.salvar(beneficiario);

        assertNotNull(salvo);
        assertEquals(coletas, salvo.getColetas());
        verify(beneficiarioRepository, times(1)).save(beneficiario);
    }

    @Test
    @DisplayName("Deve registrar uma nova coleta para um beneficiário existente")
    void registrarColetaComSucesso() {
        beneficiario.setColetas(new ArrayList<>());
        when(beneficiarioRepository.findByCpf(anyString())).thenReturn(Optional.of(beneficiario));
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        beneficiarioService.registrarColeta("123.456.789-00");

        assertNotNull(beneficiario.getColetas());
        assertEquals(1, beneficiario.getColetas().size());
        verify(beneficiarioRepository, times(1)).save(beneficiario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar coleta para CPF inexistente")
    void registrarColetaCpfNaoEncontrado() {
        when(beneficiarioRepository.findByCpf(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            beneficiarioService.registrarColeta("000.000.000-00");
        });

        verify(beneficiarioRepository, never()).save(any());
    }

    // ===== TESTES PARA LISTAGEM E BUSCA COM ORDENAÇÃO =====

    @Test
    @DisplayName("Deve listar todos beneficiários com coletas ordenadas por data decrescente")
    void listarTodosComColetasOrdenadas() {
        Beneficiario ben1 = new Beneficiario();
        ben1.setId("1");
        ben1.setNomeCompleto("João");
        List<Coleta> coletas1 = new ArrayList<>();
        coletas1.add(new Coleta(LocalDateTime.now().minusDays(5)));
        coletas1.add(new Coleta(LocalDateTime.now().minusDays(1)));
        coletas1.add(new Coleta(LocalDateTime.now().minusDays(10)));
        ben1.setColetas(coletas1);

        when(beneficiarioRepository.findAll()).thenReturn(List.of(ben1));

        List<Beneficiario> resultado = beneficiarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(3, resultado.get(0).getColetas().size());
        // Verifica se a coleta mais recente está primeiro
        assertTrue(resultado.get(0).getColetas().get(0).getDataColeta()
                .isAfter(resultado.get(0).getColetas().get(1).getDataColeta()));
        assertTrue(resultado.get(0).getColetas().get(1).getDataColeta()
                .isAfter(resultado.get(0).getColetas().get(2).getDataColeta()));
    }

    @Test
    @DisplayName("Deve buscar beneficiários com coletas ordenadas por data decrescente")
    void buscarComColetasOrdenadas() {
        Beneficiario ben1 = new Beneficiario();
        ben1.setId("1");
        ben1.setNomeCompleto("João");
        List<Coleta> coletas1 = new ArrayList<>();
        coletas1.add(new Coleta(LocalDateTime.now().minusDays(3)));
        coletas1.add(new Coleta(LocalDateTime.now().minusDays(1)));
        ben1.setColetas(coletas1);

        when(beneficiarioRepository.findByNomeCompletoContainingIgnoreCaseOrCpfContainingOrCodigoBarrasContaining("João","João","João"))
                .thenReturn(List.of(ben1));

        List<Beneficiario> resultado = beneficiarioService.buscar("João");

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(2, resultado.get(0).getColetas().size());
        // Verifica se a coleta mais recente está primeiro
        assertTrue(resultado.get(0).getColetas().get(0).getDataColeta()
                .isAfter(resultado.get(0).getColetas().get(1).getDataColeta()));
    }

    @Test
    @DisplayName("Deve listar todos quando o termo de busca for nulo ou vazio")
    void buscarComTermoVazio() {
        when(beneficiarioRepository.findAll()).thenReturn(new ArrayList<>());

        beneficiarioService.buscar("");
        verify(beneficiarioRepository, times(1)).findAll();

        beneficiarioService.buscar(null);
        verify(beneficiarioRepository, times(2)).findAll();
    }

    @Test
    @DisplayName("Deve buscar por ID e inicializar objetos aninhados")
    void buscarPorIdEInicializar() {
        when(beneficiarioRepository.findById("1")).thenReturn(Optional.of(beneficiario));

        Beneficiario encontrado = beneficiarioService.buscarPorId("1");

        assertNotNull(encontrado);
        assertNotNull(encontrado.getRenda());
        assertNotNull(encontrado.getMoradia());
        assertNotNull(encontrado.getEducacaoBens());
        verify(beneficiarioRepository, times(1)).findById("1");
    }
}
