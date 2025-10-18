package soat_fiap.siaes.interfaces.serviceLabor;

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
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.service.ServiceLaborService;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborRequest;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
class ServiceLaborControllerTest {

    @MockitoBean
    private ServiceLaborService serviceLaborService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll__should_return_page_of_service_labor() throws Exception {
        UUID id = UUID.randomUUID();
        ServiceLabor labor = createMockServiceLabor(id, "Troca de óleo", new BigDecimal("150.00"));

        Page<ServiceLaborResponse> page = new PageImpl<>(List.of( new ServiceLaborResponse(labor)));
        when(serviceLaborService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/service-labor"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("Troca de óleo"));
        assertTrue(response.getContentAsString().contains(id.toString()));
        assertTrue(response.getContentAsString().contains("150.00"));
    }

    @Test
    void findAll__should_return_empty_page_when_no_service_labor() throws Exception {
        Page<ServiceLaborResponse> page = new PageImpl<>(List.of());
        when(serviceLaborService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/service-labor"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findById__should_return_service_labor_when_found() throws Exception {
        UUID id = UUID.randomUUID();
        ServiceLabor labor = createMockServiceLabor(id, "Troca de óleo", new BigDecimal("150.00"));

        var responseLabor = new ServiceLaborResponse(labor);
        when(serviceLaborService.findById(id)).thenReturn(responseLabor);

        var response = mockMvc.perform(get("/service-labor/" + id))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("Troca de óleo"));
        assertTrue(response.getContentAsString().contains(id.toString()));
        assertTrue(response.getContentAsString().contains("150.00"));
    }

    @Test
    void findById__should_return_code_not_found_when_service_labor_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        when(serviceLaborService.findById(id))
                .thenThrow(new EntityNotFoundException("Mão de obra não encontrada com id: " + id));

        var response = mockMvc.perform(get("/service-labor/" + id))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void save__should_return_code_bad_request_when_body_is_empty() throws Exception {
        var response = mockMvc.perform(
                        post("/service-labor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andReturn()
                .getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void save__should_return_code_created_when_body_is_valid_and_saved() throws Exception {
        ServiceLaborRequest request = new ServiceLaborRequest("Troca de óleo", new BigDecimal("150.00"));

        UUID id = UUID.randomUUID();
        ServiceLabor laborCreated = createMockServiceLabor(id, "Troca de óleo", new BigDecimal("150.00"));
        var responseLabor = new ServiceLaborResponse(laborCreated);
        when(serviceLaborService.save(any(ServiceLaborRequest.class)))
                .thenReturn(responseLabor);

        var response = mockMvc.perform(
                        post("/service-labor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andReturn()
                .getResponse();

        assertEquals(201, response.getStatus());
        assertTrue(response.getContentAsString().contains("Troca de óleo"));
        assertTrue(response.getContentAsString().contains(id.toString()));
        assertTrue(response.getContentAsString().contains("150.00"));
    }

    @Test
    void deleteById__should_return_code_no_content_when_deleted() throws Exception {
        UUID id = UUID.randomUUID();

        var response = mockMvc.perform(delete("/service-labor/" + id))
                .andReturn()
                .getResponse();

        assertEquals(204, response.getStatus());
    }

    @Test
    void deleteById__should_return_code_not_found_when_not_exists() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Mão de obra não encontrada com id: " + id))
                .when(serviceLaborService)
                .deleteById(id);

        var response = mockMvc.perform(delete("/service-labor/" + id))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void update__should_return_updated_service_labor_when_successful() throws Exception {
        ServiceLaborRequest request = new ServiceLaborRequest("Troca de óleo", new BigDecimal("200.00"));
        UUID id = UUID.randomUUID();
        ServiceLabor updatedLabor = createMockServiceLabor(id, "Troca de óleo", new BigDecimal("200.00"));

        var responseLabor = new ServiceLaborResponse(updatedLabor);

        when(serviceLaborService.update(eq(id), any(ServiceLaborRequest.class)))
                .thenReturn(responseLabor);

        var response = mockMvc.perform(
                        put("/service-labor/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("Troca de óleo"));
        assertTrue(response.getContentAsString().contains("200.00"));
        assertTrue(response.getContentAsString().contains(id.toString()));
    }

    @Test
    void update__should_return_not_found_when_service_labor_not_exists() throws Exception {
        UUID id = UUID.randomUUID();
        ServiceLaborRequest request = new ServiceLaborRequest("Troca de óleo", new BigDecimal("200.00"));

        when(serviceLaborService.update(eq(id), any(ServiceLaborRequest.class)))
                .thenThrow(new EntityNotFoundException("Mão de obra não encontrada com id: " + id));

        var response = mockMvc.perform(
                put("/service-labor/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void update__should_return_code_bad_request_when_body_is_invalid() throws Exception {
        UUID id = UUID.randomUUID();
        ServiceLaborRequest request = new ServiceLaborRequest("", null);

        var response = mockMvc.perform(
                        put("/service-labor/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andReturn()
                .getResponse();

        assertEquals(400, response.getStatus());
    }

    private ServiceLabor createMockServiceLabor(UUID id, String description, BigDecimal laborCost) {
        ServiceLabor labor = mock(ServiceLabor.class);
        when(labor.getId()).thenReturn(id);
        when(labor.getIdAsString()).thenReturn(id.toString());
        when(labor.getDescription()).thenReturn(description);
        when(labor.getLaborCost()).thenReturn(laborCost);
        return labor;
    }
}