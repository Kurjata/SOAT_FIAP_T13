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
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.repository.ServiceLaborRepository;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatus;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderActivityRepository;
import soat_fiap.siaes.domain.serviceOrder.repository.ServiceOrderRepository;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.AddOrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.OrderActivityResponse;

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
class OrderActivityControllerIntegrationTest {

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

    private User createUser(String name) {
        return userRepository.save(new User(name, "user", "123456", RoleEnum.ADMIN, "971.096.620-04", name.toLowerCase() + "@email.com"));
    }

    private Vehicle createVehicle(String plate) {
        return vehicleRepository.save(new Vehicle(plate, "Brand", "Model", 2022));
    }

    private ServiceLabor createServiceLabor() {
        return serviceLaborRepository.save(new ServiceLabor("Troca de Óleo", BigDecimal.TEN));
    }

    private ServiceOrder createServiceOrder(User user, Vehicle vehicle) {
        return serviceOrderRepository.save(new ServiceOrder(user, vehicle, ServiceOrderStatus.RECEBIDA));
    }

    private OrderActivity createOrderActivity(ServiceOrder order, ServiceLabor labor) {
        OrderActivity activity = new OrderActivity(order, labor);
        order.setOrderActivities(new ArrayList<>(List.of(activity)));
        return orderActivityRepository.save(activity);
    }

    @Test
    void create__should_persist_order_activity_and_return_response() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Vinicius"), createVehicle("ABC-1234"));
        ServiceLabor labor = createServiceLabor();

        AddOrderActivityRequest request = new AddOrderActivityRequest(order.getId(), labor.getId(), List.of());

        var response = mockMvc.perform(post("/order-activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(201, response.getStatus());
        OrderActivityResponse responseBody = objectMapper.readValue(response.getContentAsString(), OrderActivityResponse.class);
        assertThat(responseBody.serviceLabor().id()).isNotNull();
    }

    @Test
    void getById__should_return_order_activity() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Alice"), createVehicle("XYZ-9999"));
        ServiceLabor labor = createServiceLabor();
        OrderActivity activity = createOrderActivity(order, labor);

        var response = mockMvc.perform(get("/order-activities/" + activity.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        OrderActivityResponse responseBody = objectMapper.readValue(response.getContentAsString(), OrderActivityResponse.class);
        assertEquals(activity.getServiceLabor().getId().toString(), responseBody.serviceLabor().id());
        assertEquals(labor.getId().toString(), responseBody.serviceLabor().id());
    }

    @Test
    void getById__should_return_not_found_when_activity_not_exists() throws Exception {
        UUID randomId = UUID.randomUUID();

        var response = mockMvc.perform(get("/order-activities/" + randomId))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getByOrder__should_return_list_of_activities() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Bob"), createVehicle("LMN-5555"));
        ServiceLabor labor = createServiceLabor();
        OrderActivity activity = createOrderActivity(order, labor);

        var response = mockMvc.perform(get("/order-activities/order/" + order.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        OrderActivityResponse[] array = objectMapper.readValue(response.getContentAsString(), OrderActivityResponse[].class);
        List<OrderActivityResponse> list = List.of(array);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).serviceLabor().id()).isEqualTo(activity.getServiceLabor().getId().toString());
    }

    @Test
    void getByOrder__should_return_404_when_order_not_found() throws Exception {
        UUID randomOrderId = UUID.randomUUID();

        var response = mockMvc.perform(get("/order-activities/order/" + randomOrderId))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void update__should_update_order_activity_successfully() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Charlie"), createVehicle("DEF-2222"));
        ServiceLabor labor = createServiceLabor();
        ServiceLabor newLabor = createServiceLabor(); // new labor
        OrderActivity activity = createOrderActivity(order, labor);

        AddOrderActivityRequest updateRequest = new AddOrderActivityRequest(order.getId(), newLabor.getId(), List.of());

        var response = mockMvc.perform(put("/order-activities/" + activity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        OrderActivityResponse responseBody = objectMapper.readValue(response.getContentAsString(), OrderActivityResponse.class);
        assertEquals(newLabor.getId().toString(), responseBody.serviceLabor().id());

        OrderActivity persisted = orderActivityRepository.findById(activity.getId()).orElseThrow();
        assertEquals(newLabor.getId(), persisted.getServiceLabor().getId());
    }

    @Test
    void update__should_return_not_found_when_activity_not_exists() throws Exception {
        UUID randomId = UUID.randomUUID();
        ServiceOrder order = createServiceOrder(createUser("Delta"), createVehicle("NOP-0000"));
        ServiceLabor labor = createServiceLabor();

        AddOrderActivityRequest request = new AddOrderActivityRequest(order.getId(), labor.getId(), List.of());

        var response = mockMvc.perform(put("/order-activities/" + randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void delete__should_remove_order_activity_successfully() throws Exception {
        ServiceOrder order = createServiceOrder(createUser("Eve"), createVehicle("JKL-4444"));
        ServiceLabor labor = createServiceLabor();
        OrderActivity activity = createOrderActivity(order, labor);

        var response = mockMvc.perform(delete("/order-activities/" + activity.getId()))
                .andReturn()
                .getResponse();

        assertEquals(204, response.getStatus());

        boolean exists = orderActivityRepository.findById(activity.getId()).isPresent();
        assertFalse(exists);
    }

    @Test
    void delete__should_return_not_found_when_activity_not_exists() throws Exception {
        UUID randomId = UUID.randomUUID();

        var response = mockMvc.perform(delete("/order-activities/" + randomId))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }
}