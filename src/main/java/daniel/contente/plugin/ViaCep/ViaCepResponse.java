package daniel.contente.plugin.ViaCep;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ViaCepResponse {

    @JsonProperty("cep")
    public String cep;

    @JsonProperty("logradouro")
    public String logradouro;

    @JsonProperty("bairro")
    public String bairro;

    @JsonProperty("localidade")
    public String cidade;

    @JsonProperty("uf")
    public String estado;

}