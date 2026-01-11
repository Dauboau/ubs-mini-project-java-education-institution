package daniel.contente.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ProfessorResponseDto {
    public Long id;
    public String nome;
    public String cpf;
    public String email;
    public String telefone;
    public EnderecoResponseDto endereco;
    public String departamento;
}
