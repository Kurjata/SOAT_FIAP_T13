package soat_fiap.siaes.interfaces.serviceOrder;

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
import soat_fiap.siaes.domain.inventory.model.Item;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;
import soat_fiap.siaes.domain.inventory.repository.PartRepository;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.repository.ServiceLaborRepository;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatus;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderActivityRepository;
import soat_fiap.siaes.domain.serviceOrder.repository.ServiceOrderRepository;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.AddOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.CreateOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.OrderItemResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
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
class OrderItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private OrderActivityRepository orderActivityRepository;

    @Autowired
    private ServiceLaborRepository serviceLaborRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PartRepository partRepository;

    private User createUser(String name) {
        return userRepository.save(new User(name, "user", "123456", RoleEnum.ADMIN, "971.096.620-04", name.toLowerCase() + "@email.com"));
    }

    private Vehicle createVehicle(String plate) {
        return vehicleRepository.save(new Vehicle(plate, "Brand", "Model", 2022));
    }

    private ServiceOrder createServiceOrder(User user, Vehicle vehicle, ServiceOrderStatus status) {
        ServiceLabor serviceLabor = createServiceLabor();
        Part part = createPart();

        ServiceOrder serviceOrder = new ServiceOrder(user, vehicle, status);

        OrderActivity orderActivity = new OrderActivity(serviceOrder, serviceLabor);
        OrderItem orderItem = new OrderItem(orderActivity, part, 2, part.getUnitPrice());

        serviceOrder.setOrderActivities(new ArrayList<>(List.of(orderActivity)));
        orderActivity.setOrderItems(new ArrayList<>(List.of(orderItem)));

        return serviceOrderRepository.save(serviceOrder);
    }

    private ServiceLabor createServiceLabor() {
        return serviceLaborRepository.save(new ServiceLabor("Troca de Óleo", BigDecimal.TEN));
    }

    private Part createPart() {
        return partRepository.save(new Part("Parafuso", BigDecimal.TEN, UnitMeasure.UNIT, 100, 0, "9482701563914", "Fornecedor X", 10));
    }

    @Test
    void create__should_persist_order_item_and_return_response() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Vinicius"), createVehicle("ABC-1234"), ServiceOrderStatus.RECEBIDA);
        OrderActivity activity = order.getOrderActivities().get(0);
        Item item = activity.getOrderItems().get(0).getPartStock();

        AddOrderItemRequest request = new AddOrderItemRequest(activity.getId(), item.getId(), 3);

        var response = mockMvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(201, response.getStatus());
        OrderItemResponse responseBody = objectMapper.readValue(response.getContentAsString(), OrderItemResponse.class);
        assertThat(responseBody.id()).isNotNull();

        OrderActivity persistedActivity = orderActivityRepository.findById(activity.getId()).orElseThrow();
        assertThat(persistedActivity.getOrderItems()).hasSize(1);
        OrderItem persistedItem = persistedActivity.getOrderItems().get(0);
        assertThat(persistedItem.getPartStock().getId()).isEqualTo(item.getId());
        assertThat(persistedItem.getQuantity()).isEqualTo(2);
    }

    @Test
    void create__should_return_not_found_when_order_activity_does_not_exist() throws Exception {
        AddOrderItemRequest request = new AddOrderItemRequest(UUID.randomUUID(), UUID.randomUUID(), 3);

        var response = mockMvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Insumo não encontrado"));
    }

    @Test
    void create__should_return_not_found_when_item_does_not_exist() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Charlie"), createVehicle("DEF-2222"), ServiceOrderStatus.RECEBIDA);
        OrderActivity activity = order.getOrderActivities().get(0);

        AddOrderItemRequest request = new AddOrderItemRequest(activity.getId(), UUID.randomUUID(), 3);

        var response = mockMvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Insumo não encontrado"));
    }

    @Test
    void getById__should_return_order_item() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Alice"), createVehicle("XYZ-9999"), ServiceOrderStatus.RECEBIDA);
        OrderActivity activity = order.getOrderActivities().get(0);
        OrderItem item = activity.getOrderItems().get(0);

        var response = mockMvc.perform(get("/order-items/" + item.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        OrderItemResponse responseBody = objectMapper.readValue(response.getContentAsString(), OrderItemResponse.class);

        assertThat(responseBody.id()).isEqualTo(item.getPartStock().getIdAsString());
        assertThat(responseBody.quantity()).isEqualTo(item.getQuantity());
    }

    @Test
    void getByOrderActivity__should_return_list_of_items() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Bob"), createVehicle("LMN-5555"), ServiceOrderStatus.RECEBIDA);
        OrderActivity activity = order.getOrderActivities().get(0);

        var response = mockMvc.perform(get("/order-items/item/" + activity.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        OrderItemResponse[] itemsArray = objectMapper.readValue(response.getContentAsString(), OrderItemResponse[].class);
        List<OrderItemResponse> items = List.of(itemsArray);

        assertThat(items).hasSize(activity.getOrderItems().size());
        assertThat(items.get(0).id()).isEqualTo(activity.getOrderItems().get(0).getPartStock().getIdAsString());
    }

    @Test
    void update__should_update_order_item_successfully() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Dave"), createVehicle("GHI-3333"), ServiceOrderStatus.RECEBIDA);
        OrderItem item = order.getOrderActivities().get(0).getOrderItems().get(0);

        CreateOrderItemRequest updateRequest = new CreateOrderItemRequest(item.getPartStock().getId(), 5);

        var response = mockMvc.perform(put("/order-items/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        OrderItemResponse updated = objectMapper.readValue(response.getContentAsString(), OrderItemResponse.class);
        assertEquals(5, updated.quantity());
    }

    @Test
    void update__should_return_not_found_when_order_item_does_not_exist() throws Exception {
        CreateOrderItemRequest updateRequest = new CreateOrderItemRequest(UUID.randomUUID(), 5);

        var response = mockMvc.perform(put("/order-items/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Item da ordem não encontrado"));
    }

    @Test
    void delete__should_remove_order_item_successfully() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Eve"), createVehicle("JKL-4444"), ServiceOrderStatus.RECEBIDA);
        OrderItem item = order.getOrderActivities().get(0).getOrderItems().get(0);

        var response = mockMvc.perform(delete("/order-items/" + item.getId()))
                .andReturn()
                .getResponse();

        assertEquals(204, response.getStatus());
    }

    @Test
    void delete__should_return_not_found_when_order_item_does_not_exist() throws Exception {
        var response = mockMvc.perform(delete("/order-items/" + UUID.randomUUID()))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Insumo não encontrado"));
    }
}