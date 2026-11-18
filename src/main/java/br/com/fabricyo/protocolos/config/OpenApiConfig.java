package br.com.fabricyo.protocolos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI protocolosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestão de Protocolos")
                        .description("Gestão de protocolos de atendimento: abertura, "
                                + "acompanhamento, filtros e encerramento (concluir/cancelar).")
                        .version("v1"));
    }
}
