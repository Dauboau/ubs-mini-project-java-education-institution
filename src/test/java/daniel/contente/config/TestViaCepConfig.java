package daniel.contente.config;

import daniel.contente.plugin.ViaCep.ViaCepService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestViaCepConfig {
    @Bean
    public ViaCepService viaCepService() {
        return Mockito.mock(ViaCepService.class);
    }
}
