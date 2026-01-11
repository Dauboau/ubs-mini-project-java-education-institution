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

import daniel.contente.dto.AlunoRequestDto;
import daniel.contente.dto.EnderecoRequestDto;
import daniel.contente.model.Aluno;
import daniel.contente.plugin.ViaCep.ViaCepResponse;
import daniel.contente.plugin.ViaCep.ViaCepService;
import daniel.contente.repository.AlunoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(daniel.contente.config.TestViaCepConfig.class)
public class AlunoControllerTestesFuncionais {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ViaCepService viaCepService;

    @BeforeEach
    void setUp() {
        // Limpar base de dados antes de cada teste
        alunoRepository.deleteAll();
        
        // Configurar mock padrão do ViaCep
        when(viaCepService.buscarEnderecoPorCep(anyString()))
                .thenReturn(new ViaCepResponse("01001000", "Praça da Sé", "Sé", "São Paulo", "SP"));
    }

    @Test
    void criarAluno_ComDadosValidos_DeveRetornarOk() throws Exception {
        AlunoRequestDto alunoRequestDto = new AlunoRequestDto(
            "Maria Silva Santos", 
            "12312312301", 
            "maria.silva@escola.com", 
            "+5511987654321",
            new EnderecoRequestDto("01310100", null, null, null, null, "1500"), 
            "2024001"
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome", is("Maria Silva Santos")))
                .andExpect(jsonPath("$.cpf", is("12312312301")))
                .andExpect(jsonPath("$.email", is("maria.silva@escola.com")))
                .andExpect(jsonPath("$.matricula", is("2024001")))
                .andExpect(jsonPath("$.endereco.cep", is("01310100")));

        // Verificar persistência no BD
        Aluno saved = alunoRepository.findByMatricula("2024001").orElseThrow();
        assert saved.getCpf().equals("12312312301");
        assert saved.getNome().equals("Maria Silva Santos");
    }

    @Test
    void criarAluno_ComMatriculaDuplicada_DeveRetornarBadRequest() throws Exception {
        // Criar primeiro aluno
        AlunoRequestDto alunoRequestDto1 = new AlunoRequestDto(
            "João Silva", 
            "11111111111", 
            "joao@escola.com", 
            "+5511999111111",
            new EnderecoRequestDto("01310100", null, null, null, null, "100"), 
            "2024100"
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto1)))
                .andExpect(status().isOk());

        // Tentar criar segundo aluno com mesma matrícula
        AlunoRequestDto alunoRequestDto2 = new AlunoRequestDto(
            "Pedro Santos", 
            "22222222222", 
            "pedro@escola.com", 
            "+5511999222222",
            new EnderecoRequestDto("01310200", null, null, null, null, "200"), 
            "2024100" // Matrícula duplicada
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarAluno_ComCpfDuplicado_DeveRetornarBadRequest() throws Exception {
        // Criar primeiro aluno
        AlunoRequestDto alunoRequestDto1 = new AlunoRequestDto(
            "Ana Costa", 
            "33333333333", 
            "ana@escola.com", 
            "+5511999333333",
            new EnderecoRequestDto("01310100", null, null, null, null, "300"), 
            "2024200"
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto1)))
                .andExpect(status().isOk());

        // Tentar criar segundo aluno com mesmo CPF
        AlunoRequestDto alunoRequestDto2 = new AlunoRequestDto(
            "Carlos Oliveira", 
            "33333333333", // CPF duplicado
            "carlos@escola.com", 
            "+5511999444444",
            new EnderecoRequestDto("01310200", null, null, null, null, "400"), 
            "2024201"
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTodos_DeveRetornarListaDeAlunos() throws Exception {
        // Criar múltiplos alunos
        AlunoRequestDto alunoRequestDto1 = new AlunoRequestDto(
            "Aluno Um", "44444444444", "aluno1@escola.com", "+5511999111111",
            new EnderecoRequestDto("01310100", null, null, null, null, "10"), "2024301"
        );
        AlunoRequestDto alunoRequestDto2 = new AlunoRequestDto(
            "Aluno Dois", "55555555555", "aluno2@escola.com", "+5511999222222",
            new EnderecoRequestDto("01310200", null, null, null, null, "20"), "2024302"
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto2)))
                .andExpect(status().isOk());

