package soat_fiap.siaes.interfaces.vehicle;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;
import soat_fiap.siaes.domain.vehicle.service.VehicleService;
import soat_fiap.siaes.interfaces.vehicle.dto.CreateVehicleRequest;
import soat_fiap.siaes.interfaces.vehicle.dto.UpdateVehicleRequest;
import soat_fiap.siaes.interfaces.vehicle.dto.VehicleResponse;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@ActiveProfiles("test")
class VehicleControllerTest {

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private VehicleRepository vehicleRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll__should_return_page_of_vehicles() throws Exception {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = createMockVehicle(id, "ABC1234", "Toyota", "Corolla", 2020);

        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));
        when(vehicleService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/vehicles"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("ABC1234"));
        assertTrue(response.getContentAsString().contains(id.toString()));
        assertTrue(response.getContentAsString().contains(vehicle.getPlate()));
        assertTrue(response.getContentAsString().contains(Integer.toString(vehicle.getYear())));
        assertTrue(response.getContentAsString().contains(vehicle.getModel()));
        assertTrue(response.getContentAsString().contains(vehicle.getBrand()));
    }

    @Test
    void findAll__should_return_empty_page_when_no_vehicles() throws Exception {
        Page<Vehicle> page = new PageImpl<>(List.of());
        when(vehicleService.findAll(any())).thenReturn(page);

        var response = mockMvc.perform(get("/vehicles"))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findById__should_return_vehicle_when_found() throws Exception {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = createMockVehicle(id, "ABC1234", "Toyota", "Corolla", 2020);

        when(vehicleService.findById(id)).thenReturn(vehicle);

        var response = mockMvc.perform(get("/vehicles/" + id))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("ABC1234"));
        assertTrue(response.getContentAsString().contains(id.toString()));
        assertTrue(response.getContentAsString().contains(vehicle.getPlate()));
        assertTrue(response.getContentAsString().contains(Integer.toString(vehicle.getYear())));
        assertTrue(response.getContentAsString().contains(vehicle.getModel()));
        assertTrue(response.getContentAsString().contains(vehicle.getBrand()));
    }

    @Test
    void findById__should_return_code_not_found_when_vehicle_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        when(vehicleService.findById(id)).thenThrow(new EntityNotFoundException("Veículo não encontrado com id: " + id));

        var response = mockMvc.perform(get("/vehicles/" + id))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void save__should_return_code_bad_request_when_body_is_empty() throws Exception {
        var response = mockMvc.perform(
                post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andReturn()
                .getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void save__should_return_code_created_when_body_is_valid_and_saved() throws Exception {
        CreateVehicleRequest vehicleRequest = new CreateVehicleRequest("ABC-1234", "Toyota", "Corolla", 2020);

        UUID id = UUID.randomUUID();
        Vehicle vehicleCreated = createMockVehicle(id, "ABC1234", "Toyota", "Corolla", 2020);

        when(vehicleService.save(any(Vehicle.class))).thenReturn(vehicleCreated);

        var response = mockMvc.perform(
                        post("/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(vehicleRequest))
                )
                .andReturn()
                .getResponse();

        assertEquals(201, response.getStatus());
        assertEquals(new VehicleResponse(vehicleCreated), objectMapper.readValue(response.getContentAsString(), VehicleResponse.class));
    }


    @Test
    void deleteById__should_return_code_no_content_when_vehicle_deleted() throws Exception {
        UUID id = UUID.randomUUID();

        var response = mockMvc.perform(delete("/vehicles/" + id))
                .andReturn()
                .getResponse();

        assertEquals(204, response.getStatus());
    }

    @Test
    void deleteById__should_return_code_not_found_when_vehicle_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Veículo não encontrado com id: " + id))
                .when(vehicleService)
                .deleteById(id);

        var response = mockMvc.perform(
                        delete("/vehicles/" + id)
                )
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void update__should_return_updated_vehicle_when_successful() throws Exception {
        UpdateVehicleRequest request = new UpdateVehicleRequest("XYZ-5678", "Honda", "Civic", 2021);
        UUID id = UUID.randomUUID();
        Vehicle updatedVehicle = createMockVehicle(id, "XYZ-5678", "Honda", "Civic", 2021);

        when(vehicleService.update(eq(id), any(UpdateVehicleRequest.class))).thenReturn(updatedVehicle);

        var response = mockMvc.perform(
                put("/vehicles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        assertEquals(new VehicleResponse(updatedVehicle), objectMapper.readValue(response.getContentAsString(), VehicleResponse.class));
    }

    @Test
    void update__should_return_not_found_when_vehicle_not_exists() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateVehicleRequest request = new UpdateVehicleRequest("XYZ-5678", "Honda", "Civic", 2021);

        when(vehicleService.update(eq(id), any(UpdateVehicleRequest.class)))
                .thenThrow(new EntityNotFoundException("Veículo não encontrado com id: " + id));

        var response = mockMvc.perform(
                put("/vehicles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void update__should_return_code_bad_request_when_body_is_invalid() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateVehicleRequest request = new UpdateVehicleRequest("", "", "", 1800);

        var response = mockMvc.perform(
                put("/vehicles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andReturn()
                .getResponse();

        assertEquals(400, response.getStatus());
    }

    private Vehicle createMockVehicle(UUID id, String plate, String brand, String model, int year) {
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getId()).thenReturn(id);
        when(vehicle.getIdAsString()).thenReturn(id.toString());
        when(vehicle.getPlate()).thenReturn(plate);
        when(vehicle.getBrand()).thenReturn(brand);
        when(vehicle.getModel()).thenReturn(model);
        when(vehicle.getYear()).thenReturn(year);
        return vehicle;
    }
}