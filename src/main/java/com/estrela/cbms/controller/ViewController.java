package com.estrela.cbms.controller;

import com.estrela.cbms.model.Responsavel;
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

@Controller
public class ViewController {

    @Autowired
    private ResponsavelService responsavelService;

    @GetMapping("/")
    public String index(@RequestParam(required = false) String busca, Model model) {
        if (busca != null && !busca.isBlank()) {
            model.addAttribute("responsaveis", responsavelService.buscar(busca));
            model.addAttribute("termoBusca", busca);
        } else {
            model.addAttribute("responsaveis", responsavelService.listarTodos());
        }
        return "index";
    }

    @GetMapping("/novo")
    public String novoResponsavel(Model model) {
        model.addAttribute("responsavel", new Responsavel());
        return "form";
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
    public String salvar(@ModelAttribute Responsavel responsavel, RedirectAttributes redirectAttributes) {
        try {
            responsavelService.salvar(responsavel);
            redirectAttributes.addFlashAttribute("sucesso", "Responsável cadastrado com sucesso!");
        } catch (Exception e) {
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
