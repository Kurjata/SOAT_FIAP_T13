package soat_fiap.siaes.interfaces.serviceOrder;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import soat_fiap.siaes.domain.serviceOrder.service.OrderActivityService;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.AddOrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.OrderActivityResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.AddOrderItemRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
class OrderActivityControllerTest {

    @MockitoBean
    private OrderActivityService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getByOrder__should_return_list_of_activities() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderActivityResponse responseObj = mock(OrderActivityResponse.class);
        when(responseObj.serviceLabor()).thenReturn(new ServiceLaborResponse("123", "Troca de Óleo", BigDecimal.TEN));
        when(service.findByServiceOrder(orderId)).thenReturn(List.of(responseObj));

        var response = mockMvc.perform(get("/order-activities/order/" + orderId))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains("123");
        assertThat(response.getContentAsString()).contains("Troca de Óleo");
    }

    @Test
    void getByOrder__should_return_404_when_order_not_found() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(service.findByServiceOrder(orderId))
                .thenThrow(new EntityNotFoundException("Ordem não encontrada"));

        var result = mockMvc.perform(get("/order-activities/order/" + orderId))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(404);
    }

    @Test
    void getById__should_return_activity() throws Exception {
        UUID id = UUID.randomUUID();
        OrderActivityResponse responseObj = mock(OrderActivityResponse.class);
        when(responseObj.serviceLabor()).thenReturn(new ServiceLaborResponse("123", "Troca de Óleo", BigDecimal.TEN));
        when(service.findById(id)).thenReturn(responseObj);

        var response = mockMvc.perform(get("/order-activities/" + id))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains("123");
        assertThat(response.getContentAsString()).contains("Troca de Óleo");
    }

    @Test
    void getById__should_return_404_when_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenThrow(new EntityNotFoundException("Atividade de ordem não encontrado"));

        var result = mockMvc.perform(get("/order-activities/" + id))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(404);
    }

    @Test
    void create__should_create_activity_with_items() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();
        AddOrderActivityRequest request = new AddOrderActivityRequest(
                orderId,
                laborId,
                List.of(new AddOrderItemRequest(UUID.randomUUID(), UUID.randomUUID(), 2))
        );
        OrderActivityResponse response = mock(OrderActivityResponse.class);
        when(service.create(any(AddOrderActivityRequest.class))).thenReturn(response);

        var result = mockMvc.perform(post("/order-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(201);
    }

    @Test
    void create__should_create_activity_with_empty_items() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();
        AddOrderActivityRequest request = new AddOrderActivityRequest(orderId, laborId, List.of());
        OrderActivityResponse response = mock(OrderActivityResponse.class);
        when(service.create(any(AddOrderActivityRequest.class))).thenReturn(response);

        var result = mockMvc.perform(post("/order-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(201);
    }

    @Test
    void update__should_update_activity_with_items() throws Exception {
        UUID id = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();
        AddOrderActivityRequest request = new AddOrderActivityRequest(
                UUID.randomUUID(),
                laborId,
                List.of(new AddOrderItemRequest(UUID.randomUUID(), UUID.randomUUID(), 3))
        );
        OrderActivityResponse response = mock(OrderActivityResponse.class);
        when(service.update(eq(id), any(AddOrderActivityRequest.class))).thenReturn(response);

        var result = mockMvc.perform(put("/order-activities/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(200);
    }

    @Test
    void update__should_return_404_when_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();
        AddOrderActivityRequest request = new AddOrderActivityRequest(
                UUID.randomUUID(),
                laborId,
                List.of()
        );
        when(service.update(eq(id), any(AddOrderActivityRequest.class)))
                .thenThrow(new EntityNotFoundException("Item da ordem não encontrado"));

        var result = mockMvc.perform(put("/order-activities/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(404);
    }

    @Test
    void delete__should_delete_activity() throws Exception {
        UUID id = UUID.randomUUID();

        var result = mockMvc.perform(delete("/order-activities/" + id))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(204);
        verify(service).delete(id);
    }

    @Test
    void delete__should_return_404_when_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Item da ordem não encontrado")).when(service).delete(id);

        var result = mockMvc.perform(delete("/order-activities/" + id))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(404);
    }
}