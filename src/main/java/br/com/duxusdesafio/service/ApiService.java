package br.com.duxusdesafio.service;

import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Service que possuirá as regras de negócio para o processamento dos dados
 * solicitados no desafio!
 *
 * OBS ao candidato: PREFERENCIALMENTE, NÃO ALTERE AS ASSINATURAS DOS MÉTODOS!
 * Trabalhe com a proposta pura.
 *
 * @author carlosau
 */
@Service
public class ApiService {

    /**
     * Vai retornar um Time, com a composição do time daquela data
     */
    public Time timeDaData(LocalDate data, List<Time> todosOsTimes) {
        if (data == null) {
            throw new IllegalArgumentException("A data deve ser informada.");
        }

        for (Time time : timesValidos(todosOsTimes)) {
            if (data.equals(time.getData())) {
                return time;
            }
        }

        return null;
    }

    /**
     * Vai retornar o integrante que estiver presente na maior quantidade de times
     * dentro do período
     */
    public Integrante integranteMaisUsado(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<Integrante, Long> aparicoes = new LinkedHashMap<>();

        for (Time time : timesNoPeriodo(dataInicial, dataFinal, todosOsTimes)) {
            // cada integrante conta no máximo uma vez por time
            Set<Integrante> integrantesNoTime = new LinkedHashSet<>(integrantesDoTime(time));
            for (Integrante integrante : integrantesNoTime) {
                Long total = aparicoes.get(integrante);
                aparicoes.put(integrante, total == null ? 1L : total + 1L);
            }
        }

        return chaveMaisRecorrente(aparicoes);
    }

    /**
     * Vai retornar uma lista com os nomes dos integrantes do time mais recorrente dentro do período.
     * OBS: Time é o clube + composição em determinada data
     */
    public List<String> integrantesDoTimeMaisRecorrente(
            LocalDate dataInicial,
            LocalDate dataFinal,
            List<Time> todosOsTimes
    ) {
        Map<String, Long> aparicoes = new LinkedHashMap<>();
        Map<String, List<String>> nomesPorFormacao = new LinkedHashMap<>();

        for (Time time : timesNoPeriodo(dataInicial, dataFinal, todosOsTimes)) {
            String formacao = chaveDaFormacao(time);

            Long total = aparicoes.get(formacao);
            aparicoes.put(formacao, total == null ? 1L : total + 1L);

            if (!nomesPorFormacao.containsKey(formacao)) {
                nomesPorFormacao.put(formacao, nomesDosIntegrantes(time));
            }
        }

        String timeMaisRecorrente = chaveMaisRecorrente(aparicoes);
        return timeMaisRecorrente == null
                ? Collections.<String>emptyList()
                : nomesPorFormacao.get(timeMaisRecorrente);
    }

    /**
     * Vai retornar a função mais recorrente nos times dentro do período
     */
    public String funcaoMaisRecorrente(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        return chaveMaisRecorrente(contagemPorFuncao(dataInicial, dataFinal, todosOsTimes));
    }

    /**
     * Vai retornar o nome do Clube mais comum dentro do período
     */
    public String clubeMaisRecorrente(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        return chaveMaisRecorrente(contagemDeClubesNoPeriodo(dataInicial, dataFinal, todosOsTimes));
    }

    /**
     * Vai retornar o número (quantidade) de aparições de cada Clube participante no período
     */
    public Map<String, Long> contagemDeClubesNoPeriodo(
            LocalDate dataInicial,
            LocalDate dataFinal,
            List<Time> todosOsTimes
    ) {
        Map<String, Long> contagem = new LinkedHashMap<>();

        for (Time time : timesNoPeriodo(dataInicial, dataFinal, todosOsTimes)) {
            String clube = time.getNomeDoClube();
            if (clube == null) {
                continue;
            }

            Long total = contagem.get(clube);
            contagem.put(clube, total == null ? 1L : total + 1L);
        }

        return contagem;
    }

