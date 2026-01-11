package daniel.contente.mapper;

import daniel.contente.dto.CreateProfessorRequestDTO;
import daniel.contente.dto.EnderecoDTO;
import daniel.contente.model.Endereco;
import daniel.contente.model.Professor;

public final class ProfessorMapper {

    private ProfessorMapper() {}

    public static Professor toEntity(CreateProfessorRequestDTO professorDto) {
        Professor professor = new Professor();
        professor.setNome(professorDto.nome);
        professor.setCpf(professorDto.cpf);
        professor.setEmail(professorDto.email);
        professor.setTelefone(professorDto.telefone);
        professor.setDepartamento(professorDto.departamento);
        if (professorDto.endereco != null) {
            professor.setEndereco(mapEndereco(professorDto.endereco));
        }
        return professor;
    }

    private static Endereco mapEndereco(EnderecoDTO enderecoDto) {
        Endereco endereco = new Endereco();
        endereco.setCep(enderecoDto.cep);
        endereco.setNumero(enderecoDto.numero);
        endereco.setLogradouro(enderecoDto.logradouro);
        endereco.setBairro(enderecoDto.bairro);
        endereco.setCidade(enderecoDto.cidade);
        endereco.setEstado(enderecoDto.estado);
        return endereco;
    }
}
