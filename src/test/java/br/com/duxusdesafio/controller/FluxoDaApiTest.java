package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.repository.IntegranteRepository;
import br.com.duxusdesafio.repository.TimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FluxoDaApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TimeRepository timeRepository;

    @Autowired
    private IntegranteRepository integranteRepository;

    @BeforeEach
    void limparBanco() {
        timeRepository.deleteAll();
        integranteRepository.deleteAll();
    }

    @Test
    void deveCadastrarDadosEProcessaLos() throws Exception {
        cadastrarIntegrante("Michael Jordan", "ala");
        cadastrarIntegrante("Scottie Pippen", "ala");
        java.util.List<Long> ids = integranteRepository.findAll()
                .stream()
                .map(br.com.duxusdesafio.model.Integrante::getId)
                .collect(java.util.stream.Collectors.toList());

        mockMvc.perform(post("/api/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeDoClube\":\"Chicago Bulls\","
                                + "\"data\":\"1995-01-01\",\"integrantesIds\":["
                                + ids.get(0) + "," + ids.get(1) + "]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeDoClube").value("Chicago Bulls"))
                .andExpect(jsonPath("$.integrantes.length()").value(2));

        mockMvc.perform(get("/api/processamento/time-da-data")
                        .param("data", "1995-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("1995-01-01"))
                .andExpect(jsonPath("$.clube").value("Chicago Bulls"))
                .andExpect(jsonPath("$.integrantes",
                        containsInAnyOrder("Michael Jordan", "Scottie Pippen")))
                .andExpect(jsonPath("$.nomeDoClube").doesNotExist())
                .andExpect(jsonPath("$.id").doesNotExist());

        mockMvc.perform(get("/api/processamento/funcao-mais-recorrente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Função']").value("ala"))
                .andExpect(jsonPath("$.funcao").doesNotExist());

        mockMvc.perform(get("/api/processamento/contagem-por-funcao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ala").value(2));

        mockMvc.perform(get("/api/processamento/integrante-mais-usado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Michael Jordan"))
                .andExpect(jsonPath("$.funcao").value("ala"));

        mockMvc.perform(get("/api/processamento/integrantes-do-time-mais-recorrente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsInAnyOrder("Michael Jordan", "Scottie Pippen")));

        mockMvc.perform(get("/api/processamento/clube-mais-recorrente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clube").value("Chicago Bulls"));

        mockMvc.perform(get("/api/processamento/contagem-de-clubes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Chicago Bulls']").value(1));
    }

    @Test
    void naoDeveCadastrarMaisDeUmTimeNaMesmaData() throws Exception {
        cadastrarIntegrante("Michael Jordan", "ala");
        Long integranteId = integranteRepository.findAll().get(0).getId();
        String time = "{\"nomeDoClube\":\"Chicago Bulls\","
                + "\"data\":\"1995-01-01\",\"integrantesIds\":[" + integranteId + "]}";

        mockMvc.perform(post("/api/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(time))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(time))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Solicitação inválida"))
                .andExpect(jsonPath("$.mensagens[0]")
                        .value("Já existe um time cadastrado para a data 1995-01-01."));

        org.junit.jupiter.api.Assertions.assertEquals(1, timeRepository.count());
    }

    @Test
    void deveValidarIntegrantesAoMontarTime() throws Exception {
        cadastrarIntegrante("Michael Jordan", "ala");
        Long integranteId = integranteRepository.findAll().get(0).getId();

        mockMvc.perform(post("/api/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeDoClube\":\"Chicago Bulls\","
                                + "\"data\":\"1995-01-01\",\"integrantesIds\":[999999]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagens[0]")
                        .value("Integrantes não encontrados para os IDs: [999999]"));

        mockMvc.perform(post("/api/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeDoClube\":\"Chicago Bulls\","
                                + "\"data\":\"1995-01-01\",\"integrantesIds\":["
                                + integranteId + "," + integranteId + "]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.integrantes.length()").value(1));
    }

    @Test
    void deveValidarCadastroEPeriodo() throws Exception {
        mockMvc.perform(post("/api/integrantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"funcao\":\"ala\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Dados inválidos"))
                .andExpect(jsonPath("$.instante").value(
                        org.hamcrest.Matchers.matchesPattern("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}")
                ))
                .andExpect(jsonPath("$.mensagens[0]").exists());

        mockMvc.perform(get("/api/processamento/contagem-de-clubes")
                        .param("dataInicial", "2025-01-02")
                        .param("dataFinal", "2025-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Solicitação inválida"))
                .andExpect(jsonPath("$.mensagens[0]")
                        .value("A data inicial não pode ser posterior à data final."));

        mockMvc.perform(get("/api/processamento/time-da-data")
                        .param("data", "data-invalida"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Parâmetro inválido"))
                .andExpect(jsonPath("$.mensagens[0]")
                        .value("Data deve ser uma data válida."));
    }

    @Test
    void deveDisponibilizarDocumentacaoOpenApiESwagger() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value(org.hamcrest.Matchers.startsWith("3.")))
                .andExpect(jsonPath("$.info.title").value("API Escala"))
                .andExpect(jsonPath("$.paths['/api/integrantes']").exists())
                .andExpect(jsonPath("$.paths['/api/times']").exists())
                .andExpect(jsonPath("$.paths['/api/processamento/time-da-data']").exists())
                .andExpect(jsonPath("$.paths['/api/processamento/integrante-mais-usado']").exists())
                .andExpect(jsonPath("$.paths['/api/processamento/integrantes-do-time-mais-recorrente']").exists())
                .andExpect(jsonPath("$.paths['/api/processamento/funcao-mais-recorrente']").exists())
                .andExpect(jsonPath("$.paths['/api/processamento/clube-mais-recorrente']").exists())
                .andExpect(jsonPath("$.paths['/api/processamento/contagem-de-clubes']").exists())
                .andExpect(jsonPath("$.paths['/api/processamento/contagem-por-funcao']").exists());

        mockMvc.perform(get("/swagger"))
                .andExpect(status().is3xxRedirection());
    }

    private void cadastrarIntegrante(String nome, String funcao) throws Exception {
        mockMvc.perform(post("/api/integrantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"" + nome + "\",\"funcao\":\"" + funcao + "\"}"))
                .andExpect(status().isCreated());
    }
}
