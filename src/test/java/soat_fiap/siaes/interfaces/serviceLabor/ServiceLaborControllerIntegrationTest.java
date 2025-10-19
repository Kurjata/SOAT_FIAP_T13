package soat_fiap.siaes.interfaces.serviceLabor;

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
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.repository.ServiceLaborRepository;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborRequest;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborResponse;
import soat_fiap.siaes.shared.utils.JsonPageUtils;

import java.math.BigDecimal;
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
class ServiceLaborControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceLaborRepository serviceLaborRepository;

    @Test
    void findAll__should_return_empty_page_when_no_service_labor() throws Exception {
        var response = mockMvc.perform(get("/service-labor"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findAll__should_return_page_of_service_labor_when_exists() throws Exception {
        ServiceLabor labor1 = new ServiceLabor("Troca de óleo", new BigDecimal("150"));
        ServiceLabor labor2 = new ServiceLabor("Alinhamento", new BigDecimal("100"));
        ServiceLaborResponse response1 = new ServiceLaborResponse(serviceLaborRepository.save(labor1));
        ServiceLaborResponse response2 = new ServiceLaborResponse(serviceLaborRepository.save(labor2));

        var response = mockMvc.perform(get("/service-labor"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        List<ServiceLaborResponse> labors = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), ServiceLaborResponse.class);
        assertThat(labors).containsExactlyInAnyOrder(response1, response2);
    }

    @Test
    void findById__should_return_service_labor_when_found() throws Exception {
        ServiceLabor labor = new ServiceLabor("Troca de óleo", new BigDecimal("150.00"));
        ServiceLabor savedLabor = serviceLaborRepository.save(labor);

        var response = mockMvc.perform(get("/service-labor/{id}", savedLabor.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ServiceLaborResponse laborResponse = objectMapper.readValue(response.getContentAsString(), ServiceLaborResponse.class);
        assertEquals(savedLabor.getIdAsString(), laborResponse.id());
        assertEquals("Troca de óleo", laborResponse.description());
        assertEquals(new BigDecimal("150.00"), laborResponse.laborCost());
    }

    @Test
    void findById__should_return_not_found_when_service_labor_does_not_exist() throws Exception {
        UUID laborId = UUID.randomUUID();

        var response = mockMvc.perform(get("/service-labor/{id}", laborId))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Serviço de mão de obra com ID " + laborId + " não encontrado"));
    }

    @Test
    void save__should_persist_service_labor_and_return_created() throws Exception {
        var request = new ServiceLaborRequest("Troca de óleo", new BigDecimal("150.00"));

        var response = mockMvc.perform(post("/service-labor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertThat(serviceLaborRepository.existsByDescription("Troca de óleo")).isTrue();
        assertEquals(201, response.getStatus());

        ServiceLaborResponse laborResponse = objectMapper.readValue(response.getContentAsString(), ServiceLaborResponse.class);
        assertEquals("Troca de óleo", laborResponse.description());
        assertEquals(new BigDecimal("150.00"), laborResponse.laborCost());
        assertNotNull(laborResponse.id());
    }

    @Test
    void delete__should_remove_service_labor_when_exists() throws Exception {
        ServiceLabor labor = new ServiceLabor("Alinhamento", new BigDecimal("100.00"));
        ServiceLabor savedLabor = serviceLaborRepository.save(labor);

        var response = mockMvc.perform(delete("/service-labor/" + savedLabor.getId()))
                .andReturn()
                .getResponse();

        assertEquals(204, response.getStatus());
        assertTrue(serviceLaborRepository.findById(savedLabor.getId()).isEmpty());
    }

    @Test
    void update__should_return_updated_service_labor_when_successful() throws Exception {
        ServiceLabor labor = new ServiceLabor("Troca de óleo", new BigDecimal("150.00"));
        ServiceLabor savedLabor = serviceLaborRepository.save(labor);

        var updateRequest = new ServiceLaborRequest("Troca de óleo premium", new BigDecimal("200.00"));

        var response = mockMvc.perform(put("/service-labor/" + savedLabor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ServiceLaborResponse laborResponse = objectMapper.readValue(response.getContentAsString(), ServiceLaborResponse.class);
        assertEquals(savedLabor.getIdAsString(), laborResponse.id());
        assertEquals("Troca de óleo premium", laborResponse.description());
        assertEquals(new BigDecimal("200.00"), laborResponse.laborCost());
    }

    @Test
    void update__should_return_not_found_when_service_labor_does_not_exist() throws Exception {
        UUID laborId = UUID.randomUUID();
        var updateRequest = new ServiceLaborRequest("Troca de óleo premium", new BigDecimal("200.00"));

        var response = mockMvc.perform(put("/service-labor/" + laborId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Serviço de mão de obra com ID " + laborId + " não encontrado"));
    }
}