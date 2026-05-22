package com.estrela.cbms.controller;

import com.estrela.cbms.model.Coleta;
import com.estrela.cbms.model.Responsavel;
import com.estrela.cbms.service.ResponsavelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/responsavel")
public class ResponsavelController {

    @Autowired
    private ResponsavelService responsavelService;

    @PostMapping("/novo")
    public ResponseEntity<?> criar(@RequestBody Responsavel responsavel) {
        try {
            Responsavel salvo = responsavelService.salvar(responsavel);
            return new ResponseEntity<>(salvo, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{cpf}/coleta")
    public ResponseEntity<?> registrarColeta(@PathVariable String cpf) {
        try {
            Coleta coleta = responsavelService.registrarColeta(cpf);
            return new ResponseEntity<>(coleta, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
