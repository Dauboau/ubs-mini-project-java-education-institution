package daniel.contente.controller;

import daniel.contente.dto.ProfessorRequestDto;
import daniel.contente.dto.ProfessorResponseDto;
import daniel.contente.mapper.ProfessorMapper;
import daniel.contente.model.Professor;
import daniel.contente.service.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Professores", description = "Operações relacionadas a professores")
@RestController
@RequestMapping("/professores")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @GetMapping
    @Operation(summary = "Listar todos os professores")
    public ResponseEntity<List<ProfessorResponseDto>> listarTodos() {
        List<Professor> professores = professorService.listarTodos();
        List<ProfessorResponseDto> dtos = professores.stream()
                .map(ProfessorMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar professor por ID")
    public ResponseEntity<ProfessorResponseDto> buscarPorId(@PathVariable Long id) {
        Professor professor = professorService.buscarPorId(id);
        return ResponseEntity.ok(ProfessorMapper.toResponse(professor));
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar professor por CPF")
    public ResponseEntity<ProfessorResponseDto> buscarPorCpf(@PathVariable String cpf) {
        Professor professor = professorService.buscarPorCpf(cpf);
        return ResponseEntity.ok(ProfessorMapper.toResponse(professor));
    }

    @GetMapping("/departamento/{departamento}")
    @Operation(summary = "Buscar professores por departamento")
    public ResponseEntity<List<ProfessorResponseDto>> buscarPorDepartamento(@PathVariable String departamento) {
        List<Professor> professores = professorService.buscarPorDepartamento(departamento);
        List<ProfessorResponseDto> dtos = professores.stream()
                .map(ProfessorMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Operation(summary = "Criar novo professor")
    public ResponseEntity<ProfessorResponseDto> criar(@RequestBody @Valid ProfessorRequestDto professorDto) {
        Professor professorSalvo = professorService.salvar(professorDto);
        return ResponseEntity.ok(ProfessorMapper.toResponse(professorSalvo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar professor")
    public ResponseEntity<ProfessorResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid ProfessorRequestDto professorDto) {
        Professor professorAtualizado = professorService.atualizar(id, professorDto);
        return ResponseEntity.ok(ProfessorMapper.toResponse(professorAtualizado));
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
