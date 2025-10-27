package soat_fiap.siaes.interfaces.serviceOrder;

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
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatus;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.service.ServiceOrderService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.CreateOrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.CreateOrderItemRequest;

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
class ServiceOrderControllerTest {

    @MockitoBean
    private ServiceOrderService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll__should_return_page_of_service_orders() throws Exception {
        UUID id = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(id);

        Page<ServiceOrder> page = new PageImpl<>(List.of(order));
        var pageServiceOrder = page.map(ServiceOrderResponse::new);
        when(service.findAll(any())).thenReturn(pageServiceOrder);

        var response = mockMvc.perform(get("/service-orders"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains(id.toString());
    }

    @Test
    void findById__should_return_service_order_when_found() throws Exception {
        UUID id = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(id);

        ServiceOrderResponse serviceOrderResponse = new ServiceOrderResponse(order);
        when(service.findById(id)).thenReturn(serviceOrderResponse);

        var response = mockMvc.perform(get("/service-orders/" + id))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains(id.toString());
    }

    @Test
    void findById__should_return_not_found_when_service_order_not_exists() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenThrow(new EntityNotFoundException("OS não encontrada com id: " + id));

        var response = mockMvc.perform(get("/service-orders/" + id))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    void create__should_return_created_service_order() throws Exception {
        CreateOrderActivityRequest activityRequest = new CreateOrderActivityRequest(UUID.randomUUID(), List.of(new CreateOrderItemRequest(UUID.randomUUID(), 2)));
        ServiceOrderRequest request = new ServiceOrderRequest("12345678900", "ABC-1234", List.of(activityRequest));
        UUID id = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(id);

        ServiceOrderResponse serviceOrderResponse = new ServiceOrderResponse(order);
        when(service.createServiceOrder(any(ServiceOrderRequest.class))).thenReturn(serviceOrderResponse);

        var response = mockMvc.perform(
                        post("/service-orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getContentAsString()).contains(id.toString());
    }

    @Test
    void updateStatus__should_update_status_successfully() throws Exception {
        UUID id = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getId()).thenReturn(id);

        ServiceOrderResponse serviceOrderResponse = new ServiceOrderResponse(order);

        when(service.updateStatus(eq(id), eq(ServiceOrderStatus.FINALIZADA)))
                .thenReturn(serviceOrderResponse);

        var response = mockMvc.perform(
                        patch("/service-orders/client/status/" + id)
                                .param("status", "FINALIZADA")
                )
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains(id.toString());
    }

    @Test
    void delete__should_return_no_content_when_deleted() throws Exception {
        UUID id = UUID.randomUUID();

        var response = mockMvc.perform(delete("/service-orders/" + id))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(204);
        verify(service).delete(id);
    }

    @Test
    void getByUserDocument__should_return_page() throws Exception {
        String cpf = "12345678900";
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of());
        when(service.findByUserDocument(eq(cpf), any())).thenReturn(page);

        var response = mockMvc.perform(get("/service-orders/user/document/" + cpf))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void findByUser__should_return_page() throws Exception {
        UUID userId = UUID.randomUUID();
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of());
        when(service.findByUserId(eq(userId), any())).thenReturn(page);

        var response = mockMvc.perform(get("/service-orders/user/" + userId))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void getByVehicleId__should_return_page() throws Exception {
        UUID vehicleId = UUID.randomUUID();
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of());
        when(service.findByVehicleId(eq(vehicleId), any())).thenReturn(page);

        var response = mockMvc.perform(get("/service-orders/vehicle/" + vehicleId))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void getByVehiclePlate__should_return_page() throws Exception {
        String plate = "ABC1234";
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of());
        when(service.findByVehiclePlate(eq(plate), any())).thenReturn(page);

        var response = mockMvc.perform(get("/service-orders/vehicle/plate/" + plate))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void findAllMe__should_return_page() throws Exception {
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of());
        when(service.findAllMe(any())).thenReturn(page);

        var response = mockMvc.perform(get("/service-orders/client/me"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
    }
}