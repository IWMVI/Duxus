package br.com.duxusdesafio.dto;

import br.com.duxusdesafio.model.Integrante;

public class IntegranteResposta {

    private final long id;
    private final String nome;
    private final String funcao;

    public IntegranteResposta(Integrante integrante) {
        this.id = integrante.getId();
        this.nome = integrante.getNome();
        this.funcao = integrante.getFuncao();
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getFuncao() {
        return funcao;
    }
}
