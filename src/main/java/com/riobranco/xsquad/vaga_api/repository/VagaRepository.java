package com.riobranco.xsquad.vaga_api.repository;

import com.riobranco.xsquad.vaga_api.model.StatusVaga;
import com.riobranco.xsquad.vaga_api.model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {

    List<Vaga> findByStatusVaga(StatusVaga statusVaga);

    boolean existsByNomeVaga(String nomeVaga);

    boolean existsByNomeVagaAndIdNot(String nomeVaga, Long id);

    long countByStatusVaga(StatusVaga statusVaga);
}