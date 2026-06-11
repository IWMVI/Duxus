package br.com.duxusdesafio.service;

import br.com.duxusdesafio.dto.IntegranteEntrada;
import br.com.duxusdesafio.dto.IntegranteResposta;
import br.com.duxusdesafio.dto.TimeEntrada;
import br.com.duxusdesafio.dto.TimeResposta;
import br.com.duxusdesafio.exception.RecursoNaoEncontradoException;
import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.repository.IntegranteRepository;
import br.com.duxusdesafio.repository.TimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CadastroService {

    private final IntegranteRepository integranteRepository;
    private final TimeRepository timeRepository;

    public CadastroService(IntegranteRepository integranteRepository, TimeRepository timeRepository) {
        this.integranteRepository = integranteRepository;
        this.timeRepository = timeRepository;
    }

    @Transactional
    public IntegranteResposta cadastrarIntegrante(IntegranteEntrada entrada) {
        Integrante integrante = new Integrante();
        integrante.setNome(entrada.getNome().trim());
        integrante.setFuncao(entrada.getFuncao().trim());

        return new IntegranteResposta(integranteRepository.save(integrante));
    }

    @Transactional(readOnly = true)
    public List<IntegranteResposta> listarIntegrantes() {
        return integranteRepository.findAll()
                .stream()
                .map(IntegranteResposta::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public TimeResposta cadastrarTime(TimeEntrada entrada) {
        if (timeRepository.existsByData(entrada.getData())) {
            throw new IllegalArgumentException(
                    "Já existe um time cadastrado para a data " + entrada.getData() + "."
            );
        }

        List<Long> idsSolicitados = new ArrayList<>(new LinkedHashSet<>(entrada.getIntegrantesIds()));
        Map<Long, Integrante> integrantesPorId = integranteRepository.findAllById(idsSolicitados)
                .stream()
                .collect(Collectors.toMap(Integrante::getId, Function.identity()));

        List<Long> idsInexistentes = idsSolicitados.stream()
                .filter(id -> !integrantesPorId.containsKey(id))
                .collect(Collectors.toList());

        if (!idsInexistentes.isEmpty()) {
            throw new RecursoNaoEncontradoException(
                    "Integrantes não encontrados para os IDs: " + idsInexistentes
            );
        }

        Time time = new Time();
        time.setNomeDoClube(entrada.getNomeDoClube().trim());
        time.setData(entrada.getData());

        List<ComposicaoTime> composicao = idsSolicitados.stream()
                .map(integrantesPorId::get)
                .map(integrante -> new ComposicaoTime(time, integrante))
                .collect(Collectors.toList());
        time.setComposicaoTime(composicao);

        return new TimeResposta(timeRepository.save(time));
    }

    @Transactional(readOnly = true)
    public List<TimeResposta> listarTimes() {
        return timeRepository.findAll()
                .stream()
                .map(TimeResposta::new)
                .collect(Collectors.toList());
    }
}
