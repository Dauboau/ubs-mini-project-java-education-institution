package daniel.contente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CreateAlunoRequestDTO {
    @Schema(example = "Fulano da Silva")
    @NotBlank(message = "Nome é obrigatório")
    public String nome;

    @Schema(example = "18316100860")
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos numéricos")
    public String cpf;

    @Schema(example = "fulano@gmail.com")
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    public String email;

    @Schema(example = "+5511999999999")
    @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "Telefone inválido — use formato 55DDDNXXXXXXXX")
    public String telefone;

    public @Valid EnderecoDTO endereco;

    @Schema(example = "12547784")
    @NotBlank(message = "Matrícula é obrigatória")
    @Pattern(regexp = "^\\d+$", message = "Número deve conter apenas dígitos numéricos")
    public String matricula;
}
