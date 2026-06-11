package br.com.duxusdesafio.dto;

import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Time;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeResponse {

    private final long id;
    private final String nomeDoClube;
    private final LocalDate data;
    private final List<IntegranteResponse> integrantes;

    public TimeResponse(Time time) {
        this.id = time.getId();
        this.nomeDoClube = time.getNomeDoClube();
        this.data = time.getData();
        this.integrantes = new ArrayList<>();

        if (time.getComposicaoTime() != null) {
            for (ComposicaoTime composicao : time.getComposicaoTime()) {
                if (composicao.getIntegrante() != null) {
                    integrantes.add(new IntegranteResponse(composicao.getIntegrante()));
                }
            }
        }
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

    public List<IntegranteResponse> getIntegrantes() {
        return integrantes;
    }
}