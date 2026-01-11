package daniel.contente.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import daniel.contente.dto.EnderecoRequestDto;
import daniel.contente.dto.ProfessorRequestDto;
import daniel.contente.model.Professor;
import daniel.contente.plugin.ViaCep.ViaCepResponse;
import daniel.contente.plugin.ViaCep.ViaCepService;
import daniel.contente.repository.ProfessorRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(daniel.contente.config.TestViaCepConfig.class)
public class ProfessorControllerTestesFuncionaisTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ViaCepService viaCepService;

    @BeforeEach
    void setUp() {
        // Limpar base de dados antes de cada teste
        professorRepository.deleteAll();
        
        // Configurar mock padrão do ViaCep
        when(viaCepService.buscarEnderecoPorCep(anyString()))
                .thenReturn(new ViaCepResponse("01001000", "Praça da Sé", "Sé", "São Paulo", "SP"));
    }

    @Test
    void criarProfessor_ComDadosValidos_DeveRetornarOk() throws Exception {
        ProfessorRequestDto professorRequestDto = new ProfessorRequestDto(
            "Dr. Roberto Carlos Silva", 
            "12345678901", 
            "roberto.silva@universidade.edu.br", 
            "+5511987654321",
            new EnderecoRequestDto("01310100", null, null, null, null, "1000"), 
            "Ciência da Computação"
        );

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome", is("Dr. Roberto Carlos Silva")))
                .andExpect(jsonPath("$.cpf", is("12345678901")))
                .andExpect(jsonPath("$.email", is("roberto.silva@universidade.edu.br")))
                .andExpect(jsonPath("$.departamento", is("Ciência da Computação")))
                .andExpect(jsonPath("$.endereco.cep", is("01310100")));

        // Verificar persistência no BD
        Professor saved = professorRepository.findByCpf("12345678901").orElseThrow();
        assert saved.getNome().equals("Dr. Roberto Carlos Silva");
        assert saved.getDepartamento().equals("Ciência da Computação");
    }

    @Test
    void criarProfessor_ComCpfDuplicado_DeveRetornarBadRequest() throws Exception {
        // Criar primeiro professor
        ProfessorRequestDto professorRequestDto1 = new ProfessorRequestDto(
            "Profa. Ana Maria", 
            "11111111111", 
            "ana@universidade.edu.br", 
            "+5511999111111",
            new EnderecoRequestDto("01310100", null, null, null, null, "100"), 
            "Matemática"
        );

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto1)))
                .andExpect(status().isOk());

        // Tentar criar segundo professor com mesmo CPF
        ProfessorRequestDto professorRequestDto2 = new ProfessorRequestDto(
            "Prof. Carlos Eduardo", 
            "11111111111", // CPF duplicado
            "carlos@universidade.edu.br", 
            "+5511999222222",
            new EnderecoRequestDto("01310200", null, null, null, null, "200"), 
            "Física"
        );

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    @Test
    void listarTodos_DeveRetornarListaDeProfessores() throws Exception {
        // Criar múltiplos professores
        ProfessorRequestDto professorRequestDto1 = new ProfessorRequestDto(
            "Prof. João Silva", "22222222222", "joao@universidade.edu.br", "+5511999333333",
            new EnderecoRequestDto("01310100", null, null, null, null, "300"), "História"
        );
        ProfessorRequestDto professorRequestDto2 = new ProfessorRequestDto(
            "Profa. Maria Santos", "33333333333", "maria@universidade.edu.br", "+5511999444444",
            new EnderecoRequestDto("01310200", null, null, null, null, "400"), "Geografia"
        );

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto2)))
                .andExpect(status().isOk());

        // Listar todos
        mockMvc.perform(get("/professores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Prof. João Silva")))
                .andExpect(jsonPath("$[1].nome", is("Profa. Maria Santos")));
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarProfessor() throws Exception {
        // Criar professor
        ProfessorRequestDto professorRequestDto = new ProfessorRequestDto(
            "Prof. Teste ID", "44444444444", "testeid@universidade.edu.br", "+5511999555555",
            new EnderecoRequestDto("01310100", null, null, null, null, "500"), "Química"
        );

        String resp = mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(resp).get("id").asLong();

        // Buscar por ID
        mockMvc.perform(get("/professores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.nome", is("Prof. Teste ID")))
                .andExpect(jsonPath("$.departamento", is("Química")));
    }

    @Test
    void buscarPorId_ComIdInexistente_DeveRetornar404() throws Exception {
        mockMvc.perform(get("/professores/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorCpf_ComCpfValido_DeveRetornarProfessor() throws Exception {
        // Criar professor
        ProfessorRequestDto professorRequestDto = new ProfessorRequestDto(
            "Prof. Teste CPF", "55555555555", "testecpf@universidade.edu.br", "+5511999666666",
            new EnderecoRequestDto("01310100", null, null, null, null, "600"), "Biologia"
        );

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto)))
                .andExpect(status().isOk());

        // Buscar por CPF
        mockMvc.perform(get("/professores/cpf/55555555555"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Prof. Teste CPF")))
                .andExpect(jsonPath("$.cpf", is("55555555555")));
    }

    @Test
    void buscarPorDepartamento_DeveRetornarProfessoresCorretos() throws Exception {
        // Criar professores de diferentes departamentos
        ProfessorRequestDto professorRequestDto1 = new ProfessorRequestDto(
            "Prof. Física 1", "66666666666", "fisica1@universidade.edu.br", "+5511999777777",
            new EnderecoRequestDto("01310100", null, null, null, null, "700"), "Física"
        );
        ProfessorRequestDto professorRequestDto2 = new ProfessorRequestDto(
            "Prof. Física 2", "77777777777", "fisica2@universidade.edu.br", "+5511999888888",
            new EnderecoRequestDto("01310200", null, null, null, null, "800"), "Física"
        );
        ProfessorRequestDto dto3 = new ProfessorRequestDto(
            "Prof. Matemática", "88888888888", "matematica@universidade.edu.br", "+5511999999999",
            new EnderecoRequestDto("01310300", null, null, null, null, "900"), "Matemática"
        );

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto2)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto3)))
                .andExpect(status().isOk());

        // Buscar por departamento de Física
        mockMvc.perform(get("/professores/departamento/Física"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].departamento", is("Física")))
                .andExpect(jsonPath("$[1].departamento", is("Física")));

        // Buscar por departamento de Matemática
        mockMvc.perform(get("/professores/departamento/Matemática"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Prof. Matemática")));
    }

    @Test
    void atualizarProfessor_ComDadosValidos_DeveRetornarOk() throws Exception {
        // Criar professor
        ProfessorRequestDto professorRequestDto = new ProfessorRequestDto(
            "Prof. Original", "99999999999", "original@universidade.edu.br", "+5511988888888",
            new EnderecoRequestDto("01310100", null, null, null, null, "1100"), "Literatura"
        );

        String resp = mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(resp).get("id").asLong();

        // Atualizar dados
        professorRequestDto.nome = "Prof. Atualizado";
        professorRequestDto.email = "atualizado@universidade.edu.br";
        professorRequestDto.departamento = "Língua Portuguesa";

        mockMvc.perform(put("/professores/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Prof. Atualizado")))
                .andExpect(jsonPath("$.email", is("atualizado@universidade.edu.br")))
                .andExpect(jsonPath("$.departamento", is("Língua Portuguesa")));

        // Verificar persistência
        Professor atualizado = professorRepository.findById(id).orElseThrow();
        assert atualizado.getNome().equals("Prof. Atualizado");
        assert atualizado.getDepartamento().equals("Língua Portuguesa");
    }

    @Test
    void deletarProfessor_ComIdValido_DeveRetornar204() throws Exception {
        // Criar professor
        ProfessorRequestDto professorRequestDto = new ProfessorRequestDto(
            "Prof. Para Deletar", "10101010101", "deletar@universidade.edu.br", "+5511977777777",
            new EnderecoRequestDto("01310100", null, null, null, null, "1200"), "Filosofia"
        );

        String resp = mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(resp).get("id").asLong();

        // Deletar
        mockMvc.perform(delete("/professores/" + id))
                .andExpect(status().isNoContent());

        // Verificar que não existe mais
        assert professorRepository.findById(id).isEmpty();
    }

    @Test
    void criarProfessor_ComCamposInvalidos_DeveRetornar400() throws Exception {
        // Nome vazio
        ProfessorRequestDto dtoNomeVazio = new ProfessorRequestDto(
            "", "11111111111", "teste@universidade.edu.br", "+5511999000000",
            new EnderecoRequestDto("01310100", null, null, null, null, "100"), "Teste"
        );

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoNomeVazio)))
                .andExpect(status().isBadRequest());

        // CPF inválido
        ProfessorRequestDto dtoCpfInvalido = new ProfessorRequestDto(
            "Nome Teste", "123456789", "teste@universidade.edu.br", "+5511999000000",
            new EnderecoRequestDto("01310100", null, null, null, null, "100"), "Teste"
        );

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoCpfInvalido)))
                .andExpect(status().isBadRequest());

        // Email inválido
        ProfessorRequestDto dtoEmailInvalido = new ProfessorRequestDto(
            "Nome Teste", "11111111111", "email-invalido", "+5511999000000",
            new EnderecoRequestDto("01310100", null, null, null, null, "100"), "Teste"
        );

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEmailInvalido)))
                .andExpect(status().isBadRequest());

        // Departamento vazio
        ProfessorRequestDto dtoDeptVazio = new ProfessorRequestDto(
            "Nome Teste", "11111111111", "teste@universidade.edu.br", "+5511999000000",
            new EnderecoRequestDto("01310100", null, null, null, null, "100"), ""
        );

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoDeptVazio)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fluxoCompleto_CriarBuscarAtualizarDeletar() throws Exception {
        // 1. Criar professor
        ProfessorRequestDto professorRequestDto = new ProfessorRequestDto(
            "Dr. Fluxo Completo", "20202020202", "fluxo@universidade.edu.br", "+5511987654321",
            new EnderecoRequestDto("01310100", null, null, null, null, "1500"), "Engenharia"
        );

        String respCriar = mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dr. Fluxo Completo")))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(respCriar).get("id").asLong();

        // 2. Buscar por ID
        mockMvc.perform(get("/professores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dr. Fluxo Completo")));

        // 3. Buscar por CPF
        mockMvc.perform(get("/professores/cpf/20202020202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dr. Fluxo Completo")));

        // 4. Buscar por departamento
        mockMvc.perform(get("/professores/departamento/Engenharia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Dr. Fluxo Completo")));

        // 5. Atualizar
        professorRequestDto.nome = "Dr. Fluxo Atualizado";
        professorRequestDto.departamento = "Engenharia Elétrica";
        mockMvc.perform(put("/professores/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(professorRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dr. Fluxo Atualizado")))
                .andExpect(jsonPath("$.departamento", is("Engenharia Elétrica")));

        // 6. Verificar atualização
        mockMvc.perform(get("/professores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dr. Fluxo Atualizado")));

        // 7. Deletar
        mockMvc.perform(delete("/professores/" + id))
                .andExpect(status().isNoContent());

        // 8. Verificar que foi deletado
        mockMvc.perform(get("/professores/" + id))
                .andExpect(status().isNotFound());
    }
}
