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
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;
import soat_fiap.siaes.domain.inventory.service.PartService;
import soat_fiap.siaes.interfaces.inventory.dto.AddStockRequest;
import soat_fiap.siaes.interfaces.inventory.dto.CreatePartRequest;
import soat_fiap.siaes.interfaces.inventory.dto.PartResponse;
import soat_fiap.siaes.interfaces.inventory.dto.UpdatePartRequest;

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
class PartControllerTest {
    @MockitoBean
    private PartService partService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll__should_return_page_of_parts() throws Exception {
        Part part = createMockPart();

        var page = new PageImpl<>(List.of(part));
        when(partService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/parts"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains(part.getName()));
        assertTrue(response.getContentAsString().contains(part.getManufacturer()));
    }

    @Test
    void findAll__should_return_empty_page_when_no_parts() throws Exception {
        Page<Part> page = new PageImpl<>(List.of());
        when(partService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/parts"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findById__should_return_part_when_found() throws Exception {
        UUID id = UUID.randomUUID();
        Part part = createMockPart();
        when(part.getId()).thenReturn(id);

        when(partService.findById(id)).thenReturn(part);

        var response = mockMvc.perform(get("/parts/" + id))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains(part.getName()));
        assertTrue(response.getContentAsString().contains(part.getManufacturer()));
    }

    @Test
    void findById__should_return_not_found_when_not_exists() throws Exception {
        UUID id = UUID.randomUUID();
        when(partService.findById(id)).thenThrow(new EntityNotFoundException("Peça não encontrada"));

        var response = mockMvc.perform(get("/parts/" + id))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void save__should_return_code_bad_request_when_body_is_empty() throws Exception {
        var response = mockMvc.perform(
                post("/parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
        ).andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void save__should_return_code_created_when_valid_body() throws Exception {
        CreatePartRequest request = new CreatePartRequest(10, "12323456789012", "Top", 5, "Filtro de óleo", BigDecimal.valueOf(20), UnitMeasure.UNIT, 0);;

        Part savedPart = createMockPart();
        when(partService.save(any(Part.class))).thenReturn(savedPart);

        var response = mockMvc.perform(
                post("/parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        PartResponse actual = objectMapper.readValue(response.getContentAsString(), PartResponse.class);
        assertEquals(savedPart.getName(), actual.name());
        assertEquals(savedPart.getUnitPrice(), actual.unitPrice());
        assertEquals(savedPart.getQuantity(), actual.quantity());
        assertEquals(savedPart.getEan(), actual.ean());
        assertEquals(savedPart.getManufacturer(), actual.manufacturer());
        assertEquals(savedPart.getMinimumStockQuantity(), actual.minimumStockQuantity());
        assertEquals(savedPart.getReservedQuantity(), actual.reservedQuantity());
    }


    @Test
    void update__should_return_updated_part_when_successful() throws Exception {
        UUID id = UUID.randomUUID();
        UpdatePartRequest request = new UpdatePartRequest("Filtro novo", BigDecimal.valueOf(30.00), UnitMeasure.UNIT,
                15, 2, 3, "1234567890123", "Top");

        Part updated = createMockPart();
        when(partService.update(eq(id), any(UpdatePartRequest.class))).thenReturn(updated);

        var response = mockMvc.perform(
                put("/parts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertEquals(new PartResponse(updated), objectMapper.readValue(response.getContentAsString(), PartResponse.class));
    }

    @Test
    void update__should_return_not_found_when_part_not_exists() throws Exception {
        UUID id = UUID.randomUUID();
        UpdatePartRequest request = new UpdatePartRequest("Filtro", BigDecimal.TEN,
                UnitMeasure.UNIT, 5, 0, 2, "123456", "Top");

        when(partService.update(eq(id), any(UpdatePartRequest.class)))
                .thenThrow(new EntityNotFoundException("Peça não encontrada"));

        var response = mockMvc.perform(
                put("/parts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void delete__should_return_no_content_when_deleted() throws Exception {
        UUID id = UUID.randomUUID();

        var response = mockMvc.perform(delete("/parts/" + id))
                .andReturn().getResponse();

        assertEquals(204, response.getStatus());
    }

    @Test
    void delete__should_return_not_found_when_not_exists() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Peça não encontrada"))
                .when(partService)
                .deleteById(id);

        var response = mockMvc.perform(delete("/parts/" + id))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void addStock__should_return_updated_part_when_successful() throws Exception {
        UUID id = UUID.randomUUID();
        AddStockRequest request = new AddStockRequest(5);

        Part updated = createMockPart();
        when(partService.addStock(eq(id), eq(5))).thenReturn(updated);

        var response = mockMvc.perform(
                patch("/parts/" + id + "/stock/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertEquals(new PartResponse(updated), objectMapper.readValue(response.getContentAsString(), PartResponse.class));
    }

    @Test
    void adjustStock__should_return_updated_part_when_successful() throws Exception {
        UUID id = UUID.randomUUID();
        Part updated = createMockPart();

        when(partService.updateStockQuantity(eq(id), eq(10))).thenReturn(updated);

        var response = mockMvc.perform(
                patch("/parts/" + id + "/stock/adjust")
                        .param("quantity", "10")
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertEquals(new PartResponse(updated), objectMapper.readValue(response.getContentAsString(), PartResponse.class));

    }

    @Test
    void findAllBelowMinimumStock__should_return_list_of_parts() throws Exception {
        Part part = createMockPart();
        when(partService.findPartsBelowMinimumStock()).thenReturn(List.of(part));

        var response = mockMvc.perform(get("/parts/stock/below-minimum"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains(part.getName()));
    }

    private Part createMockPart() {
        Part part = mock(Part.class);
        when(part.getId()).thenReturn(UUID.randomUUID());
        when(part.getName()).thenReturn("Filtro de óleo");
        when(part.getManufacturer()).thenReturn("Teste");
        when(part.getQuantity()).thenReturn(10);
        when(part.getUnitPrice()).thenReturn(BigDecimal.valueOf(25.5));
        return part;
    }
}