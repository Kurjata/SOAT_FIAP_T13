package soat_fiap.siaes.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import soat_fiap.siaes.rest.dto.LoginRequest;
import soat_fiap.siaes.rest.dto.RefreshTokenDTO;
import soat_fiap.siaes.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
@SecurityRequirement(name = "bearer-key")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autenticação de usuário",
            description = "Este endpoint realiza a autenticação do usuário utilizando suas credenciais (login e senha) e, em caso de sucesso, retorna um token JWT para utilização nas requisições subsequentes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso. O corpo da resposta contém o token JWT."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida. Pode ocorrer se os dados obrigatórios não forem fornecidos ou estiverem mal formatados."),
            @ApiResponse(responseCode = "401", description = "Falha na autenticação. Usuário ou senha inválidos."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao processar a autenticação.")
    })
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return authenticationService.authenticate(loginRequest);
    }
}
