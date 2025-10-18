package soat_fiap.siaes.interfaces.inventory;

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
import soat_fiap.siaes.domain.inventory.model.Supply;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;
import soat_fiap.siaes.domain.inventory.repository.SupplyRepository;
import soat_fiap.siaes.interfaces.inventory.dto.CreateSupplyRequest;
import soat_fiap.siaes.interfaces.inventory.dto.SupplyResponse;
import soat_fiap.siaes.interfaces.inventory.dto.UpdateSupplyAvailableRequest;
import soat_fiap.siaes.interfaces.inventory.dto.UpdateSupplyRequest;
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
class SupplyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SupplyRepository supplyRepository;


    @Test
    void findAll__should_return_empty_page_when_no_supply() throws Exception {
        var response = mockMvc.perform(get("/supply"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findAll__should_return_page_of_supply_when_exists() throws Exception {
        Supply supply1 = new Supply("Óleo 10W40", new BigDecimal("50"), UnitMeasure.LT, "Lubrax", true);
        Supply supply2 = new Supply("Filtro de ar", new BigDecimal("30"), UnitMeasure.LT, "Bosch", true);
        SupplyResponse response1 = new SupplyResponse(supplyRepository.save(supply1));
        SupplyResponse response2 = new SupplyResponse(supplyRepository.save(supply2));

        var response = mockMvc.perform(get("/supply"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        List<SupplyResponse> supplies = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), SupplyResponse.class);
        assertThat(supplies).containsExactlyInAnyOrder(response1, response2);
    }

    @Test
    void findById__should_return_supply_when_found() throws Exception {
        Supply supply = new Supply("Óleo 10W40", new BigDecimal("50"), UnitMeasure.LT, "Lubrax", true);
        Supply saved = supplyRepository.save(supply);

        var response = mockMvc.perform(get("/supply/{id}", saved.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        SupplyResponse supplyResponse = objectMapper.readValue(response.getContentAsString(), SupplyResponse.class);
        assertEquals(saved.getId().toString(), supplyResponse.id());
        assertEquals("Óleo 10W40", supplyResponse.name());
        assertEquals(new BigDecimal("50"), supplyResponse.unitPrice());
        assertEquals(UnitMeasure.LT, supplyResponse.unitMeasure());
        assertEquals("Lubrax", supplyResponse.supplier());
        assertTrue(supplyResponse.available());
    }

    @Test
    void findById__should_return_not_found_when_supply_does_not_exist() throws Exception {
        UUID id = UUID.randomUUID();

        var response = mockMvc.perform(get("/supply/{id}", id))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Insumo não encontrado com ID: " + id));
    }

    @Test
    void save__should_persist_supply_and_return_created() throws Exception {
        var request = new CreateSupplyRequest("Óleo 10W40", "Lubrax", new BigDecimal("50"), UnitMeasure.LT, true);

        var response = mockMvc.perform(post("/supply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        SupplyResponse supplyResponse = objectMapper.readValue(response.getContentAsString(), SupplyResponse.class);
        assertEquals("Óleo 10W40", supplyResponse.name());
        assertEquals("Lubrax", supplyResponse.supplier());
        assertEquals(new BigDecimal("50"), supplyResponse.unitPrice());
        assertEquals(UnitMeasure.LT, supplyResponse.unitMeasure());
        assertTrue(supplyResponse.available());
    }

    @Test
    void delete__should_remove_supply_when_exists() throws Exception {
        Supply supply = new Supply("Filtro de ar", new BigDecimal("30"), UnitMeasure.UNIT, "Bosch", true);
        Supply saved = supplyRepository.save(supply);

        var response = mockMvc.perform(delete("/supply/" + saved.getId()))
                .andReturn()
                .getResponse();

        assertEquals(204, response.getStatus());
        assertTrue(supplyRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    void update__should_return_updated_supply_when_successful() throws Exception {
        Supply supply = new Supply("Filtro de ar", new BigDecimal("30"), UnitMeasure.UNIT, "Bosch", true);
        Supply saved = supplyRepository.save(supply);

        var updateRequest = new UpdateSupplyRequest("Filtro de ar esportivo", new BigDecimal("45"), UnitMeasure.UNIT, "Bosch", false);

        var response = mockMvc.perform(put("/supply/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        SupplyResponse updated = objectMapper.readValue(response.getContentAsString(), SupplyResponse.class);
        assertEquals(saved.getId().toString(), updated.id());
        assertEquals("Filtro de ar esportivo", updated.name());
        assertEquals(new BigDecimal("45"), updated.unitPrice());
        assertEquals(UnitMeasure.UNIT, updated.unitMeasure());
        assertEquals("Bosch", updated.supplier());
        assertFalse(updated.available());
    }

    @Test
    void update__should_return_not_found_when_supply_does_not_exist() throws Exception {
        UUID id = UUID.randomUUID();
        var updateRequest = new UpdateSupplyRequest("Filtro de ar esportivo", new BigDecimal("45.00"), UnitMeasure.UNIT, "Bosch", false);

        var response = mockMvc.perform(put("/supply/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Insumo não encontrado com ID: " + id));
    }

    @Test
    void updateAvailability__should_update_availability_field_only() throws Exception {
        Supply supply = new Supply("Filtro de ar", new BigDecimal("30"), UnitMeasure.UNIT, "Bosch", true);
        Supply saved = supplyRepository.save(supply);

        var request = new UpdateSupplyAvailableRequest(false);

        var response = mockMvc.perform(patch("/supply/" + saved.getId() + "/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        SupplyResponse updated = objectMapper.readValue(response.getContentAsString(), SupplyResponse.class);
        assertFalse(updated.available());
        assertEquals(saved.getName(), updated.name());
    }
}
