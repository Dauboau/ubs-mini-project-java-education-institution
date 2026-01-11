package daniel.contente.dto;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{8}$", message = "CEP deve conter exatamente 8 dígitos numéricos")
    public String cep;

    @Hidden
    @Null(message = "Cidade não deve ser fornecida")
    public String cidade;

    @Hidden
    @Null(message = "Estado não deve ser fornecido")
    public String estado;

    @Hidden
    @Null(message = "Bairro não deve ser fornecido")
    public String bairro;

    @Hidden
    @Null(message = "Logradouro não deve ser fornecido")
    public String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Pattern(regexp = "^\\d+$", message = "Número deve conter apenas dígitos numéricos")
    public String numero;
}