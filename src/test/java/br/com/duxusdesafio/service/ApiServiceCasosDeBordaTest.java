package br.com.duxusdesafio.service;

import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Time;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ApiServiceCasosDeBordaTest {

    private ApiService apiService;
    private List<Time> todosOsTimes;

    @Before
    public void preparar() {
        apiService = new ApiService();
        todosOsTimes = new DadosParaTesteApiService().getTodosOsTimes();
    }

    @Test
    public void deveConsiderarPeriodoComLimitesNulos() {
        assertEquals("Chicago Bulls", apiService.clubeMaisRecorrente(null, null, todosOsTimes));
        assertEquals("Chicago Bulls", apiService.clubeMaisRecorrente(null, LocalDate.of(1994, 1, 1), todosOsTimes));
        assertEquals("Chicago Bulls", apiService.clubeMaisRecorrente(LocalDate.of(1994, 1, 1), null, todosOsTimes));
    }

    @Test
    public void deveRetornarResultadosVaziosQuandoNaoHaTimes() {
        assertNull(apiService.timeDaData(LocalDate.now(), Collections.emptyList()));
        assertNull(apiService.integranteMaisUsado(null, null, Collections.emptyList()));
        assertNull(apiService.funcaoMaisRecorrente(null, null, Collections.emptyList()));
        assertNull(apiService.clubeMaisRecorrente(null, null, Collections.emptyList()));
        assertTrue(apiService.integrantesDoTimeMaisRecorrente(null, null, Collections.emptyList()).isEmpty());
        assertTrue(apiService.contagemDeClubesNoPeriodo(null, null, Collections.emptyList()).isEmpty());
        assertTrue(apiService.contagemPorFuncao(null, null, Collections.emptyList()).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveRejeitarPeriodoInvertido() {
        apiService.contagemPorFuncao(
                LocalDate.of(1995, 1, 1),
                LocalDate.of(1993, 1, 1),
                todosOsTimes
        );
    }

    @Test
    public void deveContarCadaIntegranteUmaVezPorFuncao() {
        assertEquals(Long.valueOf(2), apiService.contagemPorFuncao(null, null, todosOsTimes).get("ala"));
        assertEquals(Long.valueOf(1), apiService.contagemPorFuncao(null, null, todosOsTimes).get("ala-pivô"));
    }

    @Test
    public void deveContarAparicoesDoIntegranteNosTimes() {
        Integrante maisUsado = apiService.integranteMaisUsado(null, null, todosOsTimes);

        assertNotNull(maisUsado);
        assertEquals("Denis Rodman", maisUsado.getNome());
    }

    @Test
    public void deveContarIntegranteNoMaximoUmaVezPorTime() {
        Integrante repetido = integrante(10L, "Repetido", "ala");
        Integrante recorrente = integrante(20L, "Recorrente", "pivô");

        Time primeiro = time(
                "Clube A",
                LocalDate.of(2024, 1, 1),
                repetido,
                repetido,
                recorrente
        );
        Time segundo = time("Clube B", LocalDate.of(2024, 1, 8), recorrente);

        assertEquals(
                recorrente,
                apiService.integranteMaisUsado(null, null, Arrays.asList(primeiro, segundo))
        );
    }

    @Test
    public void deveDistinguirFormacoesComIntegrantesHomônimos() {
        Integrante primeiroAlex = integrante(10L, "Alex", "ataque");
        Integrante segundoAlex = integrante(20L, "Alex", "defesa");
        Integrante bia = integrante(30L, "Bia", "meio");

        Time formacaoMaisRecorrente1 = time(
                "Clube A",
                LocalDate.of(2024, 1, 1),
                primeiroAlex,
                bia
        );
        Time outraFormacao = time(
                "Clube A",
                LocalDate.of(2024, 1, 8),
                segundoAlex,
                bia
        );
        Time formacaoMaisRecorrente2 = time(
                "Clube A",
                LocalDate.of(2024, 1, 15),
                primeiroAlex,
                bia
        );

        assertEquals(
                Arrays.asList("Alex", "Bia"),
                apiService.integrantesDoTimeMaisRecorrente(
                        null,
                        null,
                        Arrays.asList(formacaoMaisRecorrente1, outraFormacao, formacaoMaisRecorrente2)
                )
        );
    }

    @Test
    public void deveConsiderarOsLimitesDoPeriodoComoInclusivos() {
        assertEquals(
                Long.valueOf(1),
                apiService.contagemDeClubesNoPeriodo(
                        LocalDate.of(1993, 1, 1),
                        LocalDate.of(1993, 1, 1),
                        todosOsTimes
                ).get("Detroit Pistons")
        );
    }

    private Integrante integrante(long id, String nome, String funcao) {
        Integrante integrante = new Integrante();
        integrante.setId(id);
        integrante.setNome(nome);
        integrante.setFuncao(funcao);
        return integrante;
    }

    private Time time(String clube, LocalDate data, Integrante... integrantes) {
        Time time = new Time();
        time.setNomeDoClube(clube);
        time.setData(data);
        time.setComposicaoTime(
                Arrays.stream(integrantes)
                        .map(integrante -> new ComposicaoTime(time, integrante))
                        .collect(java.util.stream.Collectors.toList())
        );
        return time;
    }
}
