package com.estrela.cbms.service;

import com.estrela.cbms.model.Coleta;
import com.estrela.cbms.model.Responsavel;
import com.estrela.cbms.repository.ColetaRepository;
import com.estrela.cbms.repository.ResponsavelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponsavelServiceTest {

    @Mock
    private ResponsavelRepository responsavelRepository;

    @Mock
    private ColetaRepository coletaRepository;

    @InjectMocks
    private ResponsavelService responsavelService;

    private Responsavel responsavel;

    @BeforeEach
    void setUp() {
        responsavel = new Responsavel();
        responsavel.setId(1L);
        responsavel.setNomeCompleto("João da Silva");
        responsavel.setCpf("123.456.789-00");
    }

    @Test
    @DisplayName("Deve salvar um novo responsável com sucesso")
    void salvarComSucesso() {
        when(responsavelRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(responsavelRepository.save(any(Responsavel.class))).thenReturn(responsavel);

        Responsavel salvo = responsavelService.salvar(responsavel);

        assertNotNull(salvo);
        assertEquals(responsavel.getCpf(), salvo.getCpf());
        verify(responsavelRepository, times(1)).save(responsavel);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar responsável com CPF já cadastrado")
    void salvarComCpfDuplicado() {
        Responsavel outroResponsavel = new Responsavel();
        outroResponsavel.setId(2L);
        outroResponsavel.setCpf("123.456.789-00");

        when(responsavelRepository.findByCpf(anyString())).thenReturn(Optional.of(outroResponsavel));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            responsavelService.salvar(responsavel);
        });

        assertEquals("Já existe um responsável cadastrado com este CPF.", exception.getMessage());
        verify(responsavelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir atualizar o mesmo responsável mantendo o CPF e preservando coletas")
    void atualizarMesmoResponsavelPreservandoColetas() {
        List<Coleta> coletasExistentes = List.of(new Coleta());
        responsavel.setColetas(coletasExistentes);

        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));
        when(responsavelRepository.findByCpf(anyString())).thenReturn(Optional.of(responsavel));
        when(responsavelRepository.save(any(Responsavel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Responsavel paraAtualizar = new Responsavel();
        paraAtualizar.setId(1L);
        paraAtualizar.setNomeCompleto("João da Silva Atualizado");
        paraAtualizar.setCpf("123.456.789-00");
        paraAtualizar.setColetas(null); // Simulando o que vem do formulário

        Responsavel salvo = responsavelService.salvar(paraAtualizar);

        assertNotNull(salvo);
        assertEquals(coletasExistentes, salvo.getColetas());
        assertEquals("João da Silva Atualizado", salvo.getNomeCompleto());
        verify(responsavelRepository, times(1)).save(paraAtualizar);
    }

    @Test
    @DisplayName("Deve registrar uma nova coleta para um responsável existente")
    void registrarColetaComSucesso() {
        when(responsavelRepository.findByCpf(anyString())).thenReturn(Optional.of(responsavel));
        when(coletaRepository.save(any(Coleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Coleta coleta = responsavelService.registrarColeta("123.456.789-00");

        assertNotNull(coleta);
        assertEquals(responsavel, coleta.getResponsavel());
        assertNotNull(coleta.getDataColeta());
        verify(coletaRepository, times(1)).save(any(Coleta.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar coleta para CPF inexistente")
    void registrarColetaCpfNaoEncontrado() {
        when(responsavelRepository.findByCpf(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            responsavelService.registrarColeta("000.000.000-00");
        });

        verify(coletaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar responsáveis por termo de pesquisa")
    void buscarPorTermo() {
        List<Responsavel> lista = List.of(responsavel);
        when(responsavelRepository.findByNomeCompletoContainingIgnoreCaseOrCpfContainingOrCodigoBarrasContaining("João","João","João"))
                .thenReturn(lista);

        List<Responsavel> resultado = responsavelService.buscar("João");

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("João da Silva", resultado.get(0).getNomeCompleto());
    }

    @Test
    @DisplayName("Deve listar todos quando o termo de busca for nulo ou vazio")
    void buscarComTermoVazio() {
        responsavelService.buscar("");
        verify(responsavelRepository, times(1)).findAll();

        responsavelService.buscar(null);
        verify(responsavelRepository, times(2)).findAll();
    }

    @Test
    @DisplayName("Deve buscar por ID e inicializar objetos aninhados")
    void buscarPorIdEInicializar() {
        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));

        Responsavel encontrado = responsavelService.buscarPorId(1L);

        assertNotNull(encontrado);
        assertNotNull(encontrado.getRenda());
        assertNotNull(encontrado.getMoradia());
        assertNotNull(encontrado.getEducacaoBens());
        verify(responsavelRepository, times(1)).findById(1L);
    }
}
