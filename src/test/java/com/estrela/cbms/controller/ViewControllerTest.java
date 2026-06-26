package com.estrela.cbms.controller;

import com.estrela.cbms.model.Responsavel;
import com.estrela.cbms.service.ResponsavelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ViewController.class)
class ViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResponsavelService responsavelService;

    private Responsavel criarResponsavelMock() {
        Responsavel responsavel = new Responsavel();
        responsavel.setId(1L);
        responsavel.setNomeCompleto("João da Silva");
        responsavel.setCpf("191.000.000-00");
        responsavel.setCelular("(11) 98888-8888");
        responsavel.setColetas(Collections.emptyList());
        responsavel.setIdentificacaoFamiliar(Collections.emptyList());

        // Inicializa objetos aninhados para evitar NullPointerException no Thymeleaf
        responsavel.setRenda(new com.estrela.cbms.model.Renda());
        responsavel.getRenda().setFontesRenda(new com.estrela.cbms.model.FontesRenda());

        responsavel.setMoradia(new com.estrela.cbms.model.Moradia());
        responsavel.getMoradia().setEndereco(new com.estrela.cbms.model.Endereco());
        responsavel.getMoradia().setServicos(new com.estrela.cbms.model.Servicos());

        responsavel.setEducacaoBens(new com.estrela.cbms.model.EducacaoBens());
        responsavel.getEducacaoBens().setBens(new com.estrela.cbms.model.Bens());

        return responsavel;
    }

    @Test
    @DisplayName("Deve carregar a página inicial com sucesso")
    void indexPage() throws Exception {
        when(responsavelService.buscar(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("responsaveis"));
    }

    @Test
    @DisplayName("Deve carregar o formulário de cadastro com um novo objeto Responsavel")
    void formPage() throws Exception {
        mockMvc.perform(get("/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(model().attributeExists("responsavel"));
    }

    @Test
    @DisplayName("Deve carregar a página de perfil de um responsável")
    void perfilPage() throws Exception {
        Responsavel responsavel = criarResponsavelMock();

        when(responsavelService.buscarPorId(1L)).thenReturn(responsavel);

        mockMvc.perform(get("/perfil/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil"))
                .andExpect(model().attribute("responsavel", responsavel));
    }

}
