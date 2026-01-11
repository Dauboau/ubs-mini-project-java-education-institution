package daniel.contente.controller;

import daniel.contente.exception.RecursoNaoEncontradoException;
import daniel.contente.model.Aluno;
import daniel.contente.service.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    // Endpoint: GET /api/alunos
    @GetMapping
    public ResponseEntity<List<Aluno>> listarTodos() {
        List<Aluno> alunos = alunoService.listarTodos();
        return ResponseEntity.ok(alunos); // Retorna 200 OK com a lista
    }

    // Endpoint: GET /api/alunos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        try {
            Aluno aluno = alunoService.buscarPorId(id);
            return ResponseEntity.ok(aluno); // Retorna 200 OK com o aluno
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found
        }
    }

    // Endpoint: GET /api/alunos/cpf/{cpf}
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Aluno> buscarPorCpf(@PathVariable String cpf) {
        try {
            Aluno aluno = alunoService.buscarPorCpf(cpf);
            return ResponseEntity.ok(aluno);
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint: GET /api/alunos/matricula/{matricula}
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Aluno> buscarPorMatricula(@PathVariable String matricula) {
        try {
            Aluno aluno = alunoService.buscarPorMatricula(matricula);
            return ResponseEntity.ok(aluno);
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint: POST /api/alunos
    @PostMapping
    public ResponseEntity<Aluno> criar(@RequestBody Aluno aluno) { // @RequestBody para pegar o corpo da requisição
        try {
            Aluno alunoSalvo = alunoService.salvar(aluno);
            return ResponseEntity.ok(alunoSalvo); // Retorna 200 OK com o aluno criado
        } catch (Exception e) {
            // Pode ser refinado para capturar exceções específicas do serviço (ex: CpfDuplicadoException)
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request em caso de erro genérico
        }
    }

    // Endpoint: PUT /api/alunos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizar(@PathVariable Long id, @RequestBody Aluno aluno) {
        try {
            Aluno alunoAtualizado = alunoService.atualizar(id, aluno);
            return ResponseEntity.ok(alunoAtualizado); // Retorna 200 OK com o aluno atualizado
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found se o aluno não existir
        } catch (Exception e) {
            // Pode ser refinado para capturar exceções específicas do serviço (ex: CpfDuplicadoException)
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request em caso de erro genérico
        }
    }

    // Endpoint: DELETE /api/alunos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean deletado = alunoService.deletar(id);
        if (deletado) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found se o aluno não existir
        }
    }
}
