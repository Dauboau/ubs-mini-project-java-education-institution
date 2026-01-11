package daniel.contente.mapper;

import daniel.contente.dto.CreateAlunoRequestDTO;
import daniel.contente.dto.EnderecoDTO;
import daniel.contente.model.Aluno;
import daniel.contente.model.Endereco;

public final class AlunoMapper {

    private AlunoMapper() {}

    public static Aluno toEntity(CreateAlunoRequestDTO alunoDto) {
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