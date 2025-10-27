package soat_fiap.siaes.domain.serviceOrder.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import soat_fiap.siaes.domain.inventory.model.Item;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;
import soat_fiap.siaes.domain.inventory.repository.ItemRepository;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderActivityRepository;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderItemRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.AddOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.CreateOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.OrderItemResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderItemServiceTest {

    private OrderItemService service;
    private OrderItemRepository repository;
    private OrderActivityRepository orderActivityRepository;
    private ItemRepository itemRepository;

    private OrderActivity orderActivity;
    private Item item;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        repository = mock(OrderItemRepository.class);
        orderActivityRepository = mock(OrderActivityRepository.class);
        itemRepository = mock(ItemRepository.class);

        orderActivity = new OrderActivity(mock(ServiceOrder.class), mock(ServiceLabor.class));
        ReflectionTestUtils.setField(orderActivity, "id", UUID.randomUUID());

        item = new Part("Parafuso", BigDecimal.TEN, UnitMeasure.UNIT, 100, 0, "9482701563914", "Fornecedor X", 10);
        ReflectionTestUtils.setField(item, "id", UUID.randomUUID());

        orderItem = new OrderItem(orderActivity, item, 5, item.getUnitPrice());
        ReflectionTestUtils.setField(orderItem, "id", UUID.randomUUID());

        orderActivity.setOrderItems(List.of(orderItem));

        service = new OrderItemService(repository, orderActivityRepository, itemRepository);
    }

    @Test
    void findAllByOrderActivity__should_return_list_of_responses() {
        when(orderActivityRepository.findById(orderActivity.getId())).thenReturn(Optional.of(orderActivity));

        List<OrderItemResponse> result = service.findAllByOrderActivity(orderActivity.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).quantity()).isEqualTo(orderItem.getQuantity());
    }

    @Test
    void findAllByOrderActivity__should_throw_when_activity_not_found() {
        UUID id = UUID.randomUUID();
        when(orderActivityRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findAllByOrderActivity(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Item da ordem não encontrado");
    }

    @Test
    void findById__should_return_response_when_found() {
        when(repository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));

        OrderItemResponse result = service.findById(orderItem.getId());

        assertThat(result).isNotNull();
        assertThat(result.quantity()).isEqualTo(orderItem.getQuantity());
    }

    @Test
    void findById__should_throw_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Insumo não encontrado");
    }

    @Test
    void create__should_create_item_and_return_response() {
        AddOrderItemRequest request = new AddOrderItemRequest(orderActivity.getId(), item.getId(), 3);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(orderActivityRepository.findById(orderActivity.getId())).thenReturn(Optional.of(orderActivity));
        when(repository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", UUID.randomUUID());
            return saved;
        });

        OrderItemResponse result = service.create(request);

        assertThat(result).isNotNull();
        verify(repository).save(any(OrderItem.class));
    }

    @Test
    void create__should_throw_when_item_not_found() {
        UUID id = UUID.randomUUID();
        AddOrderItemRequest request = new AddOrderItemRequest(orderActivity.getId(), id, 1);
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Insumo não encontrado");
    }

    @Test
    void create__should_throw_when_order_activity_not_found() {
        UUID id = UUID.randomUUID();
        AddOrderItemRequest request = new AddOrderItemRequest(id, item.getId(), 1);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(orderActivityRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Item da ordem não encontrado");
    }

    @Test
    void update__should_update_item_and_return_response() {
        CreateOrderItemRequest request = new CreateOrderItemRequest(item.getId(), 10);

        when(repository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(repository.save(orderItem)).thenReturn(orderItem);

        OrderItemResponse result = service.update(orderItem.getId(), request);

        assertThat(result).isNotNull();
        assertThat(result.quantity()).isEqualTo(10);
        verify(repository).save(orderItem);
    }

    @Test
    void update__should_throw_when_item_not_found() {
        UUID id = UUID.randomUUID();
        CreateOrderItemRequest request = new CreateOrderItemRequest(item.getId(), 1);
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Item da ordem não encontrado");
    }


    @Test
    void update__should_throw_when_item_in_repository_not_found() {
        UUID id = UUID.randomUUID();
        CreateOrderItemRequest request = new CreateOrderItemRequest(id, 1);
        when(repository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(orderItem.getId(), request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Insumo não encontrado");
    }

    @Test
    void delete__should_delete_item() {
        when(repository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));

        service.delete(orderItem.getId());

        verify(repository).delete(orderItem);
    }

    @Test
    void delete__should_throw_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Insumo não encontrado");
    }
}