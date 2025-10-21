package soat_fiap.siaes.interfaces.inventory;

import com.fasterxml.jackson.core.type.TypeReference;
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
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;
import soat_fiap.siaes.domain.inventory.repository.PartRepository;
import soat_fiap.siaes.interfaces.inventory.dto.AddStockRequest;
import soat_fiap.siaes.interfaces.inventory.dto.CreatePartRequest;
import soat_fiap.siaes.interfaces.inventory.dto.PartResponse;
import soat_fiap.siaes.interfaces.inventory.dto.UpdatePartRequest;
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
class PartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PartRepository partRepository;

    @Test
    void findAll__should_return_empty_page_when_no_parts_exist() throws Exception {
        var response = mockMvc.perform(get("/parts"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findAll__should_return_page_with_existing_parts() throws Exception {
        Part part1 = partRepository.save(new Part("Filtro de Óleo", new BigDecimal("35"), UnitMeasure.UNIT, 10, 2, "1234567890123", "top", 5));
        Part part2 = partRepository.save(new Part("Pastilha de Freio", new BigDecimal("85"), UnitMeasure.UNIT, 8, 1, "9876543210987", "top", 4));

        var response = mockMvc.perform(get("/parts"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        List<PartResponse> parts = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), PartResponse.class);

        assertThat(parts)
                .containsExactlyInAnyOrder(new PartResponse(part1), new PartResponse(part2));
    }

    @Test
    void findById__should_return_part_when_found() throws Exception {
        Part part = partRepository.save(new Part("Filtro de Óleo", new BigDecimal("35"), UnitMeasure.UNIT, 10, 2, "1234567890123", "top", 5));

        var response = mockMvc.perform(get("/parts/" + part.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        PartResponse partResponse = objectMapper.readValue(response.getContentAsString(), PartResponse.class);

        assertEquals(part.getIdAsString(), partResponse.id());
        assertEquals("Filtro de Óleo", partResponse.name());
    }

    @Test
    void findById__should_return_not_found_when_part_does_not_exist() throws Exception {
        UUID id = UUID.randomUUID();

        var response = mockMvc.perform(get("/parts/" + id))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Peça não encontrada com ID: " + id));
    }

    @Test
    void save__should_persist_part_and_return_created_response() throws Exception {
        CreatePartRequest request = new CreatePartRequest(10, "12323456789012", "Top", 5, "Filtro de óleo", BigDecimal.valueOf(20), UnitMeasure.UNIT, 0);;

        var response = mockMvc.perform(post("/parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(201, response.getStatus());
        assertTrue(partRepository.existsByEan("12323456789012"));
    }

    @Test
    void update__should_return_updated_part_when_successful() throws Exception {
        Part part = partRepository.save(new Part("Filtro de Óleo", new BigDecimal("35"), UnitMeasure.UNIT, 10, 2, "1234567890123", "top", 5));

        UpdatePartRequest updateRequest = new UpdatePartRequest("Correia Dentada Premium", new BigDecimal("15"), UnitMeasure.UNIT,
                14, 1, 4, "5555555555555", "Gates");

        var response = mockMvc.perform(put("/parts/" + part.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        PartResponse updatedResponse = objectMapper.readValue(response.getContentAsString(), PartResponse.class);
        assertEquals("Correia Dentada Premium", updatedResponse.name());
        assertEquals(new BigDecimal("15"), updatedResponse.unitPrice());
    }

    @Test
    void delete__should_remove_Stock_part_when_exists() throws Exception {
        Part part = partRepository.save(new Part("Filtro de Óleo", new BigDecimal("35"), UnitMeasure.UNIT, 10, 2, "1234567890123", "top", 5));

        var response = mockMvc.perform(delete("/parts/" + part.getId()))
                .andReturn()
                .getResponse();

        assertEquals(204, response.getStatus());
        assertFalse(partRepository.existsById(part.getId()));
    }

    @Test
    void moveToAvailable__should_increase_quantity_when_valid() throws Exception {
        Part part = partRepository.save(new Part("Filtro de Óleo", new BigDecimal("35"), UnitMeasure.UNIT, 10, 2, "1234567890123", "top", 5));
        AddStockRequest request = new AddStockRequest(5);

        var response = mockMvc.perform(patch("/parts/" + part.getId() + "/stock/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        Part updatedPart = partRepository.findById(part.getId()).orElseThrow();
        assertEquals(15, updatedPart.getQuantity());
    }

    @Test
    void adjustStock__should_set_new_quantity() throws Exception {
        Part part = partRepository.save(new Part("Filtro de Óleo", new BigDecimal("35"), UnitMeasure.UNIT, 10, 2, "1234567890123", "top", 5));

        var response = mockMvc.perform(patch("/parts/" + part.getId() + "/stock/adjust")
                        .param("quantity", "7"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        Part updatedPart = partRepository.findById(part.getId()).orElseThrow();
        assertEquals(17, updatedPart.getQuantity());
    }

    @Test
    void findAllBelowMinimumStock__should_return_parts_below_minimum() throws Exception {
        partRepository.save(new Part("Filtro", new BigDecimal("20.00"), UnitMeasure.UNIT, 2, 0, "3333333333333", "Bosch", 5));
        partRepository.save(new Part("Óleo", new BigDecimal("40.00"), UnitMeasure.UNIT, 10, 0, "4444444444444", "Shell", 5));

        var response = mockMvc.perform(get("/parts/stock/below-minimum"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        List<PartResponse> parts = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertThat(parts)
                .extracting(PartResponse::name)
                .containsExactly("Filtro");
    }
}