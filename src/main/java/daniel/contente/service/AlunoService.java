package daniel.contente.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import daniel.contente.dto.AlunoRequestDto;
import daniel.contente.exception.CpfDuplicadoException;
import daniel.contente.exception.MatriculaDuplicadaException;
import daniel.contente.exception.RecursoNaoEncontradoException;
import daniel.contente.mapper.AlunoMapper;
import daniel.contente.model.Aluno;
import daniel.contente.plugin.ViaCep.ViaCepResponse;
import daniel.contente.plugin.ViaCep.ViaCepService;
import daniel.contente.repository.AlunoRepository;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ViaCepService viaCepService;

    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    public Aluno buscarPorId(Long id) {
        Optional<Aluno> aluno = alunoRepository.findById(id);
        if (aluno.isPresent()) {
            return aluno.get();
        } else {
            throw new RecursoNaoEncontradoException("Aluno não encontrado com ID: " + id);
        }
    }

    public Aluno buscarPorCpf(String cpf) {
        Optional<Aluno> aluno = alunoRepository.findByCpf(cpf);
        if (aluno.isPresent()) {
            return aluno.get();
        } else {
            throw new RecursoNaoEncontradoException("Aluno não encontrado com CPF: " + cpf);
        }
    }

    public Aluno buscarPorMatricula(String matricula) {
        Optional<Aluno> aluno = alunoRepository.findByMatricula(matricula);
        if (aluno.isPresent()) {
            return aluno.get();
        } else {
            throw new RecursoNaoEncontradoException("Aluno não encontrado com matrícula: " + matricula);
        }
    }

    public Aluno salvar(AlunoRequestDto alunoDto) {
        Optional<Aluno> alunoExistente = alunoRepository.findByCpf(alunoDto.cpf);
        if (alunoExistente.isPresent()) {
            throw new CpfDuplicadoException("CPF já cadastrado: " + alunoDto.cpf);
        }
        alunoExistente = alunoRepository.findByMatricula(alunoDto.matricula);
        if (alunoExistente.isPresent()) {
            throw new MatriculaDuplicadaException("Matrícula já cadastrada: " + alunoDto.matricula);
        }

        try {
            ViaCepResponse dadosCep = viaCepService.buscarEnderecoPorCep(alunoDto.endereco.cep);

            // Complementa os dados do endereço
            alunoDto.endereco.logradouro = dadosCep.logradouro;
            alunoDto.endereco.bairro = dadosCep.bairro;
            alunoDto.endereco.cidade = dadosCep.cidade;
            alunoDto.endereco.estado = dadosCep.estado;

        } catch (Exception e) {
            throw new RuntimeException("Falha no Plugin ViaCep.", e);
        }

        Aluno newAluno = AlunoMapper.toEntity(alunoDto);
        return alunoRepository.save(newAluno);
    }

    public Aluno atualizar(Long id, AlunoRequestDto alunoDto) {
        Optional<Aluno> alunoExistente = alunoRepository.findById(id);
        if (alunoExistente.isPresent()) {
            Aluno alunoAtualizado = AlunoMapper.toEntity(alunoDto);
            alunoAtualizado.setId(id); // Mantém o ID existente

            try {
                ViaCepResponse dadosCep = viaCepService.buscarEnderecoPorCep(alunoDto.endereco.cep);

                // Complementa os dados do endereço
                alunoDto.endereco.logradouro = dadosCep.logradouro;
                alunoDto.endereco.bairro = dadosCep.bairro;
                alunoDto.endereco.cidade = dadosCep.cidade;
                alunoDto.endereco.estado = dadosCep.estado;

            } catch (Exception e) {
                throw new RuntimeException("Falha no Plugin ViaCep.", e);
            }

            alunoExistente = alunoRepository.findByCpf(alunoDto.cpf);
            if (alunoExistente.isPresent()) {
                throw new CpfDuplicadoException("CPF já cadastrado: " + alunoDto.cpf);
            }

            alunoExistente = alunoRepository.findByMatricula(alunoDto.matricula);
            if (alunoExistente.isPresent()) {
                throw new MatriculaDuplicadaException("Matrícula já cadastrada: " + alunoDto.matricula);
            }

            return alunoRepository.save(alunoAtualizado);
        } else {
            throw new RecursoNaoEncontradoException("Aluno não encontrado com ID: " + id);
        }
    }

    public boolean deletar(Long id) {
        Optional<Aluno> aluno = alunoRepository.findById(id);
        if (aluno.isPresent()) {
            alunoRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
