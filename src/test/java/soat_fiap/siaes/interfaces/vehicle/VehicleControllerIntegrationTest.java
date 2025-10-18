package soat_fiap.siaes.interfaces.vehicle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;
import soat_fiap.siaes.interfaces.vehicle.dto.CreateVehicleRequest;
import soat_fiap.siaes.interfaces.vehicle.dto.VehicleResponse;
import soat_fiap.siaes.shared.utils.JsonPageUtils;

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
class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;


    @Test
    void findAll__should_return_empty_page_when_has_no_vehicles() throws Exception {
        var response = mockMvc.perform(get("/vehicles"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"content\":[]"));
    }

    @Test
    void findAll__should_return_page_of_vehicles_when_has_vehicles() throws Exception {
        Vehicle vehicle1 = new Vehicle("AAA-1111", "Toyota", "Corolla", 2020);
        Vehicle vehicle2 = new Vehicle("BBB-2222", "Honda", "Civic", 2021);
        VehicleResponse vehicleResponse1 = new VehicleResponse(vehicleRepository.save(vehicle1));
        VehicleResponse vehicleResponse2 = new VehicleResponse(vehicleRepository.save(vehicle2));

        var response = mockMvc.perform(get("/vehicles"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        List<VehicleResponse> vehicles = JsonPageUtils.getContentFromPage(objectMapper, response.getContentAsString(), VehicleResponse.class);

        assertThat(vehicles)
                .containsExactlyInAnyOrder(vehicleResponse1, vehicleResponse2);
    }

    @Test
    void findById__should_return_vehicle_when_found() throws Exception {
        Vehicle vehicle = new Vehicle("CCC-3333", "Ford", "Focus", 2019);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        var response = mockMvc.perform(get("/vehicles/{id}", savedVehicle.getId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        VehicleResponse vehicleResponse = objectMapper.readValue(response.getContentAsString(), VehicleResponse.class);
        assertEquals(savedVehicle.getIdAsString(), vehicleResponse.id());
        assertEquals("CCC3333", vehicleResponse.plate());
        assertEquals("Ford", vehicleResponse.brand());
        assertEquals("Focus", vehicleResponse.model());
        assertEquals(2019, vehicleResponse.year());
    }

    @Test
    void findById__should_return_not_found_when_vehicle_does_not_exist() throws Exception {
        var response = mockMvc.perform(get("/vehicles/{id}", "06bd4b36-f98f-4f52-bfd2-5d254409b885"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Veículo não encontrado com id: 06bd4b36-f98f-4f52-bfd2-5d254409b885"));
    }

    @Test
    void save__should_persist_vehicle_and_return_created_response() throws Exception {
        var request = new CreateVehicleRequest("XYZ-9876", "Honda", "Civic", 2021);

        var response = mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertThat(vehicleRepository.existsByPlate("XYZ9876")).isTrue();
        assertEquals(201, response.getStatus());

        VehicleResponse vehicleResponse = objectMapper.readValue(response.getContentAsString(), VehicleResponse.class);
        assertEquals("XYZ9876", vehicleResponse.plate());
        assertEquals("Honda", vehicleResponse.brand());
        assertEquals("Civic", vehicleResponse.model());
        assertEquals(2021, vehicleResponse.year());
        assertNotNull(vehicleResponse.id());
    }

    @Test
    void save__should_return_bad_request_when_plate_already_exists() throws Exception {
        vehicleRepository.save(new Vehicle("LMN-1234", "Ford", "Focus", 2019));
        var request = new CreateVehicleRequest("LMN-1234", "Chevrolet", "Cruze", 2020);

        var response = mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse();

        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("A placa 'LMN1234' já está em uso."));
    }

    @Test
    void delete__should_remove_vehicle_when_exists() throws Exception {
        Vehicle vehicle = new Vehicle("QRS-5678", "Nissan", "Sentra", 2018);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        var response = mockMvc.perform(delete("/vehicles/" + savedVehicle.getId()))
                .andReturn()
                .getResponse();

        assertEquals(204, response.getStatus());
        assertFalse(vehicleRepository.existsById(savedVehicle.getId()));
    }

    @Test
    void delete__should_return_not_found_when_vehicle_does_not_exist() throws Exception {
        UUID vehicleId = UUID.randomUUID();
        var response = mockMvc.perform(delete("/vehicles/" + vehicleId))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertFalse(vehicleRepository.existsById(vehicleId));
        assertTrue(response.getContentAsString().contains("Veículo não encontrado com id: " + vehicleId));
    }

    @Test
    void update__should_return_updated_vehicle_when_successful() throws Exception {
        Vehicle vehicle = new Vehicle("TUV-3456", "Mazda", "3", 2017);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        var updateRequest = new CreateVehicleRequest("TUV-3456", "Mazda", "Mazda3", 2018);

        var response = mockMvc.perform(put("/vehicles/" + savedVehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        VehicleResponse vehicleResponse = objectMapper.readValue(response.getContentAsString(), VehicleResponse.class);
        assertEquals(savedVehicle.getIdAsString(), vehicleResponse.id());
        assertEquals("TUV3456", vehicleResponse.plate());
        assertEquals("Mazda", vehicleResponse.brand());
        assertEquals("Mazda3", vehicleResponse.model());
        assertEquals(2018, vehicleResponse.year());
    }

    @Test
    void update__should_return_not_found_when_vehicle_does_not_exist() throws Exception {
        UUID vehicleId = UUID.randomUUID();
        var updateRequest = new CreateVehicleRequest("NOP-7890", "Subaru", "Impreza", 2016);

        var response = mockMvc.perform(put("/vehicles/" + vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
        assertTrue(response.getContentAsString().contains("Veículo não encontrado com id: " + vehicleId));
    }
}