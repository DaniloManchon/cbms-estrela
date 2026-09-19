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

    @Test
    @DisplayName("Deve salvar um novo beneficiário com sucesso")
    void salvarComSucesso() {
        beneficiario.setId(null);
        when(beneficiarioRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenReturn(beneficiario);

        Beneficiario salvo = beneficiarioService.salvar(beneficiario);

        assertNotNull(salvo);
        assertEquals(beneficiario.getCpf(), salvo.getCpf());
        verify(beneficiarioRepository, times(1)).save(beneficiario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar beneficiário com CPF já cadastrado")
    void salvarComCpfDuplicado() {
        beneficiario.setId(null);
        Beneficiario outroBeneficiario = new Beneficiario();
        outroBeneficiario.setId("2");
        outroBeneficiario.setCpf("123.456.789-00");

        when(beneficiarioRepository.findByCpf(anyString())).thenReturn(Optional.of(outroBeneficiario));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            beneficiarioService.salvar(beneficiario);
        });

        assertEquals("Já existe um beneficiário cadastrado com este CPF.", exception.getMessage());
        verify(beneficiarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir atualizar o mesmo beneficiário mantendo o CPF e preservando coletas")
    void atualizarMesmoBeneficiarioPreservandoColetas() {
        List<Coleta> coletasExistentes = new ArrayList<>();
        coletasExistentes.add(new Coleta());
        beneficiario.setColetas(coletasExistentes);

        when(beneficiarioRepository.findById("1")).thenReturn(Optional.of(beneficiario));
        when(beneficiarioRepository.findByCpf(anyString())).thenReturn(Optional.of(beneficiario));
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Beneficiario paraAtualizar = new Beneficiario();
        paraAtualizar.setId("1");
        paraAtualizar.setNomeCompleto("João da Silva Atualizado");
        paraAtualizar.setCpf("123.456.789-00");
        paraAtualizar.setColetas(null); 

        Beneficiario salvo = beneficiarioService.salvar(paraAtualizar);

        assertNotNull(salvo);
        assertEquals(coletasExistentes, salvo.getColetas());
        assertEquals("João da Silva Atualizado", salvo.getNomeCompleto());
        verify(beneficiarioRepository, times(1)).save(paraAtualizar);
    }

    @Test
    @DisplayName("Deve registrar uma nova coleta para um beneficiário existente")
    void registrarColetaComSucesso() {
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

    @Test
    @DisplayName("Deve buscar responsáveis por termo de pesquisa")
    void buscarPorTermo() {
        List<Beneficiario> lista = List.of(beneficiario);
        when(beneficiarioRepository.findByNomeCompletoContainingIgnoreCaseOrCpfContainingOrCodigoBarrasContaining("João","João","João"))
                .thenReturn(lista);

        List<Beneficiario> resultado = beneficiarioService.buscar("João");

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("João da Silva", resultado.get(0).getNomeCompleto());
    }

    @Test
    @DisplayName("Deve listar todos quando o termo de busca for nulo ou vazio")
    void buscarComTermoVazio() {
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
