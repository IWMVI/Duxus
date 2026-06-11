package br.com.duxusdesafio.dto;

import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Time;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimeDaDataResposta {

    private final LocalDate data;
    private final String clube;
    private final List<String> integrantes;

    public TimeDaDataResposta(Time time) {
        this.data = time.getData();
        this.clube = time.getNomeDoClube();
        this.integrantes = Optional.ofNullable(time.getComposicaoTime())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ComposicaoTime::getIntegrante)
                .map(integrante -> integrante.getNome())
                .collect(Collectors.toList());
    }

    public LocalDate getData() {
        return data;
    }

    public String getClube() {
        return clube;
    }

    public List<String> getIntegrantes() {
        return integrantes;
    }
}
