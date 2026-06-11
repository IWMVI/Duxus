package br.com.duxusdesafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Dados para montagem de um time")
public class TimeEntrada {

    @Schema(description = "Nome do clube", example = "Estrelas FC")
    @NotBlank(message = "O nome do clube deve ser informado.")
    private String nomeDoClube;

    @Schema(description = "Data da escalação no padrão ISO", example = "2026-06-11", type = "string", format = "date")
    @NotNull(message = "A data deve ser informada.")
    private LocalDate data;

    @Schema(description = "IDs dos integrantes da formação", example = "[1, 2, 3]")
    @NotEmpty(message = "O time deve possuir ao menos um integrante.")
    private List<@NotNull(message = "O ID do integrante não pode ser nulo.") Long> integrantesIds = new ArrayList<>();

    public String getNomeDoClube() {
        return nomeDoClube;
    }

    public void setNomeDoClube(String nomeDoClube) {
        this.nomeDoClube = nomeDoClube;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<Long> getIntegrantesIds() {
        return integrantesIds;
    }

    public void setIntegrantesIds(List<Long> integrantesIds) {
        this.integrantesIds = integrantesIds;
    }
}
