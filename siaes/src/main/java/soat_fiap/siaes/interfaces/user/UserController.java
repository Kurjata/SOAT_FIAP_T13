package soat_fiap.siaes.interfaces.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.service.UserService;
import soat_fiap.siaes.interfaces.user.dto.CreateUserRequest;
import soat_fiap.siaes.interfaces.user.dto.UserResponse;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> users = userService.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();

        return ResponseEntity.ok(users);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<UserResponse> save(@RequestBody @Valid CreateUserRequest request) {
        User user = userService.save(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponse(user));
    }
}