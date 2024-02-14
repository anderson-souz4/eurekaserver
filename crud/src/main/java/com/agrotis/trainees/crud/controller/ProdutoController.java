package com.agrotis.trainees.crud.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.agrotis.trainees.crud.dtos.ParceiroNegocioDto;
import com.agrotis.trainees.crud.dtos.ProdutoDto;
import com.agrotis.trainees.crud.service.ProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    
    private final ProdutoService service;
    
    @Autowired
    public ProdutoController(ProdutoService service) {
        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ProdutoDto produto) {
        ProdutoDto produtoSalvo = service.salvar(produto);

        URI uri = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(produtoSalvo.getId())
                        .toUri();

        return ResponseEntity.created(uri).body(produtoSalvo);
    }
    

}