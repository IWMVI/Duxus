package br.com.duxusdesafio.service;

import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        return timesValidos(todosOsTimes)
                .filter(time -> data.equals(time.getData()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Vai retornar o integrante que estiver presente na maior quantidade de times
     * dentro do período
     */
    public Integrante integranteMaisUsado(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<Integrante, Long> aparicoes = timesNoPeriodo(dataInicial, dataFinal, todosOsTimes)
                .flatMap(time -> integrantesDoTime(time).distinct())
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

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
        Map<IdentidadeDoTime, Long> aparicoes = timesNoPeriodo(dataInicial, dataFinal, todosOsTimes)
                .collect(Collectors.groupingBy(
                        IdentidadeDoTime::new,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        IdentidadeDoTime timeMaisRecorrente = chaveMaisRecorrente(aparicoes);
        return timeMaisRecorrente == null
                ? Collections.emptyList()
                : new ArrayList<>(timeMaisRecorrente.nomesDosIntegrantes);
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
        return timesNoPeriodo(dataInicial, dataFinal, todosOsTimes)
                .map(Time::getNomeDoClube)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
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
        return integrantesNoPeriodo(dataInicial, dataFinal, todosOsTimes)
                .distinct()
                .map(Integrante::getFuncao)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    private Stream<Time> timesNoPeriodo(
            LocalDate dataInicial,
            LocalDate dataFinal,
            List<Time> todosOsTimes
    ) {
        validarPeriodo(dataInicial, dataFinal);

        return timesValidos(todosOsTimes)
                .filter(time -> estaNoPeriodo(time.getData(), dataInicial, dataFinal));
    }

    private Stream<Time> timesValidos(List<Time> todosOsTimes) {
        return Optional.ofNullable(todosOsTimes)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull);
    }

    private Stream<Integrante> integrantesNoPeriodo(
            LocalDate dataInicial,
            LocalDate dataFinal,
            List<Time> todosOsTimes
    ) {
        return timesNoPeriodo(dataInicial, dataFinal, todosOsTimes)
                .flatMap(this::integrantesDoTime);
    }

    private Stream<Integrante> integrantesDoTime(Time time) {
        return Optional.ofNullable(time.getComposicaoTime())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .map(composicao -> composicao.getIntegrante())
                .filter(Objects::nonNull);
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

    private <T> T chaveMaisRecorrente(Map<T, Long> contagem) {
        return contagem.entrySet()
                .stream()
                .reduce((a, b) -> {
                    int cmp = a.getValue().compareTo(b.getValue());
                    if (cmp != 0) {
                        return cmp > 0 ? a : b;
                    }
                    return a.getKey().toString().compareTo(b.getKey().toString()) <= 0 ? a : b;
                })
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static final class IdentidadeDoTime {

        private final String clube;
        private final List<Integrante> integrantes;
        private final List<String> nomesDosIntegrantes;

        private IdentidadeDoTime(Time time) {
            this.clube = time.getNomeDoClube();
            this.integrantes = Optional.ofNullable(time.getComposicaoTime())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(composicao -> composicao.getIntegrante())
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted(Comparator
                            .comparingLong(Integrante::getId)
                            .thenComparing(Integrante::getNome, Comparator.nullsFirst(String::compareTo))
                            .thenComparing(Integrante::getFuncao, Comparator.nullsFirst(String::compareTo)))
                    .collect(Collectors.toList());
            this.nomesDosIntegrantes = integrantes.stream()
                    .map(Integrante::getNome)
                    .filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "IdentidadeDoTime{clube='" + clube + "', integrantes=" + integrantes + "}";
        }

        @Override
        public boolean equals(Object objeto) {
            if (this == objeto) {
                return true;
            }
            if (!(objeto instanceof IdentidadeDoTime)) {
                return false;
            }
            IdentidadeDoTime outro = (IdentidadeDoTime) objeto;
            return Objects.equals(clube, outro.clube)
                    && Objects.equals(integrantes, outro.integrantes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clube, integrantes);
        }
    }

}
