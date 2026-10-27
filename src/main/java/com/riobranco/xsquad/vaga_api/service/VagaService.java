package com.riobranco.xsquad.vaga_api.service;

import com.riobranco.xsquad.vaga_api.model.ResumoVagas;
import com.riobranco.xsquad.vaga_api.model.StatusVaga;
import com.riobranco.xsquad.vaga_api.model.Vaga;
import com.riobranco.xsquad.vaga_api.repository.VagaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VagaService {

    private final VagaRepository repository;

    public VagaService(VagaRepository repository) {
        this.repository = repository;
    }

    public Vaga criar(Vaga vaga) {
        validarDados(vaga);
        if (repository.existsByNomeVaga(vaga.getNomeVaga())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma vaga com esse nome");
        }
        vaga.setId(null);
        vaga.setStatus(StatusVaga.DISPONIVEL);   // toda vaga nasce disponível
        return repository.save(vaga);
    }

    public List<Vaga> listar() {
        return repository.findAll();
    }

    public Vaga buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaga não encontrada"));
    }

    public Vaga atualizar(Long id, Vaga dados) {
        Vaga vaga = buscarPorId(id);

        if (vaga.getStatus() == StatusVaga.OCUPADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível editar uma vaga ocupada");
        }
        validarDados(dados);
        if (repository.existsByNomeVagaAndIdNot(dados.getNomeVaga(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma vaga com esse nome");
        }

        vaga.setNomeVaga(dados.getNomeVaga());
        vaga.setPrecoHora(dados.getPrecoHora());
        return repository.save(vaga);
    }

    public void deletar(Long id) {
        Vaga vaga = buscarPorId(id);
        if (vaga.getStatus() == StatusVaga.OCUPADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir uma vaga ocupada");
        }
        repository.delete(vaga);
    }

    // ---- Controle de estado (chamado pela Reserva API no check-in/check-out) ----

    public Vaga ocupar(Long id) {
        Vaga vaga = buscarPorId(id);
        if (vaga.getStatus() != StatusVaga.DISPONIVEL) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Vaga não está disponível (status atual: " + vaga.getStatus() + ")");
        }
        vaga.setStatus(StatusVaga.OCUPADA);
        return repository.save(vaga);
    }

    public Vaga liberar(Long id) {
        Vaga vaga = buscarPorId(id);
        if (vaga.getStatus() != StatusVaga.OCUPADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Só é possível liberar uma vaga ocupada");
        }
        vaga.setStatus(StatusVaga.DISPONIVEL);
        return repository.save(vaga);
    }

    public Vaga alterarIndisponibilidade(Long id, boolean indisponivel) {
        Vaga vaga = buscarPorId(id);
        if (vaga.getStatus() == StatusVaga.OCUPADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vaga ocupada não pode mudar de disponibilidade");
        }
        vaga.setStatus(indisponivel ? StatusVaga.INDISPONIVEL : StatusVaga.DISPONIVEL);
        return repository.save(vaga);
    }

    // ---- Consultas ----

    public List<Vaga> listarPorStatus(StatusVaga status) {
        return repository.findByStatusVaga(status);
    }

    private void validarDados(Vaga vaga) {
        if (vaga.getNomeVaga() == null || vaga.getNomeVaga().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da vaga é obrigatório");
        }
        if (vaga.getPrecoHora() == null || vaga.getPrecoHora().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preço por hora deve ser maior que zero");
        }
    }

    public ResumoVagas resumo() {
    return new ResumoVagas(
            repository.count(),
            repository.countByStatusVaga(StatusVaga.DISPONIVEL),
            repository.countByStatusVaga(StatusVaga.OCUPADA),
            repository.countByStatusVaga(StatusVaga.INDISPONIVEL));
}
}