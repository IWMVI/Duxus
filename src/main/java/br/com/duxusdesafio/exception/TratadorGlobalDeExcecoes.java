package br.com.duxusdesafio.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratadorGlobalDeExcecoes {

    private static final Map<String, String> NOMES_DOS_CAMPOS = nomesDosCampos();

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException excecao) {
        return resposta(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                Collections.singletonList(excecao.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResposta> tratarArgumentoInvalido(IllegalArgumentException excecao) {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "Solicitação inválida",
                Collections.singletonList(excecao.getMessage())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratarViolacaoDeIntegridade() {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "Solicitação inválida",
                Collections.singletonList("Já existe um time cadastrado para a data informada.")
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException excecao) {
        List<String> mensagens = excecao.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> nomeDoCampo(erro.getField()) + ": " + erro.getDefaultMessage())
                .collect(Collectors.toList());

        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos", mensagens);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> tratarTipoDeParametroInvalido(
            MethodArgumentTypeMismatchException excecao
    ) {
        String campo = nomeDoCampo(excecao.getName());
        String mensagem = campo + " possui um valor inválido.";

        if (excecao.getRequiredType() != null
                && java.time.LocalDate.class.isAssignableFrom(excecao.getRequiredType())) {
            mensagem = campo + " deve ser uma data válida.";
        }

        return resposta(
                HttpStatus.BAD_REQUEST,
                "Parâmetro inválido",
                Collections.singletonList(mensagem)
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResposta> tratarParametroObrigatorioAusente(
            MissingServletRequestParameterException excecao
    ) {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "Parâmetro obrigatório ausente",
                Collections.singletonList(nomeDoCampo(excecao.getParameterName()) + " deve ser informada.")
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> tratarCorpoDaRequisicaoInvalido(
            HttpMessageNotReadableException excecao
    ) {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "Dados inválidos",
                Collections.singletonList(
                        "Não foi possível interpretar os dados enviados. Verifique os campos e as datas informadas."
                )
        );
    }

    private ResponseEntity<ErroResposta> resposta(
            HttpStatus status,
            String titulo,
            List<String> mensagens
    ) {
        return ResponseEntity.status(status)
                .body(new ErroResposta(status.value(), titulo, mensagens));
    }

    private String nomeDoCampo(String campo) {
        return NOMES_DOS_CAMPOS.getOrDefault(campo, campo);
    }

    private static Map<String, String> nomesDosCampos() {
        Map<String, String> nomes = new LinkedHashMap<>();
        nomes.put("nome", "Nome");
        nomes.put("funcao", "Função");
        nomes.put("nomeDoClube", "Nome do clube");
        nomes.put("data", "Data");
        nomes.put("dataInicial", "Data inicial");
        nomes.put("dataFinal", "Data final");
        nomes.put("integrantesIds", "Integrantes");
        return Collections.unmodifiableMap(nomes);
    }
}
