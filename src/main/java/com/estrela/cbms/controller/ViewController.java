package com.estrela.cbms.controller;

import com.estrela.cbms.model.*;
import com.estrela.cbms.service.ResponsavelService;
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

@Controller
public class ViewController {

    @Autowired
    private ResponsavelService responsavelService;

    @GetMapping("/")
    public String index(@RequestParam(required = false) String busca, Model model) {
        java.util.List<Responsavel> lista;
        if (busca != null && !busca.isBlank()) {
            lista = responsavelService.buscar(busca);
        } else {
            lista = responsavelService.listarTodos();
        }
        System.out.println("DEBUG: Listando responsáveis. Quantidade encontrada: " + (lista != null ? lista.size() : "null"));
        model.addAttribute("responsaveis", lista);
        model.addAttribute("termoBusca", busca);
        return "index";
    }

    @GetMapping("/novo")
    public String novoResponsavel(Model model) {
        Responsavel responsavel = new Responsavel();
        responsavelService.inicializarObjetosAninhados(responsavel);
        model.addAttribute("responsavel", responsavel);
        return "form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        try {
            Responsavel responsavel = responsavelService.buscarPorId(id);
            model.addAttribute("responsavel", responsavel);
            return "form";
        } catch (Exception e) {
            return "redirect:/?erro=Responsavel nao encontrado";
        }
    }

    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("responsavel", responsavelService.buscarPorId(id));
            return "perfil";
        } catch (Exception e) {
            return "redirect:/?erro=Responsavel nao encontrado";
        }
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Responsavel responsavel, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            String erroMsg = result.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("erro", erroMsg);
            return "redirect:/novo";
        }
        try {
            System.out.println("DEBUG: Tentando salvar responsável: " + responsavel.getNomeCompleto());
            responsavelService.salvar(responsavel);
            System.out.println("DEBUG: Responsável salvo com sucesso!");
            redirectAttributes.addFlashAttribute("sucesso", "Responsável cadastrado com sucesso!");
        } catch (Exception e) {
            System.err.println("DEBUG: Erro ao salvar responsável: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/novo";
        }
        return "redirect:/";
    }

    @PostMapping("/coleta/{cpf}")
    public String registrarColeta(@PathVariable String cpf, RedirectAttributes redirectAttributes) {
        try {
            responsavelService.registrarColeta(cpf);
            redirectAttributes.addFlashAttribute("sucesso", "Coleta registrada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/";
    }
}
