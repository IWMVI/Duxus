package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.dto.IntegranteResposta;
import br.com.duxusdesafio.dto.TimeDaDataResposta;
import br.com.duxusdesafio.service.ProcessamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/processamento")
@Tag(name = "Análises", description = "Processamento e análise do histórico de escalações")
public class ProcessamentoController {

    private final ProcessamentoService processamentoService;

    public ProcessamentoController(ProcessamentoService processamentoService) {
        this.processamentoService = processamentoService;
    }

    @GetMapping("/time-da-data")
    @Operation(summary = "Consultar time por data", description = "Retorna a escalação registrada na data informada.")
    @ApiResponse(responseCode = "200", description = "Time encontrado")
    @ApiResponse(responseCode = "404", description = "Nenhum time encontrado na data")
    public ResponseEntity<TimeDaDataResposta> timeDaData(
            @Parameter(description = "Data da escalação", example = "2026-06-11", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        return ResponseEntity.of(Optional.ofNullable(processamentoService.timeDaData(data)));
    }

    @GetMapping("/integrante-mais-usado")
    @Operation(summary = "Consultar integrante mais escalado")
    public ResponseEntity<IntegranteResposta> integranteMaisUsado(
            @Parameter(description = "Início inclusivo do período", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Fim inclusivo do período", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return ResponseEntity.of(Optional.ofNullable(
                processamentoService.integranteMaisUsado(dataInicial, dataFinal)
        ));
    }

    @GetMapping("/integrantes-do-time-mais-recorrente")
    @Operation(summary = "Consultar formação mais recorrente")
    public List<String> integrantesDoTimeMaisRecorrente(
            @Parameter(description = "Início inclusivo do período", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Fim inclusivo do período", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return processamentoService.integrantesDoTimeMaisRecorrente(dataInicial, dataFinal);
    }

    @GetMapping("/funcao-mais-recorrente")
    @Operation(summary = "Consultar função mais recorrente")
    public Map<String, String> funcaoMaisRecorrente(
            @Parameter(description = "Início inclusivo do período", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Fim inclusivo do período", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return respostaUnica("Função", processamentoService.funcaoMaisRecorrente(dataInicial, dataFinal));
    }

    @GetMapping("/clube-mais-recorrente")
    @Operation(summary = "Consultar clube mais recorrente")
    public Map<String, String> clubeMaisRecorrente(
            @Parameter(description = "Início inclusivo do período", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Fim inclusivo do período", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return respostaUnica("clube", processamentoService.clubeMaisRecorrente(dataInicial, dataFinal));
    }

    @GetMapping("/contagem-de-clubes")
    @Operation(summary = "Contar participações por clube")
    public Map<String, Long> contagemDeClubesNoPeriodo(
            @Parameter(description = "Início inclusivo do período", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Fim inclusivo do período", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return processamentoService.contagemDeClubesNoPeriodo(dataInicial, dataFinal);
    }

    @GetMapping("/contagem-por-funcao")
    @Operation(summary = "Contar integrantes por função")
    public Map<String, Long> contagemPorFuncao(
            @Parameter(description = "Início inclusivo do período", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Fim inclusivo do período", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return processamentoService.contagemPorFuncao(dataInicial, dataFinal);
    }

    private Map<String, String> respostaUnica(String chave, String valor) {
        Map<String, String> resposta = new LinkedHashMap<>();
        resposta.put(chave, valor);
        return resposta;
    }
}
