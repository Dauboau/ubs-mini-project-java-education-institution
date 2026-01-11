package daniel.contente.mapper;

import daniel.contente.dto.ProfessorRequestDto;
import daniel.contente.dto.ProfessorResponseDto;
import daniel.contente.dto.EnderecoRequestDto;
import daniel.contente.dto.EnderecoResponseDto;
import daniel.contente.model.Endereco;
import daniel.contente.model.Professor;

public final class ProfessorMapper {

    private ProfessorMapper() {}

    public static Professor toEntity(ProfessorRequestDto professorDto) {
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

    public static ProfessorResponseDto toResponse(Professor professor) {
        if (professor == null) return null;
        ProfessorResponseDto professorResponseDto = new ProfessorResponseDto();
        professorResponseDto.id = professor.getId();
        professorResponseDto.nome = professor.getNome();
        professorResponseDto.cpf = professor.getCpf();
        professorResponseDto.email = professor.getEmail();
        professorResponseDto.telefone = professor.getTelefone();
        professorResponseDto.departamento = professor.getDepartamento();
        if (professor.getEndereco() != null) {
            professorResponseDto.endereco = mapEnderecoToDto(professor.getEndereco());
        }
        return professorResponseDto;
    }

    private static EnderecoResponseDto mapEnderecoToDto(Endereco endereco) {
        if (endereco == null) return null;
        EnderecoResponseDto enderecoResponseDto = new EnderecoResponseDto();
        enderecoResponseDto.cep = endereco.getCep();
        enderecoResponseDto.numero = endereco.getNumero();
        enderecoResponseDto.logradouro = endereco.getLogradouro();
        enderecoResponseDto.bairro = endereco.getBairro();
        enderecoResponseDto.cidade = endereco.getCidade();
        enderecoResponseDto.estado = endereco.getEstado();
        return enderecoResponseDto;
    }

    private static Endereco mapEndereco(EnderecoRequestDto EnderecoRequestDto) {
        Endereco endereco = new Endereco();
        endereco.setCep(EnderecoRequestDto.cep);
        endereco.setNumero(EnderecoRequestDto.numero);
        endereco.setLogradouro(EnderecoRequestDto.logradouro);
        endereco.setBairro(EnderecoRequestDto.bairro);
        endereco.setCidade(EnderecoRequestDto.cidade);
        endereco.setEstado(EnderecoRequestDto.estado);
        return endereco;
    }
}
