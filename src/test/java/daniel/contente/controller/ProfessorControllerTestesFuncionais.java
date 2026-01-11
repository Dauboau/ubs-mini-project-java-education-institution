package daniel.contente.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import daniel.contente.dto.EnderecoRequestDto;
import daniel.contente.dto.ProfessorRequestDto;
import daniel.contente.model.Professor;
import daniel.contente.plugin.ViaCep.ViaCepResponse;
import daniel.contente.plugin.ViaCep.ViaCepService;
import daniel.contente.repository.ProfessorRepository;
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
public class ProfessorControllerTestesFuncionais {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ViaCepService viaCepService;

    @Test
    void criarProfessor_E_ConsultaBD() throws Exception {
        when(viaCepService.buscarEnderecoPorCep(anyString()))
                .thenReturn(new ViaCepResponse("01008000", "L", "B", "C", "S"));

        ProfessorRequestDto dto = new ProfessorRequestDto("Prof Int", "99999999999", "pint@mail.com", null,
                new EnderecoRequestDto("01008000", null, null, null, null, "4"), "Física");

        mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departamento", is("Física")));

        Professor saved = professorRepository.findByCpf("99999999999").orElseThrow();
        assert saved.getDepartamento().equals("Física");
    }

    @Test
    void atualizarEDeletarProfessor_ComBD() throws Exception {
        when(viaCepService.buscarEnderecoPorCep(anyString()))
                .thenReturn(new ViaCepResponse("01009000", "L", "B", "C", "S"));

        ProfessorRequestDto dto = new ProfessorRequestDto("Prof U", "10101010101", "pu@mail.com", null,
                new EnderecoRequestDto("01009000", null, null, null, null, "5"), "Química");

        String resp = mockMvc.perform(post("/professores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        dto.nome = "Prof Updated";

        mockMvc.perform(put("/professores/" + objectMapper.readTree(resp).get("id").asLong())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Prof Updated")));

        mockMvc.perform(delete("/professores/" + objectMapper.readTree(resp).get("id").asLong()))
                .andExpect(status().isNoContent());
    }
}
