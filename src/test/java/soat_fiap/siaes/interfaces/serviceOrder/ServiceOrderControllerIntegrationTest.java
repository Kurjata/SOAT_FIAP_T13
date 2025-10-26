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
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;
import soat_fiap.siaes.domain.inventory.repository.PartRepository;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.repository.ServiceLaborRepository;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatus;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.repository.ServiceOrderRepository;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.CreateOrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.CreateOrderItemRequest;
import soat_fiap.siaes.shared.utils.JsonPageUtils;

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
class ServiceOrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ServiceLaborRepository serviceLaborRepository;

    @Autowired
    private PartRepository partRepository;

    private User createUser(String name, String cpf) {
        return userRepository.save(new User(name, "user", "123456", RoleEnum.ADMIN, cpf, name.toLowerCase() + "@email.com"));
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
    void findAll__should_return_empty_page_when_no_orders() throws Exception {
        var response = mockMvc.perform(get("/service-orders"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findAll__should_return_page_of_orders_when_exist() throws Exception {
        User user1 = createUser("Alice", "930.488.160-97");
        Vehicle vehicle1 = createVehicle("AAA-1111");

        ServiceOrder order1 = createServiceOrder(user1, vehicle1, ServiceOrderStatus.RECEBIDA);

        var response = mockMvc.perform(get("/service-orders"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        List<ServiceOrderResponse> orders = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), ServiceOrderResponse.class);

        assertThat(orders).hasSize(1)
                .extracting(ServiceOrderResponse::id)
                .containsExactlyInAnyOrder(order1.getIdAsString());
    }

    @Test
    void findById__should_return_order_when_found() throws Exception {
        User user = createUser("Charlie", "971.096.620-04");
        Vehicle vehicle = createVehicle("CCC-3333");
        ServiceOrder order = createServiceOrder(user, vehicle, ServiceOrderStatus.RECEBIDA);

        var response = mockMvc.perform(get("/service-orders/{id}", order.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        ServiceOrderResponse orderResponse = objectMapper.readValue(response.getContentAsString(), ServiceOrderResponse.class);
        assertEquals(order.getIdAsString(), orderResponse.id());
        assertEquals(order.getOrderStatus().getDescricao(), orderResponse.orderStatus());
        assertEquals(vehicle.getPlate(), orderResponse.vehiclePlate());
    }

    @Test
    void findById__should_return_not_found_when_order_does_not_exist() throws Exception {
        var response = mockMvc.perform(get("/service-orders/{id}", UUID.randomUUID()))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void create__should_persist_order_and_return_response() throws Exception {
        User user = createUser("David", "971.096.620-04");
        Vehicle vehicle = createVehicle("CCC3333");
        ServiceLabor serviceLabor = createServiceLabor();
        Part part = createPart();
        CreateOrderActivityRequest activityRequest = new CreateOrderActivityRequest(serviceLabor.getId(), List.of(new CreateOrderItemRequest(part.getId(), 2)));
        ServiceOrderRequest serviceOrderRequest = new ServiceOrderRequest("CCC3333", "971.096.620-04", List.of(activityRequest));

        var response = mockMvc.perform(post("/service-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceOrderRequest)))
                .andReturn()
                .getResponse();

        assertEquals(201, response.getStatus());
        ServiceOrderResponse orderResponse = objectMapper.readValue(response.getContentAsString(), ServiceOrderResponse.class);
        assertThat(serviceOrderRepository.findById(UUID.fromString(orderResponse.id()))).isPresent();

        ServiceOrder persistedOrder = serviceOrderRepository.findById(UUID.fromString(orderResponse.id())).orElseThrow();
        assertThat(persistedOrder.getOrderActivities()).hasSize(1);
        OrderActivity activity = persistedOrder.getOrderActivities().get(0);
        assertThat(activity.getOrderItems()).hasSize(1);
        OrderItem item = activity.getOrderItems().get(0);
        assertThat(item.getPartStock().getId()).isEqualTo(part.getId());
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(activity.getServiceLabor().getId()).isEqualTo(serviceLabor.getId());
    }

    @Test
    void delete__should_remove_order_when_exists() throws Exception {
        User user = createUser("Eve", "971.096.620-04");
        Vehicle vehicle = createVehicle("EEE-5555");
        ServiceOrder order = createServiceOrder(user, vehicle, ServiceOrderStatus.RECEBIDA);

        var response = mockMvc.perform(delete("/service-orders/{id}", order.getId()))
                .andReturn()
                .getResponse();

        assertEquals(204, response.getStatus());
        assertThat(serviceOrderRepository.findById(order.getId())).isEmpty();
    }

    @Test
    void delete__should_return_not_found_when_order_does_not_exist() throws Exception {
        var response = mockMvc.perform(delete("/service-orders/{id}", UUID.randomUUID()))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void updateStatus__should_update_status_when_valid() throws Exception {
        User user = createUser("Frank", "971.096.620-04");
        Vehicle vehicle = createVehicle("FFF-6666");
        ServiceOrder order = createServiceOrder(user, vehicle, ServiceOrderStatus.AGUARDANDO_APROVACAO);

        var response = mockMvc.perform(patch("/service-orders/client/status/{id}", order.getId())
                        .param("status", "APROVADO_CLIENTE"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        ServiceOrder updated = serviceOrderRepository.findById(order.getId()).get();
        assertEquals(ServiceOrderStatus.APROVADO_CLIENTE, updated.getOrderStatus());
    }

    @Test
    void findByUserDocument__should_return_orders_for_user() throws Exception {
        User user = createUser("Grace", "971.096.620-04");
        Vehicle vehicle = createVehicle("GGG-7777");
        ServiceOrder order = createServiceOrder(user, vehicle, ServiceOrderStatus.RECEBIDA);

        var response = mockMvc.perform(get("/service-orders/user/document/{cpfCnpj}", "97109662004"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        List<ServiceOrderResponse> orders = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), ServiceOrderResponse.class);
        assertThat(orders).extracting(ServiceOrderResponse::id).contains(order.getIdAsString());
    }

    @Test
    void findByUserId__should_return_orders_for_user() throws Exception {
        User user = createUser("Hank", "971.096.620-04");
        Vehicle vehicle = createVehicle("HHH-8888");
        ServiceOrder order = createServiceOrder(user, vehicle, ServiceOrderStatus.RECEBIDA);

        var response = mockMvc.perform(get("/service-orders/user/{userId}", user.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        List<ServiceOrderResponse> orders = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), ServiceOrderResponse.class);
        assertThat(orders).extracting(ServiceOrderResponse::id).contains(order.getIdAsString());
    }

    @Test
    void findByVehicleId__should_return_orders_for_vehicle() throws Exception {
        User user = createUser("Ivy", "971.096.620-04");
        Vehicle vehicle = createVehicle("III-9999");
        ServiceOrder order = createServiceOrder(user, vehicle, ServiceOrderStatus.RECEBIDA);

        var response = mockMvc.perform(get("/service-orders/vehicle/{vehicleId}", vehicle.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        List<ServiceOrderResponse> orders = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), ServiceOrderResponse.class);
        assertThat(orders).extracting(ServiceOrderResponse::id).contains(order.getIdAsString());
    }

    @Test
    void findByVehiclePlate__should_return_orders_for_vehicle() throws Exception {
        User user = createUser("Jack", "971.096.620-04");
        Vehicle vehicle = createVehicle("JJJ-0000");
        ServiceOrder order = createServiceOrder(user, vehicle, ServiceOrderStatus.RECEBIDA);

        var response = mockMvc.perform(get("/service-orders/vehicle/plate/{plate}", vehicle.getPlate()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        List<ServiceOrderResponse> orders = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), ServiceOrderResponse.class);
        assertThat(orders).extracting(ServiceOrderResponse::id).contains(order.getIdAsString());
    }

    @Test
    void findAllMe__should_return_orders_for_authenticated_user() throws Exception {
        User user = createUser("Kate", "971.096.620-04");
        Vehicle vehicle = createVehicle("KKK-1111");
        ServiceOrder order = createServiceOrder(user, vehicle, ServiceOrderStatus.RECEBIDA);

        var response = mockMvc.perform(get("/service-orders/client/me"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        List<ServiceOrderResponse> orders = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), ServiceOrderResponse.class);
        assertThat(orders).extracting(ServiceOrderResponse::id).contains(order.getIdAsString());
    }
}