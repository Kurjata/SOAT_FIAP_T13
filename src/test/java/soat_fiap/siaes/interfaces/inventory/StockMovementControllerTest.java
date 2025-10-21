package soat_fiap.siaes.interfaces.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import soat_fiap.siaes.domain.inventory.model.MovementType;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.StockMovement;
import soat_fiap.siaes.domain.inventory.service.StockMovementService;
import soat_fiap.siaes.interfaces.inventory.dto.StockMovementResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
class StockMovementControllerTest {

    @MockitoBean
    private StockMovementService stockMovementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Part part;

    @BeforeEach
    void setUp() {
        part = mock(Part.class);
        when(part.getUnitPrice()).thenReturn(BigDecimal.TEN);
    }

    private StockMovement createStockMovement(MovementType movementType, int quantity, int balanceBefore, int balanceAfter) {
        return new StockMovement(part, movementType, quantity, balanceBefore, balanceAfter);
    }

    @Test
    void findAll__should_return_page_of_stock_movements() throws Exception {
        StockMovement movement = createStockMovement(MovementType.SAIDA_OS, 2, 20, 18);
        Page<StockMovementResponse> page = new PageImpl<>(
                List.of(StockMovementResponse.response(movement)),
                PageRequest.of(0, 10),
                1
        );

        when(stockMovementService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/stock-movements"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains(movement.getQuantity().toString()));
        assertTrue(response.getContentAsString().contains(movement.getBalanceAfter().toString()));
        assertTrue(response.getContentAsString().contains(movement.getBalanceAfter().toString()));
        assertTrue(response.getContentAsString().contains(movement.getType().name()));
    }

    @Test
    void findAll__should_return_empty_page_when_no_movements() throws Exception {
        Page<StockMovementResponse> emptyPage = new PageImpl<>(List.of());
        when(stockMovementService.findAll(any())).thenReturn(emptyPage);

        var response = mockMvc.perform(get("/stock-movements"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findByPart__should_return_page_of_movements_for_specific_part() throws Exception {
        UUID partId = UUID.randomUUID();
        StockMovement movement = createStockMovement(MovementType.ENTRADA, 3, 22, 25);

        Page<StockMovementResponse> page = new PageImpl<>(
                List.of(StockMovementResponse.response(movement)),
                PageRequest.of(0, 10),
                1
        );

        when(stockMovementService.findByPart(eq(partId), any())).thenReturn(page);

        var response = mockMvc.perform(get("/stock-movements/part/" + partId))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains(movement.getQuantity().toString()));
        assertTrue(response.getContentAsString().contains(movement.getBalanceAfter().toString()));
        assertTrue(response.getContentAsString().contains(movement.getBalanceAfter().toString()));
        assertTrue(response.getContentAsString().contains(movement.getType().name()));
    }

    @Test
    void findByPart__should_return_empty_page_when_no_movements_for_part() throws Exception {
        UUID partId = UUID.randomUUID();
        Page<StockMovementResponse> emptyPage = new PageImpl<>(List.of());
        when(stockMovementService.findByPart(eq(partId), any())).thenReturn(emptyPage);

        var response = mockMvc.perform(get("/stock-movements/part/" + partId))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

}