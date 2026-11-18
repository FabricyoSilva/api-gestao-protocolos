package br.com.fabricyo.protocolos.controller;

import br.com.fabricyo.protocolos.TestcontainersConfiguration;
import br.com.fabricyo.protocolos.repository.ProtocoloRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ProtocoloControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProtocoloRepository repository;

    private static final String BODY_VALIDO = """
            {"assunto":"Falta de iluminacao","descricao":"Postes apagados","solicitante":"Maria"}
            """;

    @BeforeEach
    void limparBase() {
        // Isola cada teste: contagens e filtros ficam determinísticos.
        repository.deleteAll();
    }

    @Test
    void criaProtocoloComNumeroGeradoEStatusAberto() throws Exception {
        mockMvc.perform(post("/protocolos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.numero").value(matchesPattern("\\d{4}/\\d{6}")))
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andExpect(jsonPath("$.abertura").isNotEmpty())
                .andExpect(jsonPath("$.encerramento").doesNotExist());
    }

    @Test
    void rejeitaCriacaoComCamposObrigatoriosVazios() throws Exception {
        String invalido = """
                {"assunto":"","solicitante":""}
                """;

        mockMvc.perform(post("/protocolos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos").isNotEmpty());
    }

    @Test
    void buscaPorIdInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/protocolos/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void fluxoCompletoIniciarEConcluirPreencheEncerramento() throws Exception {
        String id = criarEObterId();

        mockMvc.perform(post("/protocolos/{id}/iniciar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));

        mockMvc.perform(post("/protocolos/{id}/concluir", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDO"))
                .andExpect(jsonPath("$.encerramento").isNotEmpty());
    }

    @Test
    void concluirProtocoloJaConcluidoRetorna409() throws Exception {
        String id = criarEObterId();
        mockMvc.perform(post("/protocolos/{id}/concluir", id)).andExpect(status().isOk());

        mockMvc.perform(post("/protocolos/{id}/concluir", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void cancelarAPartirDeAbertoEncerraOProtocolo() throws Exception {
        String id = criarEObterId();

        mockMvc.perform(post("/protocolos/{id}/cancelar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"))
                .andExpect(jsonPath("$.encerramento").isNotEmpty());
    }

    @Test
    void listagemRetornaEstruturaPaginada() throws Exception {
        criarEObterId();
        criarEObterId();
        criarEObterId();

        mockMvc.perform(get("/protocolos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo", hasSize(3)))
                .andExpect(jsonPath("$.pagina").value(0))
                .andExpect(jsonPath("$.totalElementos").value(3))
                .andExpect(jsonPath("$.totalPaginas").value(1))
                .andExpect(jsonPath("$.ultima").value(true));
    }

    @Test
    void filtraPorStatus() throws Exception {
        criarEObterId(); // fica ABERTO
        String idConcluir = criarEObterId();
        mockMvc.perform(post("/protocolos/{id}/concluir", idConcluir)).andExpect(status().isOk());

        mockMvc.perform(get("/protocolos").param("status", "CONCLUIDO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElementos").value(1))
                .andExpect(jsonPath("$.conteudo[0].status").value("CONCLUIDO"));
    }

    @Test
    void filtraPorSolicitanteParcialIgnorandoMaiusculas() throws Exception {
        criarComSolicitante("Assunto A", "Maria Oliveira");
        criarComSolicitante("Assunto B", "Joao Pereira");

        mockMvc.perform(get("/protocolos").param("solicitante", "maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElementos").value(1))
                .andExpect(jsonPath("$.conteudo[0].solicitante").value("Maria Oliveira"));
    }

    @Test
    void paginacaoRespeitaOTamanhoDaPagina() throws Exception {
        criarEObterId();
        criarEObterId();
        criarEObterId();

        mockMvc.perform(get("/protocolos").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo", hasSize(2)))
                .andExpect(jsonPath("$.tamanho").value(2))
                .andExpect(jsonPath("$.totalElementos").value(3))
                .andExpect(jsonPath("$.totalPaginas").value(2))
                .andExpect(jsonPath("$.ultima").value(false));
    }

    private String criarEObterId() throws Exception {
        return criar(BODY_VALIDO);
    }

    private String criarComSolicitante(String assunto, String solicitante) throws Exception {
        String corpo = """
                {"assunto":"%s","solicitante":"%s"}
                """.formatted(assunto, solicitante);
        return criar(corpo);
    }

    private String criar(String corpo) throws Exception {
        String resposta = mockMvc.perform(post("/protocolos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }
}
