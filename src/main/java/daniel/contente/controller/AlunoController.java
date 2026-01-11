package daniel.contente.controller;

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
        Aluno aluno = alunoService.buscarPorId(id);
        return ResponseEntity.ok(aluno);
    }

    // Endpoint: GET /api/alunos/cpf/{cpf}
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Aluno> buscarPorCpf(@PathVariable String cpf) {
        Aluno aluno = alunoService.buscarPorCpf(cpf);
        return ResponseEntity.ok(aluno);
    }

    // Endpoint: GET /api/alunos/matricula/{matricula}
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Aluno> buscarPorMatricula(@PathVariable String matricula) {
        Aluno aluno = alunoService.buscarPorMatricula(matricula);
        return ResponseEntity.ok(aluno);
    }

    // Endpoint: POST /api/alunos
    @PostMapping
    public ResponseEntity<Aluno> criar(@RequestBody Aluno aluno) { // @RequestBody para pegar o corpo da requisição
        Aluno alunoSalvo = alunoService.salvar(aluno);
        return ResponseEntity.ok(alunoSalvo);
    }

    // Endpoint: PUT /api/alunos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizar(@PathVariable Long id, @RequestBody Aluno aluno) {
        Aluno alunoAtualizado = alunoService.atualizar(id, aluno);
        return ResponseEntity.ok(alunoAtualizado);
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
