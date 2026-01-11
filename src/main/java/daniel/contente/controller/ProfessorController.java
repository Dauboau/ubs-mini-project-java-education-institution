package daniel.contente.controller;

import daniel.contente.dto.CreateProfessorRequestDTO;
import daniel.contente.model.Professor;
import daniel.contente.service.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Professores", description = "Operações relacionadas a professores")
@RestController
@RequestMapping("/professores")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @GetMapping
    @Operation(summary = "Listar todos os professores")
    public ResponseEntity<List<Professor>> listarTodos() {
        List<Professor> professores = professorService.listarTodos();
        return ResponseEntity.ok(professores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar professor por ID")
    public ResponseEntity<Professor> buscarPorId(@PathVariable Long id) {
        Professor professor = professorService.buscarPorId(id);
        return ResponseEntity.ok(professor);
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar professor por CPF")
    public ResponseEntity<Professor> buscarPorCpf(@PathVariable String cpf) {
        Professor professor = professorService.buscarPorCpf(cpf);
        return ResponseEntity.ok(professor);
    }

    @GetMapping("/departamento/{departamento}")
    @Operation(summary = "Buscar professores por departamento")
    public ResponseEntity<List<Professor>> buscarPorDepartamento(@PathVariable String departamento) {
        List<Professor> professores = professorService.buscarPorDepartamento(departamento);
        return ResponseEntity.ok(professores);
    }

    @PostMapping
    @Operation(summary = "Criar novo professor")
    public ResponseEntity<Professor> criar(@RequestBody @Valid CreateProfessorRequestDTO professorDto) {
        Professor professorSalvo = professorService.salvar(professorDto);
        return ResponseEntity.ok(professorSalvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar professor")
    public ResponseEntity<Professor> atualizar(@PathVariable Long id, @RequestBody @Valid CreateProfessorRequestDTO professorDto) {
        Professor professorAtualizado = professorService.atualizar(id, professorDto);
        return ResponseEntity.ok(professorAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar professor")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean deletado = professorService.deletar(id);
        if (deletado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
