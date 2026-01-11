package daniel.contente.service;

import daniel.contente.model.Aluno;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlunoService extends UsuarioService {

    @Override
    public boolean validarCpfUnico(String cpf) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Aluno salvar(Aluno aluno) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Optional<Aluno> buscarPorId(Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
