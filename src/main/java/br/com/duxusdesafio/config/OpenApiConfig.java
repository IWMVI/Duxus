package br.com.duxusdesafio.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentacaoDaApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Escala")
                        .description(
                                "API para cadastro de integrantes, montagem de times "
                                        + "e processamento dos dados das escalações."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Duxus")
                                .url("https://www.duxus.com.br"))
                        .license(new License()
                                .name("Uso interno")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação do desafio e instruções de execução")
                        .url("/"));
    }
}
