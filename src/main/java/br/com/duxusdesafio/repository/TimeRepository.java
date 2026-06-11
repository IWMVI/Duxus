package br.com.duxusdesafio.repository;

import br.com.duxusdesafio.model.Time;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimeRepository extends JpaRepository<Time, Long> {

    @Override
    @EntityGraph(attributePaths = {"composicaoTime", "composicaoTime.integrante"})
    List<Time> findAll();

    boolean existsByData(LocalDate data);
}
