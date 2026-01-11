package daniel.contente.plugin.ViaCep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import daniel.contente.exception.RecursoNaoEncontradoException;

@Service
public class ViaCepService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String URL_VIACEP = "https://viacep.com.br/ws/{cep}/json/";

    public ViaCepResponse buscarEnderecoPorCep(String cep) {

        String url = URL_VIACEP.replace("{cep}", cep);

        try {
            ResponseEntity<ViaCepResponse> response = restTemplate.getForEntity(url, ViaCepResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                ViaCepResponse dados = response.getBody();
                if (dados != null && dados.getLogradouro() == null) { // ViaCEP retorna { "erro": true } como objeto com campos nulos
                    throw new RecursoNaoEncontradoException("CEP não encontrado na base da ViaCEP: " + cep);
                }
                return dados;
            } else {
                throw new RuntimeException("Erro ao buscar CEP na API externa. Código: " + response.getStatusCode().value());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Erro HTTP ao chamar a API ViaCEP: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Erro de rede ao chamar a API ViaCEP: " + e.getMessage());
        }

    }

}