        // Listar todos
        mockMvc.perform(get("/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Aluno Um")))
                .andExpect(jsonPath("$[1].nome", is("Aluno Dois")));
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarAluno() throws Exception {
        // Criar aluno
        AlunoRequestDto alunoRequestDto = new AlunoRequestDto(
            "Teste Busca ID", "66666666666", "testeid@escola.com", "+5511999555555",
            new EnderecoRequestDto("01310100", null, null, null, null, "50"), "2024401"
        );

        String resp = mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(resp).get("id").asLong();

        // Buscar por ID
        mockMvc.perform(get("/alunos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.nome", is("Teste Busca ID")))
                .andExpect(jsonPath("$.cpf", is("66666666666")));
    }

    @Test
    void buscarPorId_ComIdInexistente_DeveRetornar404() throws Exception {
        mockMvc.perform(get("/alunos/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorCpf_ComCpfValido_DeveRetornarAluno() throws Exception {
        // Criar aluno
        AlunoRequestDto alunoRequestDto = new AlunoRequestDto(
            "Teste Busca CPF", "77777777777", "testecpf@escola.com", "+5511999666666",
            new EnderecoRequestDto("01310100", null, null, null, null, "60"), "2024501"
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto)))
                .andExpect(status().isOk());

        // Buscar por CPF
        mockMvc.perform(get("/alunos/cpf/77777777777"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Teste Busca CPF")))
                .andExpect(jsonPath("$.cpf", is("77777777777")));
    }

    @Test
    void buscarPorMatricula_ComMatriculaValida_DeveRetornarAluno() throws Exception {
        // Criar aluno
        AlunoRequestDto alunoRequestDto = new AlunoRequestDto(
            "Teste Busca Matrícula", "88888888888", "testematricula@escola.com", "+5511999777777",
            new EnderecoRequestDto("01310100", null, null, null, null, "70"), "2024601"
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto)))
                .andExpect(status().isOk());

        // Buscar por matrícula
        mockMvc.perform(get("/alunos/matricula/2024601"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Teste Busca Matrícula")))
                .andExpect(jsonPath("$.matricula", is("2024601")));
    }

    @Test
    void atualizarAluno_ComDadosValidos_DeveRetornarOk() throws Exception {
        // Criar aluno
        AlunoRequestDto alunoRequestDto = new AlunoRequestDto(
            "Nome Original", "99999999999", "original@escola.com", "+5511999888888",
            new EnderecoRequestDto("01310100", null, null, null, null, "80"), "2024701"
        );

        String resp = mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(resp).get("id").asLong();

        // Atualizar dados
        alunoRequestDto.nome = "Nome Atualizado";
        alunoRequestDto.email = "atualizado@escola.com";
        alunoRequestDto.telefone = "+5511988888888";

        mockMvc.perform(put("/alunos/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Nome Atualizado")))
                .andExpect(jsonPath("$.email", is("atualizado@escola.com")))
                .andExpect(jsonPath("$.cpf", is("99999999999")));

        // Verificar persistência
        Aluno atualizado = alunoRepository.findById(id).orElseThrow();
        assert atualizado.getNome().equals("Nome Atualizado");
    }

    @Test
    void deletarAluno_ComIdValido_DeveRetornar204() throws Exception {
        // Criar aluno
        AlunoRequestDto alunoRequestDto = new AlunoRequestDto(
            "Para Deletar", "10101010101", "deletar@escola.com", "+5511999999999",
            new EnderecoRequestDto("01310100", null, null, null, null, "90"), "2024801"
        );

        String resp = mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alunoRequestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(resp).get("id").asLong();

        // Deletar
        mockMvc.perform(delete("/alunos/" + id))
                .andExpect(status().isNoContent());

        // Verificar que não existe mais
        assert alunoRepository.findById(id).isEmpty();
    }

    @Test
    void criarAluno_ComCamposInvalidos_DeveRetornar400() throws Exception {
        // Nome vazio
        AlunoRequestDto dtoNomeVazio = new AlunoRequestDto(
            "", "11111111111", "teste@escola.com", "+5511999000000",
            new EnderecoRequestDto("01310100", null, null, null, null, "100"), "2024901"
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoNomeVazio)))
                .andExpect(status().isBadRequest());

        // CPF inválido (menos de 11 dígitos)
        AlunoRequestDto dtoCpfInvalido = new AlunoRequestDto(
            "Nome Teste", "123456789", "teste@escola.com", "+5511999000000",
            new EnderecoRequestDto("01310100", null, null, null, null, "100"), "2024901"
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoCpfInvalido)))
                .andExpect(status().isBadRequest());

        // Email inválido
        AlunoRequestDto dtoEmailInvalido = new AlunoRequestDto(
            "Nome Teste", "11111111111", "email-invalido", "+5511999000000",
            new EnderecoRequestDto("01310100", null, null, null, null, "100"), "2024901"
        );

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEmailInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fluxoCompleto_CriarBuscarAtualizarDeletar() throws Exception {
        // 1. Criar aluno
        AlunoRequestDto dto = new AlunoRequestDto(
            "Fluxo Completo", "20202020202", "fluxo@escola.com", "+5511987654321",
            new EnderecoRequestDto("01310100", null, null, null, null, "123"), "2025001"
        );

        String respCriar = mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Fluxo Completo")))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(respCriar).get("id").asLong();

        // 2. Buscar por ID
        mockMvc.perform(get("/alunos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Fluxo Completo")));

        // 3. Buscar por CPF
        mockMvc.perform(get("/alunos/cpf/20202020202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Fluxo Completo")));

        // 4. Buscar por matrícula
        mockMvc.perform(get("/alunos/matricula/2025001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Fluxo Completo")));

        // 5. Atualizar
        dto.nome = "Fluxo Atualizado";
        mockMvc.perform(put("/alunos/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Fluxo Atualizado")));

        // 6. Verificar atualização
        mockMvc.perform(get("/alunos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Fluxo Atualizado")));

        // 7. Deletar
        mockMvc.perform(delete("/alunos/" + id))
                .andExpect(status().isNoContent());

        // 8. Verificar que foi deletado
        mockMvc.perform(get("/alunos/" + id))
                .andExpect(status().isNotFound());
    }
}
