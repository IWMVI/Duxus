package br.com.duxusdesafio.dto;

import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Time;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeDaDataResponse {

    private final LocalDate data;
    private final String clube;
    private final List<String> integrantes;

    public TimeDaDataResponse(Time time) {
        this.data = time.getData();
        this.clube = time.getNomeDoClube();
        this.integrantes = new ArrayList<>();

        if (time.getComposicaoTime() != null) {
            for (ComposicaoTime composicao : time.getComposicaoTime()) {
                if (composicao.getIntegrante() != null) {
                    integrantes.add(composicao.getIntegrante().getNome());
                }
            }
        }
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