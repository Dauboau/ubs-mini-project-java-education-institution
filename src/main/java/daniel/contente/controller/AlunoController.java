package daniel.contente.controller;

import daniel.contente.dto.AlunoRequestDto;
import daniel.contente.dto.AlunoResponseDto;
import daniel.contente.mapper.AlunoMapper;
import daniel.contente.model.Aluno;
import daniel.contente.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Alunos", description = "Operações relacionadas a alunos")
@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @GetMapping
    @Operation(summary = "Listar todos os alunos")
    public ResponseEntity<List<AlunoResponseDto>> listarTodos() {
        List<Aluno> alunos = alunoService.listarTodos();
        List<AlunoResponseDto> dtos = alunos.stream()
                .map(AlunoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aluno por ID")
    public ResponseEntity<AlunoResponseDto> buscarPorId(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarPorId(id);
        return ResponseEntity.ok(AlunoMapper.toResponse(aluno));
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar aluno por CPF")
    public ResponseEntity<AlunoResponseDto> buscarPorCpf(@PathVariable String cpf) {
        Aluno aluno = alunoService.buscarPorCpf(cpf);
        return ResponseEntity.ok(AlunoMapper.toResponse(aluno));
    }

    @GetMapping("/matricula/{matricula}")
    @Operation(summary = "Buscar aluno por matrícula")
    public ResponseEntity<AlunoResponseDto> buscarPorMatricula(@PathVariable String matricula) {
        Aluno aluno = alunoService.buscarPorMatricula(matricula);
        return ResponseEntity.ok(AlunoMapper.toResponse(aluno));
    }

    @PostMapping
    @Operation(summary = "Criar novo aluno")
    public ResponseEntity<AlunoResponseDto> criar(@RequestBody @Valid AlunoRequestDto alunoDto) {
        Aluno alunoSalvo = alunoService.salvar(alunoDto);
        return ResponseEntity.ok(AlunoMapper.toResponse(alunoSalvo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar aluno")
    public ResponseEntity<AlunoResponseDto> atualizar(@PathVariable Long id, @RequestBody @Valid AlunoRequestDto alunoDto) {
        Aluno alunoAtualizado = alunoService.atualizar(id, alunoDto);
        return ResponseEntity.ok(AlunoMapper.toResponse(alunoAtualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar aluno")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean deletado = alunoService.deletar(id);
        if (deletado) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found se o aluno não existir
        }
    }
}
