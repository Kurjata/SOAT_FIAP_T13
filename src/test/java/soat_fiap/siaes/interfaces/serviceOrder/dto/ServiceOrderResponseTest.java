package soat_fiap.siaes.interfaces.serviceOrder.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.user.model.User;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceOrderResponseTest {

    @Test
    @DisplayName("Deve criar corretamente um ServiceOrderResponse com todos os dados preenchidos")
    void shouldCreateResponseWithFullData() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        String expectedPlate = "ABC1234";
        String expectedUserName = "João da Silva";
        String expectedLaborName = "Troca de óleo";

        Vehicle vehicle = new Vehicle();
        vehicle.setPlate(expectedPlate);

        User user = new User();
        user.setName(expectedUserName);

        ServiceLabor labor = new ServiceLabor();
        ReflectionTestUtils.setField(labor, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(labor, "description", expectedLaborName);

        OrderActivity activity = new OrderActivity();
        ReflectionTestUtils.setField(activity, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(activity, "serviceLabor", labor);

        LocalDateTime startTime = LocalDateTime.of(2025, 10, 24, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 10, 24, 11, 30); // 90 minutos de duração
        long durationMinutes = ChronoUnit.MINUTES.between(startTime, endTime);

        ServiceOrder order = new ServiceOrder();
        order.setId(orderId);
        order.setVehicle(vehicle);
        order.setUser(user);
        order.setStartTime(startTime);
        order.setEndTime(endTime);
        order.setOrderStatusEnum(ServiceOrderStatusEnum.EM_EXECUCAO);
        order.setOrderActivities(List.of(activity));

        ServiceOrderResponse response = new ServiceOrderResponse(order);

        assertEquals(orderId.toString(), response.id());
        assertEquals(expectedPlate, response.vehiclePlate());
        assertEquals(expectedUserName, response.userName());
        assertEquals(ServiceOrderStatusEnum.EM_EXECUCAO.getDescricao(), response.orderStatus());
        assertEquals(startTime, response.startTime());
        assertEquals(endTime, response.endTime());
        assertEquals(durationMinutes, response.durationMinutes());
        assertNotNull(response.items());
        assertEquals(1, response.items().size());

        assertEquals(expectedLaborName, response.items().get(0).serviceLabor().description());
    }

    @Test
    @DisplayName("Deve criar um ServiceOrderResponse mesmo com campos nulos e lista vazia")
    void shouldHandleNullFieldsGracefully() {

        UUID orderId = UUID.randomUUID();

        ServiceOrder order = new ServiceOrder();
        order.setId(orderId);
        order.setVehicle(null);
        order.setUser(null);
        order.setStartTime(null);
        order.setEndTime(null);
        order.setOrderStatusEnum(null);
        order.setOrderActivities(null);

        ServiceOrderResponse response = new ServiceOrderResponse(order);

        assertEquals(orderId.toString(), response.id());
        assertNull(response.vehiclePlate());
        assertNull(response.userName());
        assertNull(response.startTime());
        assertNull(response.endTime());
        assertNull(response.durationMinutes());
        assertNull(response.orderStatus());
        assertNotNull(response.items());
        assertTrue(response.items().isEmpty());
    }
}
