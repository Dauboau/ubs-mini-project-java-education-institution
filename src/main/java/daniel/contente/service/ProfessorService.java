package daniel.contente.service;

import daniel.contente.model.Professor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfessorService extends UsuarioService {

    @Override
    public boolean validarCpfUnico(String cpf) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Professor salvar(Professor professor) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Optional<Professor> buscarPorId(Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
