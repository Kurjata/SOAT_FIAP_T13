package soat_fiap.siaes.interfaces.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.model.document.DocumentFactory;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.interfaces.user.dto.CreateUserRequest;
import soat_fiap.siaes.interfaces.user.dto.UpdateUserRequest;
import soat_fiap.siaes.interfaces.user.dto.UserResponse;
import soat_fiap.siaes.shared.utils.JsonPageUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAll__should_return_empty_page_when_no_users() throws Exception {
        var response = mockMvc.perform(get("/users"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findAll__should_return_page_of_users_when_has_users() throws Exception {
        User user1 = new User("Jill Valentine", "valentine", "nemesis", RoleEnum.CLIENT, "384.088.920-02", "jill@email.com");
        User user2 = new User("Ada Wong", "wong", "leon", RoleEnum.CLIENT, "240.631.510-00", "ada@email.com");

        UserResponse userResponse1 = new UserResponse(userRepository.save(user1));
        UserResponse userResponse2 = new UserResponse(userRepository.save(user2));

        var response = mockMvc.perform(get("/users"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        List<UserResponse> users = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), UserResponse.class);
        assertThat(users).containsExactlyInAnyOrder(userResponse1, userResponse2);
    }

    @Test
    void findByDocument__should_return_user_when_found() throws Exception {
        User user = new User("Jill Valentine", "valentine", "nemesis", RoleEnum.CLIENT, "384.088.920-02", "jill@email.com");
        userRepository.save(user);

        var response = mockMvc.perform(get("/users/{document}", "38408892002"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        UserResponse userResponse = objectMapper.readValue(response.getContentAsString(), UserResponse.class);
        assertEquals("Jill Valentine", userResponse.name());
        assertEquals("jill@email.com", userResponse.email());
        assertEquals("384.088.920-02", userResponse.document());
    }

    @Test
    void findByDocument__should_return_not_found_when_user_does_not_exist() throws Exception {
        var response = mockMvc.perform(get("/users/{document}", "38408892002"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Usuário com documento 38408892002 não encontrado"));
    }

    @Test
    void save__should_persist_user_and_return_created_response() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "Vinicius L",
                "vinelouzada",
                "senha123",
                "384.088.920-02",
                "vinicius@email.com"
        );
        var response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(201, response.getStatus());
        assertThat(userRepository.findByDocument(DocumentFactory.fromString("384.088.920-02"))).isPresent();

        UserResponse userResponse = objectMapper.readValue(response.getContentAsString(), UserResponse.class);
        assertEquals("Vinicius L", userResponse.name());
        assertEquals("384.088.920-02", userResponse.document());
        assertEquals("vinicius@email.com", userResponse.email());
        assertEquals(RoleEnum.CLIENT.toString(), userResponse.role());
    }

    @Test
    void save__should_return_bad_request_when_document_already_exists() throws Exception {
        User user = new User("Jill Valentine", "valentine", "nemesis", RoleEnum.CLIENT, "384.088.920-02", "jill@email.com");
        userRepository.save(user);

        CreateUserRequest request = new CreateUserRequest(
                "Vinicius L",
                "vinelouzada",
                "senha123",
                "384.088.920-02",
                "vinicius@email.com"
        );
        var response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("Documento 384.088.920-02 ou login vinelouzada já cadastrado"));
    }

    @Test
    void delete__should_remove_user_when_exists() throws Exception {
        User user = new User("Jill Valentine", "valentine", "nemesis", RoleEnum.CLIENT, "384.088.920-02", "jill@email.com");
        User savedUser = userRepository.save(user);

        var response = mockMvc.perform(delete("/users/" + savedUser.getId()))
                .andReturn()
                .getResponse();

        assertEquals(204, response.getStatus());
        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void delete__should_return_not_found_when_user_does_not_exist() throws Exception {
        UUID userId = UUID.randomUUID();

        var response = mockMvc.perform(delete("/users/" + userId))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Usuário não encontrado"));
    }

    @Test
    void update__should_return_updated_user_when_successful() throws Exception {
        User user = new User("Jill Valentine", "valentine", "nemesis", RoleEnum.CLIENT, "384.088.920-02", "jill@email.com");
        User savedUser = userRepository.save(user);

        var updateRequest = new UpdateUserRequest("Lucas Silva", "941.016.760-46", RoleEnum.CLIENT);

        var response = mockMvc.perform(put("/users/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        UserResponse updatedResponse = objectMapper.readValue(response.getContentAsString(), UserResponse.class);
        assertEquals(savedUser.getId().toString(), updatedResponse.id());
        assertEquals("Lucas Silva", updatedResponse.name());
        assertEquals("941.016.760-46", updatedResponse.document());
        assertEquals(RoleEnum.CLIENT.toString(), updatedResponse.role());
    }

    @Test
    void update__should_return_not_found_when_user_does_not_exist() throws Exception {
        UUID userId = UUID.randomUUID();
        var updateRequest = new UpdateUserRequest("Name", "email@email.com", RoleEnum.CLIENT);

        var response = mockMvc.perform(put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Usuário não encontrado"));
    }
}
