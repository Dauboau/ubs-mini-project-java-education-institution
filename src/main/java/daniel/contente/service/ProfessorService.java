package daniel.contente.service;

import daniel.contente.exception.CpfDuplicadoException;
import daniel.contente.exception.RecursoNaoEncontradoException;
import daniel.contente.model.Professor;
import daniel.contente.repository.ProfessorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    public List<Professor> listarTodos() {
        return professorRepository.findAll();
    }

    public Professor buscarPorId(Long id) {
        Optional<Professor> professor = professorRepository.findById(id);
        if (professor.isPresent()) {
            return professor.get();
        } else {
            throw new RecursoNaoEncontradoException("Professor não encontrado com ID: " + id);
        }
    }

    public Professor buscarPorCpf(String cpf) {
        Optional<Professor> professor = professorRepository.findByCpf(cpf);
        if (professor.isPresent()) {
            return professor.get();
        } else {
            throw new RecursoNaoEncontradoException("Professor não encontrado com CPF: " + cpf);
        }
    }

    public List<Professor> buscarPorDepartamento(String departamento) {
        List<Professor> professores = professorRepository.findAllByDepartamento(departamento);
        if (!professores.isEmpty()) {
            return professores;
        } else {
            throw new RecursoNaoEncontradoException("Nenhum professor encontrado no departamento: " + departamento);
        }
    }

    public Professor salvar(Professor professor) {
        // Validação: CPF único
        Optional<Professor> professorExistente = professorRepository.findByCpf(professor.getCpf());
        if (professorExistente.isPresent() && !professorExistente.get().getId().equals(professor.getId())) {
            throw new CpfDuplicadoException("CPF já cadastrado: " + professor.getCpf());
        }
        return professorRepository.save(professor);
    }

    public Professor atualizar(Long id, Professor professorAtualizado) {
        Optional<Professor> professorExistente = professorRepository.findById(id);
        if (professorExistente.isPresent()) {
            professorAtualizado.setId(id);
            // Validação: CPF único (exceto para o próprio professor sendo atualizado)
            Optional<Professor> professorComMesmoCpf = professorRepository.findByCpf(professorAtualizado.getCpf());
            if (professorComMesmoCpf.isPresent() && !professorComMesmoCpf.get().getId().equals(id)) {
                throw new CpfDuplicadoException("CPF já cadastrado por outro professor: " + professorAtualizado.getCpf());
            }
            return professorRepository.save(professorAtualizado);
        } else {
            throw new RecursoNaoEncontradoException("Professor não encontrado com ID: " + id);
        }
    }

    public boolean deletar(Long id) {
        Optional<Professor> professor = professorRepository.findById(id);
        if (professor.isPresent()) {
            professorRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
