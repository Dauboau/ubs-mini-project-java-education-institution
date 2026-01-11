package daniel.contente.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import daniel.contente.dto.AlunoRequestDto;
import daniel.contente.dto.EnderecoRequestDto;
import daniel.contente.model.Aluno;
import daniel.contente.plugin.ViaCep.ViaCepResponse;
import daniel.contente.plugin.ViaCep.ViaCepService;
import daniel.contente.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void criarAluno_IntegraComBD_E_RetornaOk() throws Exception {
        when(viaCepService.buscarEnderecoPorCep(anyString()))
                .thenReturn(new ViaCepResponse("01001000", "Logradouro", "Bairro", "Cidade", "UF"));

        AlunoRequestDto dto = new AlunoRequestDto("Fulano Int", "55555555555", "fulano.int@mail.com", "+5511999000222",
                new EnderecoRequestDto("01005000", null, null, null, null, "20"), "3001");

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula", is("3001")));

        // saved in DB
        Aluno saved = alunoRepository.findByMatricula("3001").orElseThrow();
        assert saved.getCpf().equals("55555555555");
    }

    @Test
    void criarAluno_DeveRetornarBadRequest_QuandoMatriculaDuplicada() throws Exception {
        when(viaCepService.buscarEnderecoPorCep(anyString()))
                .thenReturn(new ViaCepResponse("01006000", "L", "B", "C", "S"));

        AlunoRequestDto dto1 = new AlunoRequestDto("A", "66666666666", "a@mail.com", null,
                new EnderecoRequestDto("01006000", null, null, null, null, "1"), "4001");

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isOk());

        AlunoRequestDto dto2 = new AlunoRequestDto("B", "77777777777", "b@mail.com", null,
                new EnderecoRequestDto("01006001", null, null, null, null, "2"), "4001");

        mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    @Test
    void atualizar_e_deletar_funcionam_com_bd_real() throws Exception {
        when(viaCepService.buscarEnderecoPorCep(anyString()))
                .thenReturn(new ViaCepResponse("01007000", "L", "B", "C", "S"));

        AlunoRequestDto dto = new AlunoRequestDto("ToUpdate", "88888888888", "up@mail.com", null,
                new EnderecoRequestDto("01007000", null, null, null, null, "3"), "5001");

        String resp = mockMvc.perform(post("/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // update name
        dto.nome = "Updated Name";

        mockMvc.perform(put("/alunos/" + objectMapper.readTree(resp).get("id").asLong())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Updated Name")));

        // delete
        mockMvc.perform(delete("/alunos/" + objectMapper.readTree(resp).get("id").asLong()))
                .andExpect(status().isNoContent());
    }
}
