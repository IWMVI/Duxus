package br.com.duxusdesafio.service;

import br.com.duxusdesafio.dto.IntegranteRequest;
import br.com.duxusdesafio.dto.IntegranteResponse;
import br.com.duxusdesafio.dto.TimeRequest;
import br.com.duxusdesafio.dto.TimeResponse;
import br.com.duxusdesafio.exception.RecursoNaoEncontradoException;
import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.repository.IntegranteRepository;
import br.com.duxusdesafio.repository.TimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CadastroService {

    private final IntegranteRepository integranteRepository;
    private final TimeRepository timeRepository;

    public CadastroService(IntegranteRepository integranteRepository, TimeRepository timeRepository) {
        this.integranteRepository = integranteRepository;
        this.timeRepository = timeRepository;
    }

    @Transactional
    public IntegranteResponse cadastrarIntegrante(IntegranteRequest request) {
        Integrante integrante = new Integrante();
        integrante.setNome(request.getNome().trim());
        integrante.setFuncao(request.getFuncao().trim());

        return new IntegranteResponse(integranteRepository.save(integrante));
    }

    @Transactional(readOnly = true)
    public List<IntegranteResponse> listarIntegrantes() {
        List<IntegranteResponse> respostas = new ArrayList<>();
        for (Integrante integrante : integranteRepository.findAll()) {
            respostas.add(new IntegranteResponse(integrante));
        }

        return respostas;
    }

    @Transactional
    public TimeResponse cadastrarTime(TimeRequest request) {
        if (timeRepository.existsByData(request.getData())) {
            throw new IllegalArgumentException(
                    "Já existe um time cadastrado para a data " + request.getData() + "."
            );
        }

        // remove ids repetidos mantendo a ordem informada
        List<Long> idsSolicitados = new ArrayList<>();
        for (Long id : request.getIntegrantesIds()) {
            if (!idsSolicitados.contains(id)) {
                idsSolicitados.add(id);
            }
        }

        Map<Long, Integrante> integrantesPorId = new HashMap<>();
        for (Integrante integrante : integranteRepository.findAllById(idsSolicitados)) {
            integrantesPorId.put(integrante.getId(), integrante);
        }

        List<Long> idsInexistentes = new ArrayList<>();
        for (Long id : idsSolicitados) {
            if (!integrantesPorId.containsKey(id)) {
                idsInexistentes.add(id);
            }
        }

        if (!idsInexistentes.isEmpty()) {
            throw new RecursoNaoEncontradoException(
                    "Integrantes não encontrados para os IDs: " + idsInexistentes
            );
        }

        Time time = new Time();
        time.setNomeDoClube(request.getNomeDoClube().trim());
        time.setData(request.getData());

        List<ComposicaoTime> composicao = new ArrayList<>();
        for (Long id : idsSolicitados) {
            composicao.add(new ComposicaoTime(time, integrantesPorId.get(id)));
        }
        time.setComposicaoTime(composicao);

        return new TimeResponse(timeRepository.save(time));
    }

    @Transactional(readOnly = true)
    public List<TimeResponse> listarTimes() {
        List<TimeResponse> respostas = new ArrayList<>();
        for (Time time : timeRepository.findAll()) {
            respostas.add(new TimeResponse(time));
        }

        return respostas;
    }
}