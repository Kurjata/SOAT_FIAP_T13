package soat_fiap.siaes.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // Contatos adicionais
        List<Contact> contacts = List.of(
                new Contact().name("Douglas Andrade Severa").email("douglas.severa96@gmail.com").url("https://github.com/Kurjata/SOAT_FIAP_T13.git"),
                new Contact().name("Edmar Dias Santos").email("edmarsantos56201@gmail.com"),
                new Contact().name("Vinícius Louzada Valente").email("vinicius.louzada@alura.com.br"),
                new Contact().name("Rodrigo Luiz Santos Alves").email("rodrigo.rlsa@outlook.com.br"),
                new Contact().name("Felipe Martines Kurjata").email("fkurjata@gmail.com")
        );

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("Bearer")
                                        .in(SecurityScheme.In.HEADER)
                                        .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("Siaes API")
                        .version("1")
                        .description("Project Tech Challenge - FIAP")
                        .contact(contacts.get(0)) // Swagger exige 1 contato principal :/
                        .extensions(Map.of("x-additional-contacts", contacts)) // adiciona múltiplos contatos como extensão :D
                );
    }
}
