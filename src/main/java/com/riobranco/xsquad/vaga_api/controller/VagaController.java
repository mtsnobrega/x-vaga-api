package com.riobranco.xsquad.vaga_api.controller;

import com.riobranco.xsquad.vaga_api.model.ResumoVagas;
import com.riobranco.xsquad.vaga_api.model.StatusVaga;
import com.riobranco.xsquad.vaga_api.model.Vaga;
import com.riobranco.xsquad.vaga_api.service.VagaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vagas")
public class VagaController {

    private final VagaService service;

    public VagaController(VagaService service) {
        this.service = service;
    }

    @PostMapping("/nova-vaga")
    public ResponseEntity<Vaga> criar(@RequestBody Vaga vaga) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(vaga));
    }

    @GetMapping("/listar-vagas")
    public List<Vaga> listar() {
        return service.listar();
    }

    @GetMapping("/listar-vagas/{id}")
    public Vaga buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/atualizar-vagas/{id}")
    public Vaga atualizar(@PathVariable Long id, @RequestBody Vaga vaga) {
        return service.atualizar(id, vaga);
    }

    @DeleteMapping("/deletar-vagas/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ocupar")
    public Vaga ocupar(@PathVariable Long id) {
        return service.ocupar(id);
    }

    @PatchMapping("/{id}/liberar")
    public Vaga liberar(@PathVariable Long id) {
        return service.liberar(id);
    }

    @PatchMapping("/{id}/indisponivel")
    public Vaga indisponivel(@PathVariable Long id) {
        return service.alterarIndisponibilidade(id, true);
    }

    @PatchMapping("/{id}/disponivel")
    public Vaga disponivel(@PathVariable Long id) {
        return service.alterarIndisponibilidade(id, false);
    }

    @GetMapping("/status/{status}")
    public List<Vaga> porStatus(@PathVariable StatusVaga status) {
        return service.listarPorStatus(status);
    }

    @GetMapping("/resumo")
    public ResumoVagas resumo() {
        return service.resumo();
    }
}