    /**
     * Vai retornar o número (quantidade) de Funções dentro do período.
     * Dica - pense sobre repetições!
     */
    public Map<String, Long> contagemPorFuncao(
            LocalDate dataInicial,
            LocalDate dataFinal,
            List<Time> todosOsTimes
    ) {
        Map<String, Long> contagem = new LinkedHashMap<>();
        Set<Long> integrantesJaContados = new HashSet<>();

        for (Time time : timesNoPeriodo(dataInicial, dataFinal, todosOsTimes)) {
            for (Integrante integrante : integrantesDoTime(time)) {
                String funcao = integrante.getFuncao();
                if (funcao == null || !integrantesJaContados.add(integrante.getId())) {
                    continue;
                }

                Long total = contagem.get(funcao);
                contagem.put(funcao, total == null ? 1L : total + 1L);
            }
        }

        return contagem;
    }

    /**
     * Retorna os times que caem dentro do período informado (limites inclusivos).
     */
    private List<Time> timesNoPeriodo(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        validarPeriodo(dataInicial, dataFinal);

        List<Time> times = new ArrayList<>();
        for (Time time : timesValidos(todosOsTimes)) {
            if (estaNoPeriodo(time.getData(), dataInicial, dataFinal)) {
                times.add(time);
            }
        }

        return times;
    }

    private List<Time> timesValidos(List<Time> todosOsTimes) {
        if (todosOsTimes == null) {
            return Collections.emptyList();
        }

        List<Time> times = new ArrayList<>();
        for (Time time : todosOsTimes) {
            if (time != null) {
                times.add(time);
            }
        }

        return times;
    }

    private List<Integrante> integrantesDoTime(Time time) {
        List<Integrante> integrantes = new ArrayList<>();
        for (ComposicaoTime composicao : composicaoDoTime(time)) {
            if (composicao.getIntegrante() != null) {
                integrantes.add(composicao.getIntegrante());
            }
        }

        return integrantes;
    }

    private List<ComposicaoTime> composicaoDoTime(Time time) {
        if (time == null || time.getComposicaoTime() == null) {
            return Collections.emptyList();
        }

        List<ComposicaoTime> composicao = new ArrayList<>();
        for (ComposicaoTime item : time.getComposicaoTime()) {
            if (item != null) {
                composicao.add(item);
            }
        }

        return composicao;
    }

    /**
     * Gera uma chave única para a formação de um time (clube + integrantes).
     * É por ela que identificamos quando um "mesmo time" repete ao longo do tempo.
     */
    private String chaveDaFormacao(Time time) {
        StringBuilder chave = new StringBuilder(time.getNomeDoClube());

        Set<Long> ids = new TreeSet<>();
        for (Integrante integrante : integrantesDoTime(time)) {
            ids.add(integrante.getId());
        }
        for (Long id : ids) {
            chave.append('#').append(id);
        }

        return chave.toString();
    }

    private List<String> nomesDosIntegrantes(Time time) {
        List<String> nomes = new ArrayList<>();
        Set<Long> ids = new HashSet<>();

        for (Integrante integrante : integrantesDoTime(time)) {
            if (integrante.getNome() == null || !ids.add(integrante.getId())) {
                continue;
            }
            nomes.add(integrante.getNome());
        }

        Collections.sort(nomes);
        return nomes;
    }

    private boolean estaNoPeriodo(LocalDate data, LocalDate dataInicial, LocalDate dataFinal) {
        if (data == null) {
            return false;
        }

        boolean depoisDoInicio = dataInicial == null || !data.isBefore(dataInicial);
        boolean antesDoFim = dataFinal == null || !data.isAfter(dataFinal);
        return depoisDoInicio && antesDoFim;
    }

    private void validarPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial != null && dataFinal != null && dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }
    }

    /**
     * Retorna a chave com maior contagem. Em caso de empate, vale a chave
     * "menor" (ordem alfabética do próprio valor).
     */
    private <T> T chaveMaisRecorrente(Map<T, Long> contagem) {
        T vencedor = null;
        Long maiorContagem = null;

        for (Map.Entry<T, Long> entrada : contagem.entrySet()) {
            if (vencedor == null || vence(entrada.getKey(), entrada.getValue(), vencedor, maiorContagem)) {
                vencedor = entrada.getKey();
                maiorContagem = entrada.getValue();
            }
        }

        return vencedor;
    }

    private <T> boolean vence(T candidato, Long contagemCandidato, T atual, Long contagemAtual) {
        int comparacao = contagemCandidato.compareTo(contagemAtual);
        if (comparacao != 0) {
            return comparacao > 0;
        }
        return candidato.toString().compareTo(atual.toString()) < 0;
    }
}