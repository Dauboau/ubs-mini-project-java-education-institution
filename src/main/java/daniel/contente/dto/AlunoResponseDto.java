package daniel.contente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AlunoResponseDto {

    @Schema(example = "1")
    public Long id;

    @Schema(example = "Fulano da Silva")
    public String nome;

    @Schema(example = "18316100860")
    public String cpf;

    @Schema(example = "fulano@gmail.com")
    public String email;

    @Schema(example = "+5511999999999")
    public String telefone;

    public EnderecoResponseDto endereco;

    @Schema(example = "12547784")
    public String matricula;
    
}
