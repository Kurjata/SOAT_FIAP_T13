package soat_fiap.siaes.interfaces.inventory;

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
import soat_fiap.siaes.domain.inventory.model.Supply;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;
import soat_fiap.siaes.domain.inventory.service.SupplyService;
import soat_fiap.siaes.interfaces.inventory.dto.CreateSupplyRequest;
import soat_fiap.siaes.interfaces.inventory.dto.UpdateSupplyAvailableRequest;
import soat_fiap.siaes.interfaces.inventory.dto.UpdateSupplyRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
class SupplyControllerTest {

    @MockitoBean
    private SupplyService supplyService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll__should_return_page_of_supplies() throws Exception {
        UUID id = UUID.randomUUID();
        Supply supply = createMockSupply(id, "Graxa", "Shell", new BigDecimal("89.90"), true);

        Page<Supply> page = new PageImpl<>(List.of(supply));
        when(supplyService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/supply"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("Graxa"));
        assertTrue(response.getContentAsString().contains("Shell"));
        assertTrue(response.getContentAsString().contains(id.toString()));
    }

    @Test
    void findAll__should_return_empty_page_when_no_supplies() throws Exception {
        Page<Supply> page = new PageImpl<>(List.of());
        when(supplyService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/supply"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findById__should_return_supply_when_found() throws Exception {
        UUID id = UUID.randomUUID();
        Supply supply = createMockSupply(id, "Óleo", "Petrobras", new BigDecimal("59.90"), true);

        when(supplyService.findById(id)).thenReturn(supply);

        var response = mockMvc.perform(get("/supply/" + id))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("Óleo"));
        assertTrue(response.getContentAsString().contains("Petrobras"));
        assertTrue(response.getContentAsString().contains(id.toString()));
    }

    @Test
    void findById__should_return_not_found_when_supply_does_not_exist() throws Exception {
        UUID id = UUID.randomUUID();
        when(supplyService.findById(id)).thenThrow(new EntityNotFoundException("Supply not found with id: " + id));

        var response = mockMvc.perform(get("/supply/" + id))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void save__should_return_created_when_valid_request() throws Exception {
        CreateSupplyRequest request = new CreateSupplyRequest(
                "Graxa Industrial", "Shell", new BigDecimal("89.90"), UnitMeasure.LT, true
        );
        UUID id = UUID.randomUUID();
        Supply savedSupply = createMockSupply(id, "Graxa Industrial", "Shell", new BigDecimal("89.90"), true);

        when(supplyService.save(any(Supply.class))).thenReturn(savedSupply);

        var response = mockMvc.perform(
                post("/supply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("Graxa Industrial"));
        assertTrue(response.getContentAsString().contains("Shell"));
        assertTrue(response.getContentAsString().contains(id.toString()));
    }

    @Test
    void save__should_return_bad_request_when_body_invalid() throws Exception {
        var response = mockMvc.perform(
                post("/supply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
        ).andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void update__should_return_updated_supply_when_successful() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateSupplyRequest request = new UpdateSupplyRequest(
                "Óleo Premium", new BigDecimal("99.90"), UnitMeasure.LT, "Petrobras", true
        );

        Supply updated = createMockSupply(id, "Óleo Premium", "Petrobras", new BigDecimal("99.90"), true);
        when(supplyService.update(eq(id), any(UpdateSupplyRequest.class))).thenReturn(updated);

        var response = mockMvc.perform(
                put("/supply/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("Óleo Premium"));
        assertTrue(response.getContentAsString().contains("Petrobras"));
    }

    @Test
    void update__should_return_not_found_when_supply_not_exists() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateSupplyRequest request = new UpdateSupplyRequest(
                "Óleo Premium", new BigDecimal("99.90"), UnitMeasure.LT, "Petrobras", true
        );

        when(supplyService.update(eq(id), any(UpdateSupplyRequest.class)))
                .thenThrow(new EntityNotFoundException("Supply not found with id: " + id));

        var response = mockMvc.perform(
                put("/supply/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void update__should_return_bad_request_when_body_invalid() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateSupplyRequest request = new UpdateSupplyRequest(
                "", BigDecimal.ZERO, null, "", null
        );

        var response = mockMvc.perform(
                put("/supply/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void delete__should_return_no_content_when_supply_deleted() throws Exception {
        UUID id = UUID.randomUUID();

        var response = mockMvc.perform(delete("/supply/" + id))
                .andReturn().getResponse();

        assertEquals(204, response.getStatus());
    }

    @Test
    void delete__should_return_not_found_when_supply_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Insumo não encontrado com ID: " + id))
                .when(supplyService)
                .deleteById(id);

        var response = mockMvc.perform(delete("/supply/" + id))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void updateAvailability__should_return_updated_supply_when_successful() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateSupplyAvailableRequest request = new UpdateSupplyAvailableRequest(false);

        Supply updated = createMockSupply(id, "Graxa", "Shell", new BigDecimal("89.90"), false);
        when(supplyService.updateAvailability(id, false)).thenReturn(updated);

        var response = mockMvc.perform(
                patch("/supply/" + id + "/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("false"));
        assertTrue(response.getContentAsString().contains("Shell"));
        assertTrue(response.getContentAsString().contains("Graxa"));
    }

    @Test
    void updateAvailability__should_return_not_found_when_supply_not_exists() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateSupplyAvailableRequest request = new UpdateSupplyAvailableRequest(false);

        when(supplyService.updateAvailability(id, false))
                .thenThrow(new EntityNotFoundException("Insumo não encontrado com ID: " + id));

        var response = mockMvc.perform(
                patch("/supply/" + id + "/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    private Supply createMockSupply(UUID id, String name, String supplier, BigDecimal unitPrice, boolean available) {
        Supply supply = mock(Supply.class);
        when(supply.getId()).thenReturn(id);
        when(supply.getName()).thenReturn(name);
        when(supply.getSupplier()).thenReturn(supplier);
        when(supply.getUnitPrice()).thenReturn(unitPrice);
        when(supply.getUnitMeasure()).thenReturn(UnitMeasure.LT);
        when(supply.getAvailable()).thenReturn(available);
        return supply;
    }
}