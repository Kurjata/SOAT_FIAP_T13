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
import soat_fiap.siaes.domain.serviceOrder.service.OrderItemService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.AddOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.CreateOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.OrderItemResponse;

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
class OrderItemControllerTest {

    @MockitoBean
    private OrderItemService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getByOrderActivity__should_return_list_of_items() throws Exception {
        UUID orderActivityId = UUID.randomUUID();
        OrderItemResponse responseObj = mock(OrderItemResponse.class);
        when(service.findAllByOrderActivity(orderActivityId)).thenReturn(List.of(responseObj));

        var response = mockMvc.perform(get("/order-items/item/" + orderActivityId))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void getByOrderActivity__should_return_404_when_not_found() throws Exception {
        UUID orderActivityId = UUID.randomUUID();
        when(service.findAllByOrderActivity(orderActivityId))
                .thenThrow(new EntityNotFoundException("Item da ordem não encontrado"));

        var result = mockMvc.perform(get("/order-items/item/" + orderActivityId))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(404);
    }

    @Test
    void getById__should_return_item() throws Exception {
        UUID id = UUID.randomUUID();
        OrderItemResponse responseObj = mock(OrderItemResponse.class);
        when(service.findById(id)).thenReturn(responseObj);

        var response = mockMvc.perform(get("/order-items/" + id))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void getById__should_return_404_when_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenThrow(new EntityNotFoundException("Insumo não encontrado"));

        var result = mockMvc.perform(get("/order-items/" + id))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(404);
    }

    @Test
    void create__should_create_item() throws Exception {
        AddOrderItemRequest request = new AddOrderItemRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                2
        );
        OrderItemResponse response = mock(OrderItemResponse.class);
        when(service.create(any(AddOrderItemRequest.class))).thenReturn(response);

        var result = mockMvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(201);
    }

    @Test
    void update__should_update_item() throws Exception {
        UUID id = UUID.randomUUID();
        CreateOrderItemRequest request = new CreateOrderItemRequest(UUID.randomUUID(), 5);
        OrderItemResponse response = mock(OrderItemResponse.class);
        when(service.update(eq(id), any(CreateOrderItemRequest.class))).thenReturn(response);

        var result = mockMvc.perform(put("/order-items/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(200);
    }

    @Test
    void update__should_return_404_when_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        CreateOrderItemRequest request = new CreateOrderItemRequest(UUID.randomUUID(), 5);
        when(service.update(eq(id), any(CreateOrderItemRequest.class)))
                .thenThrow(new EntityNotFoundException("Insumo não encontrado"));

        var result = mockMvc.perform(put("/order-items/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(404);
    }

    @Test
    void delete__should_delete_item() throws Exception {
        UUID id = UUID.randomUUID();

        var result = mockMvc.perform(delete("/order-items/" + id))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(204);
        verify(service).delete(id);
    }

    @Test
    void delete__should_return_404_when_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Insumo não encontrado")).when(service).delete(id);

        var result = mockMvc.perform(delete("/order-items/" + id))
                .andReturn().getResponse();

        assertThat(result.getStatus()).isEqualTo(404);
    }
}