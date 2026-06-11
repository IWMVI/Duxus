package br.com.duxusdesafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(description = "Dados para cadastro de um integrante")
public class IntegranteEntrada {

    @Schema(description = "Nome do integrante", example = "Marta Silva")
    @NotBlank(message = "O nome deve ser informado.")
    private String nome;

    @Schema(description = "Função exercida no time", example = "Atacante")
    @NotBlank(message = "A função deve ser informada.")
    private String funcao;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }
}
