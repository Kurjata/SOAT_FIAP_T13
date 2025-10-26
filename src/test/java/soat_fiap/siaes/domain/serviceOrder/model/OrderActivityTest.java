package soat_fiap.siaes.domain.serviceOrder.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class OrderActivityTest {

    @Mock
    private ServiceOrder mockServiceOrder;

    @Mock
    private ServiceLabor mockServiceLabor;

    private final List<OrderItem> orderItems = Collections.emptyList();

    private final UUID ACTIVITY_ID = UUID.randomUUID();

    @Test
    void customConstructor_should_set_serviceOrder_and_serviceLabor() {
        OrderActivity activity = new OrderActivity(
                mockServiceOrder,
                mockServiceLabor
        );

        assertNotNull(activity);
        assertEquals(mockServiceOrder, activity.getServiceOrder());
        assertEquals(mockServiceLabor, activity.getServiceLabor());
        assertNull(activity.getId());
        assertNull(activity.getOrderItems());
    }

    @Test
    void allArgsConstructor_should_set_all_fields() {
        OrderActivity activity = new OrderActivity(
                ACTIVITY_ID,
                mockServiceOrder,
                mockServiceLabor,
                orderItems
        );

        assertNotNull(activity);
        assertEquals(ACTIVITY_ID, activity.getId());
        assertEquals(mockServiceOrder, activity.getServiceOrder());
        assertEquals(mockServiceLabor, activity.getServiceLabor());
        assertEquals(orderItems, activity.getOrderItems());
    }

    @Test
    void noArgsConstructor_should_create_empty_instance() {
        OrderActivity activity = new OrderActivity();

        assertNotNull(activity);
        assertNull(activity.getId());
        assertNull(activity.getServiceOrder());
        assertNull(activity.getServiceLabor());
        assertNull(activity.getOrderItems());
    }

    @Test
    void dataAnnotation_should_cover_getters_setters_and_utility_methods() {
        OrderActivity activity1 = new OrderActivity(ACTIVITY_ID, mockServiceOrder, mockServiceLabor, orderItems);
        OrderActivity activity2 = new OrderActivity(ACTIVITY_ID, mockServiceOrder, mockServiceLabor, orderItems);

        OrderActivity activity3 = new OrderActivity(UUID.randomUUID(), mockServiceOrder, mockServiceLabor, null);

        ServiceLabor newLabor = mock(ServiceLabor.class);
        activity3.setServiceLabor(newLabor);

        assertEquals(newLabor, activity3.getServiceLabor());

        assertThat(activity1).isEqualTo(activity2);
        assertThat(activity1.hashCode()).isEqualTo(activity2.hashCode());
        assertThat(activity1).isNotEqualTo(activity3);

        assertThat(activity1.toString()).contains("id=" + ACTIVITY_ID.toString());
        assertThat(activity1.toString()).contains("serviceOrder=" + mockServiceOrder);
    }
}
