package soat_fiap.siaes.interfaces.serviceOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import soat_fiap.siaes.domain.serviceOrder.service.OrderActivityService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrderActivityResponse;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class OrderActivityControllerTest {

    @Mock
    private OrderActivityService service;

    @InjectMocks
    private OrderActivityController controller;

    private UUID id;
    private UUID orderId;
    private OrderActivityResponse response;
    private OrderActivityRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        orderId = UUID.randomUUID();
        response = mock(OrderActivityResponse.class);
        request = mock(OrderActivityRequest.class); // mock evita erro de construtor
    }

    @Test
    void testGetByOrder() {
        List<OrderActivityResponse> list = List.of(response);
        when(service.findByServiceOrder(orderId)).thenReturn(list);

        ResponseEntity<List<OrderActivityResponse>> result = controller.getByOrder(orderId);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(list, result.getBody());
        verify(service).findByServiceOrder(orderId);
    }

    @Test
    void testGetById() {
        when(service.findById(id)).thenReturn(response);

        ResponseEntity<OrderActivityResponse> result = controller.getById(id);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(service).findById(id);
    }

    @Test
    void testCreate() {
        when(service.create(any(OrderActivityRequest.class))).thenReturn(response);

        ResponseEntity<OrderActivityResponse> result = controller.create(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(service).create(request);
    }

    @Test
    void testUpdate() {
        when(service.update(eq(id), any(OrderActivityRequest.class))).thenReturn(response);

        ResponseEntity<OrderActivityResponse> result = controller.update(id, request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(service).update(id, request);
    }

    @Test
    void testDelete() {
        doNothing().when(service).delete(id);

        ResponseEntity<Void> result = controller.delete(id);

        assertEquals(204, result.getStatusCodeValue());
        verify(service).delete(id);
    }


}
