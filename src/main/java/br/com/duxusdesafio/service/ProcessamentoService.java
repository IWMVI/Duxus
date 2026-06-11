package br.com.duxusdesafio.service;

import br.com.duxusdesafio.dto.IntegranteResposta;
import br.com.duxusdesafio.dto.TimeDaDataResposta;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.repository.TimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProcessamentoService {

    private final ApiService apiService;
    private final TimeRepository timeRepository;

    public ProcessamentoService(ApiService apiService, TimeRepository timeRepository) {
        this.apiService = apiService;
        this.timeRepository = timeRepository;
    }

    public TimeDaDataResposta timeDaData(LocalDate data) {
        Time time = apiService.timeDaData(data, todosOsTimes());
        return time == null ? null : new TimeDaDataResposta(time);
    }

    public IntegranteResposta integranteMaisUsado(LocalDate dataInicial, LocalDate dataFinal) {
        Integrante integrante = apiService.integranteMaisUsado(dataInicial, dataFinal, todosOsTimes());
        return integrante == null ? null : new IntegranteResposta(integrante);
    }

    public List<String> integrantesDoTimeMaisRecorrente(LocalDate dataInicial, LocalDate dataFinal) {
        return apiService.integrantesDoTimeMaisRecorrente(dataInicial, dataFinal, todosOsTimes());
    }

    public String funcaoMaisRecorrente(LocalDate dataInicial, LocalDate dataFinal) {
        return apiService.funcaoMaisRecorrente(dataInicial, dataFinal, todosOsTimes());
    }

    public String clubeMaisRecorrente(LocalDate dataInicial, LocalDate dataFinal) {
        return apiService.clubeMaisRecorrente(dataInicial, dataFinal, todosOsTimes());
    }

    public Map<String, Long> contagemDeClubesNoPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        return apiService.contagemDeClubesNoPeriodo(dataInicial, dataFinal, todosOsTimes());
    }

    public Map<String, Long> contagemPorFuncao(LocalDate dataInicial, LocalDate dataFinal) {
        return apiService.contagemPorFuncao(dataInicial, dataFinal, todosOsTimes());
    }

    private List<Time> todosOsTimes() {
        return timeRepository.findAll();
    }
}
