package daniel.contente.service;

import daniel.contente.exception.CpfDuplicadoException;
import daniel.contente.exception.RecursoNaoEncontradoException;
import daniel.contente.model.Aluno;
import daniel.contente.repository.AlunoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

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

    public Aluno salvar(Aluno aluno) {
        // Validação: CPF único
        Optional<Aluno> alunoExistente = alunoRepository.findByCpf(aluno.getCpf());
        if (alunoExistente.isPresent() && !alunoExistente.get().getId().equals(aluno.getId())) {
            // Se um aluno com o mesmo CPF for encontrado e for um aluno diferente (não uma atualização do mesmo)
            throw new CpfDuplicadoException("CPF já cadastrado: " + aluno.getCpf());
        }
        return alunoRepository.save(aluno);
    }

    public Aluno atualizar(Long id, Aluno alunoAtualizado) {
        Optional<Aluno> alunoExistente = alunoRepository.findById(id);
        if (alunoExistente.isPresent()) {
            // Garante que o ID do aluno atualizado seja o mesmo do encontrado
            alunoAtualizado.setId(id);
            // Validação: CPF único (exceto para o próprio aluno sendo atualizado)
            Optional<Aluno> alunoComMesmoCpf = alunoRepository.findByCpf(alunoAtualizado.getCpf());
            if (alunoComMesmoCpf.isPresent() && !alunoComMesmoCpf.get().getId().equals(id)) {
                throw new CpfDuplicadoException("CPF já cadastrado por outro aluno: " + alunoAtualizado.getCpf());
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
            return false; // Indica que o aluno não foi encontrado e não pôde ser deletado
        }
    }
}
