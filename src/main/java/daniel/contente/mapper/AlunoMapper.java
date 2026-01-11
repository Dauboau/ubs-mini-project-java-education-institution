package daniel.contente.mapper;

import daniel.contente.dto.AlunoRequestDto;
import daniel.contente.dto.AlunoResponseDto;
import daniel.contente.dto.EnderecoRequestDto;
import daniel.contente.dto.EnderecoResponseDto;
import daniel.contente.model.Aluno;
import daniel.contente.model.Endereco;

public final class AlunoMapper {

    private AlunoMapper() {}

    public static Aluno toEntity(AlunoRequestDto alunoDto) {
        Aluno aluno = new Aluno();
        aluno.setNome(alunoDto.nome);
        aluno.setCpf(alunoDto.cpf);
        aluno.setEmail(alunoDto.email);
        aluno.setTelefone(alunoDto.telefone);
        aluno.setMatricula(alunoDto.matricula);
        if (alunoDto.endereco != null) {
            aluno.setEndereco(mapEndereco(alunoDto.endereco));
        }
        return aluno;
    }

    public static AlunoResponseDto toResponse(Aluno aluno) {
        if (aluno == null) return null;
        AlunoResponseDto alunoResponseDto = new AlunoResponseDto();
        alunoResponseDto.id = aluno.getId();
        alunoResponseDto.nome = aluno.getNome();
        alunoResponseDto.cpf = aluno.getCpf();
        alunoResponseDto.email = aluno.getEmail();
        alunoResponseDto.telefone = aluno.getTelefone();
        alunoResponseDto.matricula = aluno.getMatricula();
        if (aluno.getEndereco() != null) {
            alunoResponseDto.endereco = mapEnderecoToDto(aluno.getEndereco());
        }
        return alunoResponseDto;
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