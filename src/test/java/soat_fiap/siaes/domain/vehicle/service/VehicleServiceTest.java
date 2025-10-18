package soat_fiap.siaes.domain.vehicle.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;
import soat_fiap.siaes.interfaces.vehicle.dto.UpdateVehicleRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleServiceTest {
    private VehicleService vehicleService;
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        vehicleRepository = mock(VehicleRepository.class);
        vehicleService = new VehicleService(vehicleRepository);
    }

    private Vehicle createVehicle(String plate, String brand, String model, int year) {
        return new Vehicle(plate, brand, model, year);
    }

    @Test
    void save__should_return_vehicle_saved() {
        Vehicle vehicle = createVehicle("ABC-1234", "Toyota", "Corolla", 2020);

        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        Vehicle savedVehicle = vehicleService.save(vehicle);

        assertNotNull(savedVehicle);
        assertEquals("ABC1234", savedVehicle.getPlate());
        assertEquals("Toyota", savedVehicle.getBrand());
        assertEquals("Corolla", savedVehicle.getModel());
        assertEquals(2020, savedVehicle.getYear());
    }

    @Test
    void save__should_throw_exception_when_plate_already_exists() {
        Vehicle vehicle = createVehicle("ABC-1234", "Toyota", "Corolla", 2020);

        when(vehicleRepository.existsByPlate("ABC1234")).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.save(vehicle))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A placa 'ABC1234' já está em uso.");
    }

    @Test
    void update__should_throw_exception_when_plate_already_exists_for_different_vehicle() {
        UUID requestId = UUID.randomUUID();
        UpdateVehicleRequest request = new UpdateVehicleRequest("XYZ-5678", "Toyota", "Corolla", 2020);

        when(vehicleRepository.existsByPlateAndIdNot(request.plate(), requestId)).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.update(requestId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A placa 'XYZ5678' já está em uso por outro veículo.");
    }

    @Test
    void update__should_throw_exception_when_vehicle_not_found() {
        UUID requestId = UUID.randomUUID();
        UpdateVehicleRequest request = new UpdateVehicleRequest("XYZ-5678", "Toyota", "Corolla", 2020);

        when(vehicleRepository.existsByPlateAndIdNot(request.plate(), requestId)).thenReturn(false);
        when(vehicleRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.update(requestId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Veículo não encontrado com id: " + requestId);
    }

    @Test
    void update_should_return_updated_vehicle() {
        UUID requestId = UUID.randomUUID();
        UpdateVehicleRequest request = new UpdateVehicleRequest("XYZ-5678", "Toyota", "Corolla", 2020);
        Vehicle existingVehicle = createVehicle("ABC-1234", "Honda", "Civic", 2019);

        when(vehicleRepository.existsByPlateAndIdNot(request.plate(), requestId)).thenReturn(false);
        when(vehicleRepository.findById(requestId)).thenReturn(Optional.of(existingVehicle));
        when(vehicleRepository.save(existingVehicle)).thenReturn(existingVehicle);

        Vehicle updatedVehicle = vehicleService.update(requestId, request);

        assertNotNull(updatedVehicle);
        assertEquals("XYZ5678", updatedVehicle.getPlate());
        assertEquals("Toyota", updatedVehicle.getBrand());
        assertEquals("Corolla", updatedVehicle.getModel());
        assertEquals(2020, updatedVehicle.getYear());
    }

    @Test
    void findAll__should_return_page_of_vehicles() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Vehicle> vehicles = List.of(
                new Vehicle("ABC-1234", "Toyota", "Corolla", 2020),
                new Vehicle("XYZ-5678", "Honda", "Civic", 2021)
        );
        Page<Vehicle> expectedPage = new PageImpl<>(vehicles, pageable, vehicles.size());
        when(vehicleRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Vehicle> result = vehicleService.findAll(pageable);

        assertThat(result).isEqualTo(expectedPage);
        verify(vehicleRepository, times(1)).findAll(pageable);
    }

    @Test
    void findById__should_return_vehicle_when_exists() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = createVehicle("XYZ-9999", "Fiat", "Uno", 2015);

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(vehicle));

        Vehicle found = vehicleService.findById(id);

        assertNotNull(found);
        assertEquals(vehicle, found);
        assertEquals("XYZ9999", found.getPlate());
        assertEquals("Fiat", found.getBrand());
        assertEquals(2015, found.getYear());
        assertEquals("Uno", found.getModel());
    }

    @Test
    void findById__should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Veículo não encontrado com id: " + id);
    }

    @Test
    void deleteById__should_delete_when_vehicle_exists() {
        UUID id = UUID.randomUUID();
        when(vehicleRepository.existsById(id)).thenReturn(true);

        vehicleService.deleteById(id);

        verify(vehicleRepository).deleteById(id);
    }

    @Test
    void deleteById__should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        when(vehicleRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> vehicleService.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Veículo não encontrado com id: " + id);
    }

    @Test
    void findByPlateIgnoreCase_should_return_vehicle_when_found() {
        Vehicle vehicle = createVehicle("ABC-1234", "Toyota", "Corolla", 2020);

        when(vehicleRepository.findByPlateIgnoreCase("ABC1234")).thenReturn(Optional.of(vehicle));
        Vehicle found = vehicleService.findByPlateIgnoreCase("ABC1234");

        assertNotNull(found);
        assertEquals(vehicle, found);
        assertEquals("ABC1234", found.getPlate());
        assertEquals("Corolla", found.getModel());
    }

    @Test
    void findByPlateIgnoreCase__should_throw_exception_when_not_found() {
        when(vehicleRepository.findByPlateIgnoreCase("ABC1234")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.findByPlateIgnoreCase("ABC1234"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Veículo com placa ABC1234 não encontrado");
    }
}