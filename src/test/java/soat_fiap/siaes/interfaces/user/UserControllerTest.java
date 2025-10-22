package soat_fiap.siaes.interfaces.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.model.document.DocumentFactory;
import soat_fiap.siaes.domain.user.service.UserService;
import soat_fiap.siaes.interfaces.user.dto.CreateUserRequest;
import soat_fiap.siaes.interfaces.user.dto.UpdateUserRequest;
import soat_fiap.siaes.interfaces.user.dto.UserResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll__should_return_page_of_users() throws Exception {
        User user = createMockUser("384.088.920-02", "Vinicius L", "vinicius@email.com");

        Page<User> page = new PageImpl<>(List.of(user));
        when(userService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/users"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("Vinicius L"));
        assertTrue(response.getContentAsString().contains("384.088.920-02"));
        assertTrue(response.getContentAsString().contains("vinicius@email.com"));
    }

    @Test
    void findAll__should_return_empty_page_when_no_users() throws Exception {
        Page<User> page = new PageImpl<>(List.of());
        when(userService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/users"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findByDocument__should_return_user_when_found() throws Exception {
        String document = "384.088.920-02";
        User user = createMockUser(document, "Vinicius L", "vinicius@email.com");

        when(userService.findByDocument(document)).thenReturn(user);

        var response = mockMvc.perform(get("/users/" + document))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("Vinicius L"));
        assertTrue(response.getContentAsString().contains("384.088.920-02"));
        assertTrue(response.getContentAsString().contains("vinicius@email.com"));
    }

    @Test
    void findByDocument__should_return_not_found_when_user_not_found() throws Exception {
        String document = "999.999.999-99";
        when(userService.findByDocument(document))
                .thenThrow(new EntityNotFoundException("Usuário não encontrado com documento: " + document));

        var response = mockMvc.perform(get("/users/" + document))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void save__should_return_code_bad_request_when_body_is_empty() throws Exception {
        var response = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
        ).andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void save__should_return_code_created_when_body_is_valid_and_saved() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "Vinicius L",
                "vinelouzada",
                "senha123",
                "384.088.920-02",
                "vinicius@email.com"
        );

        User userCreated = createMockUser("384.088.920-02", "Vinicius L", "vinicius@email.com");
        when(userService.save(any(CreateUserRequest.class))).thenReturn(userCreated);

        var response = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(201, response.getStatus());
        assertEquals(new UserResponse(userCreated), objectMapper.readValue(response.getContentAsString(), UserResponse.class));
    }

    @Test
    void deleteById__should_return_no_content_when_user_deleted() throws Exception {
        UUID id = UUID.randomUUID();

        var response = mockMvc.perform(delete("/users/" + id))
                .andReturn().getResponse();

        assertEquals(204, response.getStatus());
    }

    @Test
    void deleteById__should_return_not_found_when_user_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Usuário não encontrado com id: " + id))
                .when(userService)
                .deleteById(id);

        var response = mockMvc.perform(delete("/users/" + id))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void update__should_return_updated_user_when_successful() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest(
                "Vinicius Louzada",
                "vinicius@email.com",
                RoleEnum.ADMIN
        );

        User updatedUser = createMockUser("384.088.920-02", "Vinicius Louzada", "vinicius@email.com");
        when(userService.update(eq(id), any(UpdateUserRequest.class))).thenReturn(updatedUser);

        var response = mockMvc.perform(
                put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertEquals(new UserResponse(updatedUser), objectMapper.readValue(response.getContentAsString(), UserResponse.class));
    }

    @Test
    void update__should_return_not_found_when_user_not_exists() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest(
                "Vinicius Louzada",
                "vinicius@email.com",
                RoleEnum.ADMIN
        );

        when(userService.update(eq(id), any(UpdateUserRequest.class)))
                .thenThrow(new EntityNotFoundException("Usuário não encontrado com id: " + id));

        var response = mockMvc.perform(
                put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void update__should_return_code_bad_request_when_body_is_invalid() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("", "", null);

        var response = mockMvc.perform(
                put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }

    private User createMockUser(String document, String name, String email) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(user.getDocument()).thenReturn(DocumentFactory.fromString(document));
        when(user.getDocumentAsString()).thenReturn(document);
        when(user.getName()).thenReturn(name);
        when(user.getEmail()).thenReturn(email);
        when(user.getRole()).thenReturn(RoleEnum.CLIENT);
        return user;
    }
}