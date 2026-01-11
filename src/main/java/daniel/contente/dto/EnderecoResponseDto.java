package daniel.contente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class EnderecoResponseDto {
    @Schema(example = "01002900")
    public String cep;

    @Schema(example = "São Paulo")
    public String cidade;

    @Schema(example = "SP")
    public String estado;

    @Schema(example = "Centro")
    public String bairro;

    @Schema(example = "Viaduto do Chá")
    public String logradouro;

    @Schema(example = "15")
    public String numero;
}