package br.com.duxusdesafio.dto;

import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Time;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimeResposta {

    private final long id;
    private final String nomeDoClube;
    private final LocalDate data;
    private final List<IntegranteResposta> integrantes;

    public TimeResposta(Time time) {
        this.id = time.getId();
        this.nomeDoClube = time.getNomeDoClube();
        this.data = time.getData();
        this.integrantes = Optional.ofNullable(time.getComposicaoTime())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ComposicaoTime::getIntegrante)
                .map(IntegranteResposta::new)
                .collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public String getNomeDoClube() {
        return nomeDoClube;
    }

    public LocalDate getData() {
        return data;
    }

    public List<IntegranteResposta> getIntegrantes() {
        return integrantes;
    }
}
