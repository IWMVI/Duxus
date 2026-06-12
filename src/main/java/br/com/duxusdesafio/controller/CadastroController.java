package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.dto.IntegranteEntrada;
import br.com.duxusdesafio.dto.IntegranteResposta;
import br.com.duxusdesafio.dto.TimeEntrada;
import br.com.duxusdesafio.dto.TimeResposta;
import br.com.duxusdesafio.exception.ErroResposta;
import br.com.duxusdesafio.service.CadastroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Cadastros", description = "Cadastro e consulta de integrantes e times")
public class CadastroController {

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @PostMapping("/integrantes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Cadastrar integrante",
            description = "Cadastra uma pessoa que poderá participar da composição dos times."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Integrante cadastrado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados do integrante inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResposta.class))
            )
    })
    public IntegranteResposta cadastrarIntegrante(@Valid @RequestBody IntegranteEntrada entrada) {
        return cadastroService.cadastrarIntegrante(entrada);
    }

    @GetMapping("/integrantes")
    @Operation(summary = "Listar integrantes", description = "Retorna todos os integrantes cadastrados.")
    @ApiResponse(responseCode = "200", description = "Integrantes consultados")
    public List<IntegranteResposta> listarIntegrantes() {
        return cadastroService.listarIntegrantes();
    }

    @PostMapping("/times")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Montar time",
            description = "Registra um time para um clube e uma data, usando os IDs dos integrantes cadastrados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Time cadastrado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados do time inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResposta.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Um ou mais integrantes não foram encontrados",
                    content = @Content(schema = @Schema(implementation = ErroResposta.class))
            )
    })
    public TimeResposta cadastrarTime(@Valid @RequestBody TimeEntrada entrada) {
        return cadastroService.cadastrarTime(entrada);
    }

    @GetMapping("/times")
    @Operation(summary = "Listar times", description = "Retorna todas as escalações cadastradas.")
    @ApiResponse(responseCode = "200", description = "Times consultados")
    public List<TimeResposta> listarTimes() {
        return cadastroService.listarTimes();
    }
}
