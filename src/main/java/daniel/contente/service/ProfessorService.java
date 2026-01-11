package daniel.contente.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import daniel.contente.dto.ProfessorRequestDto;
import daniel.contente.exception.CpfDuplicadoException;
import daniel.contente.exception.RecursoNaoEncontradoException;
import daniel.contente.mapper.ProfessorMapper;
import daniel.contente.model.Professor;
import daniel.contente.plugin.ViaCep.ViaCepResponse;
import daniel.contente.plugin.ViaCep.ViaCepService;
import daniel.contente.repository.ProfessorRepository;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ViaCepService viaCepService;

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

    public Professor salvar(ProfessorRequestDto professorDto) {
        Optional<Professor> professorExistente = professorRepository.findByCpf(professorDto.cpf);
        if (professorExistente.isPresent()) {
            throw new CpfDuplicadoException("CPF já cadastrado: " + professorDto.cpf);
        }

        try {
            ViaCepResponse dadosCep = viaCepService.buscarEnderecoPorCep(professorDto.endereco.cep);
            
            // Complementa os dados do endereço
            professorDto.endereco.logradouro = dadosCep.logradouro;
            professorDto.endereco.bairro = dadosCep.bairro;
            professorDto.endereco.cidade = dadosCep.cidade;
            professorDto.endereco.estado = dadosCep.estado;

        } catch (Exception e) {
            throw new RuntimeException("Falha no Plugin ViaCep.", e);
        }

        Professor newProfessor = ProfessorMapper.toEntity(professorDto);
        return professorRepository.save(newProfessor);
    }

    public Professor atualizar(Long id, ProfessorRequestDto professorDto) {
        Optional<Professor> professorExistente = professorRepository.findById(id);
        if (professorExistente.isPresent()) {
            Professor professorAtualizado = ProfessorMapper.toEntity(professorDto);
            professorAtualizado.setId(id); // Mantém o ID existente

            try {
                ViaCepResponse dadosCep = viaCepService.buscarEnderecoPorCep(professorDto.endereco.cep);

                // Complementa os dados do endereço
                professorAtualizado.getEndereco().setLogradouro(dadosCep.logradouro);
                professorAtualizado.getEndereco().setBairro(dadosCep.bairro);
                professorAtualizado.getEndereco().setCidade(dadosCep.cidade);
                professorAtualizado.getEndereco().setEstado(dadosCep.estado);

            } catch (Exception e) {
                throw new RuntimeException("Falha no Plugin ViaCep.", e);
            }

            professorExistente = professorRepository.findByCpf(professorDto.cpf);
            if (professorExistente.isPresent() && professorExistente.get().getId() != professorAtualizado.getId()) {
                throw new CpfDuplicadoException("CPF já cadastrado: " + professorDto.cpf);
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
