package com.estrela.cbms.controller;

import com.estrela.cbms.model.*;
import com.estrela.cbms.service.BeneficiarioService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Log4j2
@Controller
public class ViewController {

    @Autowired
    private BeneficiarioService beneficiarioService;

    @GetMapping("/")
    public String index(@RequestParam(required = false) String busca, Model model) {
        java.util.List<Beneficiario> lista;
        if (busca != null && !busca.isBlank()) {
            // Tenta buscar por código de barras exato para redirecionamento imediato
            java.util.Optional<Beneficiario> porCodigo = beneficiarioService.buscarPorCodigoBarras(busca.trim());
            if (porCodigo.isPresent()) {
                return "redirect:/perfil/" + porCodigo.get().getId();
            }
            lista = beneficiarioService.buscar(busca);
        } else {
            lista = beneficiarioService.listarTodos();
        }
        log.debug("Listando responsáveis. Quantidade encontrada: " + (lista != null ? lista.size() : "null"));
        model.addAttribute("beneficiarios", lista);
        model.addAttribute("termoBusca", busca);
        return "index";
    }

    @GetMapping("/novo")
    public String novoBeneficiario(Model model) {
        Beneficiario beneficiario = new Beneficiario();
        beneficiarioService.inicializarObjetosAninhados(beneficiario);
        model.addAttribute("beneficiario", beneficiario);
            return "cadastro_beneficiario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        try {
            Beneficiario beneficiario = beneficiarioService.buscarPorId(id);
            model.addAttribute("beneficiario", beneficiario);
        return "cadastro_beneficiario";
        } catch (Exception e) {
            return "redirect:/?erro=Beneficiario nao encontrado";
        }
    }

    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("beneficiario", beneficiarioService.buscarPorId(id));
            return "perfil_beneficiario";
        } catch (Exception e) {
            return "redirect:/?erro=Beneficiario nao encontrado";
        }
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Beneficiario beneficiario, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            String erroMsg = result.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("erro", erroMsg);
            return "redirect:/novo";
        }
        try {
            log.debug("Tentando salvar beneficiário: " + beneficiario.getNomeCompleto());
            beneficiarioService.salvar(beneficiario);
            log.debug("Beneficiário salvo com sucesso!");
            redirectAttributes.addFlashAttribute("sucesso", "Beneficiário cadastrado com sucesso!");
        } catch (Exception e) {
            log.debug("Erro ao salvar beneficiário: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/novo";
        }
        return "redirect:/";
    }

    @PostMapping("/coleta/{cpf}")
    public String registrarColeta(@PathVariable String cpf, @RequestParam(required = false) Boolean noPerfil, RedirectAttributes redirectAttributes) {
        try {
            Coleta coleta = beneficiarioService.registrarColeta(cpf);
            redirectAttributes.addFlashAttribute("sucesso", "Coleta registrada com sucesso!");
            if (Boolean.TRUE.equals(noPerfil) && coleta.getBeneficiario() != null) {
                return "redirect:/perfil/" + coleta.getBeneficiario().getId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/";
    }
}
