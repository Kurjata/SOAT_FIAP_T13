package soat_fiap.siaes.application.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soat_fiap.siaes.application.event.part.UpdateStockEvent;
import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.service.ItemService;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PartStockListenerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private PartStockListener partStockListener;

    @Test
    void handle_should_call_processStockMovement_for_all_order_items() {
        UUID partId = UUID.randomUUID();
        int quantity = 5;
        StockOperation operation = StockOperation.CANCEL_RESERVATION;

        Part mockPart = mock(Part.class);
        when(mockPart.getId()).thenReturn(partId);

        OrderItem mockOrderItem = mock(OrderItem.class);
        when(mockOrderItem.getPartStock()).thenReturn(mockPart);
        when(mockOrderItem.getQuantity()).thenReturn(quantity);

        OrderActivity mockOrderActivity = mock(OrderActivity.class);
        when(mockOrderActivity.getOrderItems()).thenReturn(List.of(mockOrderItem));

        ServiceOrder mockOrder = mock(ServiceOrder.class);
        when(mockOrder.getOrderActivities()).thenReturn(List.of(mockOrderActivity));

        UpdateStockEvent event = new UpdateStockEvent(mockOrder, operation);

        partStockListener.handle(event);

        verify(itemService, times(1)).processStockMovement(
                partId,
                operation,
                quantity
        );

        verifyNoMoreInteractions(itemService);
    }

    @Test
    void handle_should_not_call_processStockMovement_when_orderActivities_is_empty() {

        ServiceOrder mockOrder = mock(ServiceOrder.class);
        when(mockOrder.getOrderActivities()).thenReturn(Collections.emptyList());

        UpdateStockEvent event = new UpdateStockEvent(mockOrder, StockOperation.CANCEL_RESERVATION);

        partStockListener.handle(event);

        verify(itemService, never()).processStockMovement(any(), any(), anyInt());
    }

    @Test
    void handle_should_not_call_processStockMovement_when_orderItems_is_empty() {

        OrderActivity mockOrderActivity = mock(OrderActivity.class);
        when(mockOrderActivity.getOrderItems()).thenReturn(Collections.emptyList());

        ServiceOrder mockOrder = mock(ServiceOrder.class);
        when(mockOrder.getOrderActivities()).thenReturn(List.of(mockOrderActivity));

       UpdateStockEvent event = new UpdateStockEvent(mockOrder, StockOperation.RESERVE_STOCK);

        partStockListener.handle(event);

        verify(itemService, never()).processStockMovement(any(), any(), anyInt());
    }
}
