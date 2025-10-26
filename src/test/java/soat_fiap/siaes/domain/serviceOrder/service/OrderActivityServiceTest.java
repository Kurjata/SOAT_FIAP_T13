package soat_fiap.siaes.domain.serviceOrder.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.service.ServiceLaborService;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderActivityRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.AddOrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.OrderActivityResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.AddOrderItemRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OrderActivityServiceTest {

    private OrderActivityService service;
    private OrderActivityRepository repository;
    private ServiceOrderService serviceOrderService;
    private OrderItemService supplyService;
    private ServiceLaborService serviceLaborService;

    private ServiceOrder order;
    private ServiceLabor labor;
    private OrderActivity activity;

    @BeforeEach
    void setUp() {
        repository = mock(OrderActivityRepository.class);
        serviceOrderService = mock(ServiceOrderService.class);
        supplyService = mock(OrderItemService.class);
        serviceLaborService = mock(ServiceLaborService.class);

        order = new ServiceOrder();
        ReflectionTestUtils.setField(order, "id", UUID.randomUUID());
        labor = new ServiceLabor("Troca de óleo", null);
        ReflectionTestUtils.setField(labor, "id", UUID.randomUUID());

        activity = new OrderActivity(order, labor);
        ReflectionTestUtils.setField(activity, "id", UUID.randomUUID());
        order.setOrderActivities(List.of(activity));

        service = new OrderActivityService(repository, serviceOrderService, supplyService, serviceLaborService);
    }

    @Test
    void findByServiceOrder__should_return_list_of_responses() {
        when(serviceOrderService.findByUUID(order.getId())).thenReturn(order);

        List<OrderActivityResponse> result = service.findByServiceOrder(order.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).serviceLabor().id()).isEqualTo(activity.getServiceLabor().getIdAsString());
    }

    @Test
    void findById__should_return_response_when_found() {
        when(repository.findById(activity.getId())).thenReturn(Optional.of(activity));

        OrderActivityResponse result = service.findById(activity.getId());

        assertThat(result).isNotNull();
        assertThat(result.serviceLabor().id()).isEqualTo(activity.getServiceLabor().getIdAsString());
    }

    @Test
    void findById__should_throw_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Atividade de ordem não encontrado");
    }

    @Test
    void create__should_create_activity_and_return_response() {
        AddOrderActivityRequest request = new AddOrderActivityRequest(
                order.getId(),
                labor.getId(),
                List.of(new AddOrderItemRequest(UUID.randomUUID(), UUID.randomUUID(), 2))
        );

        when(serviceOrderService.findByUUID(order.getId())).thenReturn(order);
        when(serviceLaborService.findEntityById(labor.getId())).thenReturn(labor);
        when(repository.save(any(OrderActivity.class))).thenAnswer(invocation -> {
            OrderActivity saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", UUID.randomUUID());
            return saved;
        });

        OrderActivityResponse result = service.create(request);

        assertThat(result).isNotNull();
        verify(repository).save(any(OrderActivity.class));
        verify(supplyService).create(any(AddOrderItemRequest.class));
    }

    @Test
    void create__should_create_activity_with_no_items() {
        AddOrderActivityRequest request = new AddOrderActivityRequest(
                order.getId(),
                labor.getId(),
                List.of()
        );

        when(serviceOrderService.findByUUID(order.getId())).thenReturn(order);
        when(serviceLaborService.findEntityById(labor.getId())).thenReturn(labor);
        when(repository.save(any(OrderActivity.class))).thenAnswer(invocation -> {
            OrderActivity saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", UUID.randomUUID());
            return saved;
        });

        OrderActivityResponse result = service.create(request);

        assertThat(result).isNotNull();
        verify(repository).save(any(OrderActivity.class));
        verify(supplyService, never()).create(any());
    }

    @Test
    void update__should_update_activity_and_items() {
        AddOrderActivityRequest request = new AddOrderActivityRequest(
                order.getId(),
                labor.getId(),
                List.of(new AddOrderItemRequest(UUID.randomUUID(), UUID.randomUUID(), 2))
        );

        when(repository.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(serviceLaborService.findEntityById(labor.getId())).thenReturn(labor);
        when(repository.save(activity)).thenReturn(activity);

        OrderActivityResponse result = service.update(activity.getId(), request);

        assertThat(result).isNotNull();
        verify(repository).save(activity);
        verify(supplyService).create(any(AddOrderItemRequest.class));
    }

    @Test
    void update__should_throw_when_activity_not_found() {
        UUID id = UUID.randomUUID();
        AddOrderActivityRequest request = new AddOrderActivityRequest(order.getId(), labor.getId(), null);
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Item da ordem não encontrado");
    }

    @Test
    void delete__should_delete_activity_and_items() {
        when(repository.findById(activity.getId())).thenReturn(Optional.of(activity));

        service.delete(activity.getId());

        verify(repository).delete(activity);
    }

    @Test
    void delete__should_throw_when_activity_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Item da ordem não encontrado");
    }
}