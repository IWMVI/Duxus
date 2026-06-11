package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.dto.IntegranteRequest;
import br.com.duxusdesafio.dto.IntegranteResponse;
import br.com.duxusdesafio.dto.TimeRequest;
import br.com.duxusdesafio.dto.TimeResponse;
import br.com.duxusdesafio.service.CadastroService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CadastroController {

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @PostMapping("/integrantes")
    @ResponseStatus(HttpStatus.CREATED)
    public IntegranteResponse cadastrarIntegrante(@Valid @RequestBody IntegranteRequest request) {
        return cadastroService.cadastrarIntegrante(request);
    }

    @GetMapping("/integrantes")
    public List<IntegranteResponse> listarIntegrantes() {
        return cadastroService.listarIntegrantes();
    }

    @PostMapping("/times")
    @ResponseStatus(HttpStatus.CREATED)
    public TimeResponse cadastrarTime(@Valid @RequestBody TimeRequest request) {
        return cadastroService.cadastrarTime(request);
    }

    @GetMapping("/times")
    public List<TimeResponse> listarTimes() {
        return cadastroService.listarTimes();
    }
}