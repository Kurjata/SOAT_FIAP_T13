package soat_fiap.siaes.rest.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Tag(name = "Apenas para testar as roles")
@SecurityRequirement(name = "bearer-key")
public class TestRoleController {

    @GetMapping("/public/teste")
    public ResponseEntity<String> testePublico() {
        return ResponseEntity.ok("Público: qualquer usuário pode acessar");
    }

    @GetMapping("/monitoramento/teste")
    public ResponseEntity<String> testeProtegido() {
        return ResponseEntity.ok("Exclusivo: apenas ADMIN pode acessar");
    }

    @GetMapping("/ordens/teste")
    public ResponseEntity<String> testeColaborador() {
        return ResponseEntity.ok("Protegido: apenas ADMIN ou COLLABORATOR podem acessar");
    }

}
