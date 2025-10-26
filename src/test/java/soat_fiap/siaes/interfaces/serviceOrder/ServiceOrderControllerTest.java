package soat_fiap.siaes.interfaces.serviceOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrder.service.ServiceOrderService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ServiceOrderControllerTest {

    @Mock
    private ServiceOrderService service;

    @InjectMocks
    private ServiceOrderController controller;

    private Pageable pageable;
    private UUID id;
    private ServiceOrderResponse response;
    private ServiceOrderRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pageable = PageRequest.of(0, 10);
        id = UUID.randomUUID();
        response = mock(ServiceOrderResponse.class);
        request = mock(ServiceOrderRequest.class);
    }

    @Test
    void testFindAll() {
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of(response));
        when(service.findAll(pageable)).thenReturn(page);

        ResponseEntity<Page<ServiceOrderResponse>> result = controller.findAll(pageable);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(page, result.getBody());
        verify(service).findAll(pageable);
    }

    @Test
    void testFindById() {
        when(service.findById(id)).thenReturn(response);

        ResponseEntity<ServiceOrderResponse> result = controller.findById(id);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(service).findById(id);
    }

    @Test
    void testGetByUserDocument() {
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of(response));
        when(service.findByUserDocument(anyString(), eq(pageable))).thenReturn(page);

        ResponseEntity<Page<ServiceOrderResponse>> result = controller.getByUserDocument("12345678900", pageable);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(page, result.getBody());
        verify(service).findByUserDocument("12345678900", pageable);
    }

    @Test
    void testFindByUser() {
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of(response));
        when(service.findByUserId(id, pageable)).thenReturn(page);

        ResponseEntity<Page<ServiceOrderResponse>> result = controller.findByUser(id, pageable);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(page, result.getBody());
        verify(service).findByUserId(id, pageable);
    }

    @Test
    void testGetByVehicleId() {
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of(response));
        when(service.findByVehicleId(id, pageable)).thenReturn(page);

        ResponseEntity<Page<ServiceOrderResponse>> result = controller.getByVehicleId(id, pageable);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(page, result.getBody());
        verify(service).findByVehicleId(id, pageable);
    }

    @Test
    void testGetByVehiclePlate() {
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of(response));
        when(service.findByVehiclePlate("ABC1234", pageable)).thenReturn(page);

        ResponseEntity<Page<ServiceOrderResponse>> result = controller.getByVehiclePlate("ABC1234", pageable);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(page, result.getBody());
        verify(service).findByVehiclePlate("ABC1234", pageable);
    }

    @Test
    void testCreate() {
        when(service.createServiceOrder(request)).thenReturn(response);

        ResponseEntity<ServiceOrderResponse> result = controller.create(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(service).createServiceOrder(request);
    }

    @Test
    void testUpdateStatus() {

        when(service.updateStatus(id, ServiceOrderStatusEnum.FINALIZADA)).thenReturn(response);

        ResponseEntity<ServiceOrderResponse> result = controller.updateStatus(id, ServiceOrderStatusEnum.FINALIZADA);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(service).updateStatus(id, ServiceOrderStatusEnum.FINALIZADA);
    }

    @Test
    void testDelete() {
        doNothing().when(service).delete(id);

        ResponseEntity<Void> result = controller.delete(id);

        assertEquals(204, result.getStatusCodeValue());
        verify(service).delete(id);
    }

    @Test
    void testFindAllMe() {
        Page<ServiceOrderResponse> page = new PageImpl<>(List.of(response));
        when(service.findAllMe(pageable)).thenReturn(page);

        ResponseEntity<Page<ServiceOrderResponse>> result = controller.findAllMe(pageable);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(page, result.getBody());
        verify(service).findAllMe(pageable);
    }
}
