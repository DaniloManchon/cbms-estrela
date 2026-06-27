package com.estrela.cbms.controller;

import com.estrela.cbms.model.Beneficiario;
import com.estrela.cbms.service.BeneficiarioService;
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
    private BeneficiarioService beneficiarioService;

    private Beneficiario criarBeneficiarioMock() {
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setId(1L);
        beneficiario.setNomeCompleto("João da Silva");
        beneficiario.setCpf("191.000.000-00");
        beneficiario.setCelular("(11) 98888-8888");
        beneficiario.setColetas(Collections.emptyList());
        beneficiario.setIdentificacaoFamiliar(Collections.emptyList());

        // Inicializa objetos aninhados para evitar NullPointerException no Thymeleaf
        beneficiario.setRenda(new com.estrela.cbms.model.Renda());
        beneficiario.getRenda().setFontesRenda(new com.estrela.cbms.model.FontesRenda());

        beneficiario.setMoradia(new com.estrela.cbms.model.Moradia());
        beneficiario.getMoradia().setEndereco(new com.estrela.cbms.model.Endereco());
        beneficiario.getMoradia().setServicos(new com.estrela.cbms.model.Servicos());

        beneficiario.setEducacaoBens(new com.estrela.cbms.model.EducacaoBens());
        beneficiario.getEducacaoBens().setBens(new com.estrela.cbms.model.Bens());

        return beneficiario;
    }

    @Test
    @DisplayName("Deve carregar a página inicial com sucesso")
    void indexPage() throws Exception {
        when(beneficiarioService.buscar(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("beneficiarios"));
    }

    @Test
    @DisplayName("Deve carregar o formulário de cadastro com um novo objeto Beneficiario")
    void formPage() throws Exception {
        mockMvc.perform(get("/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro_beneficiario"))
                .andExpect(model().attributeExists("beneficiario"));
    }

    @Test
    @DisplayName("Deve carregar a página de perfil de um beneficiário")
    void perfilPage() throws Exception {
        Beneficiario beneficiario = criarBeneficiarioMock();

        when(beneficiarioService.buscarPorId(1L)).thenReturn(beneficiario);

        mockMvc.perform(get("/perfil/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil_beneficiario"))
                .andExpect(model().attribute("beneficiario", beneficiario));
    }

}
