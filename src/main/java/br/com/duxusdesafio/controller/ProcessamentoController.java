package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.dto.IntegranteResponse;
import br.com.duxusdesafio.dto.TimeDaDataResponse;
import br.com.duxusdesafio.service.ProcessamentoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/processamento")
public class ProcessamentoController {

    private final ProcessamentoService processamentoService;

    public ProcessamentoController(ProcessamentoService processamentoService) {
        this.processamentoService = processamentoService;
    }

    @GetMapping("/time-da-data")
    public ResponseEntity<TimeDaDataResponse> timeDaData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        TimeDaDataResponse resposta = processamentoService.timeDaData(data);
        return resposta == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(resposta);
    }

    @GetMapping("/integrante-mais-usado")
    public ResponseEntity<IntegranteResponse> integranteMaisUsado(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        IntegranteResponse resposta = processamentoService.integranteMaisUsado(dataInicial, dataFinal);
        return resposta == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(resposta);
    }

    @GetMapping("/integrantes-do-time-mais-recorrente")
    public List<String> integrantesDoTimeMaisRecorrente(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return processamentoService.integrantesDoTimeMaisRecorrente(dataInicial, dataFinal);
    }

    @GetMapping("/funcao-mais-recorrente")
    public Map<String, String> funcaoMaisRecorrente(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return respostaUnica("Função", processamentoService.funcaoMaisRecorrente(dataInicial, dataFinal));
    }

    @GetMapping("/clube-mais-recorrente")
    public Map<String, String> clubeMaisRecorrente(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return respostaUnica("clube", processamentoService.clubeMaisRecorrente(dataInicial, dataFinal));
    }

    @GetMapping("/contagem-de-clubes")
    public Map<String, Long> contagemDeClubesNoPeriodo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return processamentoService.contagemDeClubesNoPeriodo(dataInicial, dataFinal);
    }

    @GetMapping("/contagem-por-funcao")
    public Map<String, Long> contagemPorFuncao(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
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