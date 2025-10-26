package soat_fiap.siaes.domain.serviceOrder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ServiceOrderTest {

    @Mock
    private User mockUser;
    @Mock
    private Vehicle mockVehicle;


    private LocalDateTime fixedStartTime;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        fixedStartTime = LocalDateTime.now().minusHours(2).truncatedTo(ChronoUnit.SECONDS);
    }


    @Test
    void constructor_should_initialize_all_fields_correctly() {

        ServiceOrder order = new ServiceOrder(mockUser, mockVehicle, ServiceOrderStatusEnum.EM_EXECUCAO);


        assertThat(order.getUser()).isEqualTo(mockUser);
        assertThat(order.getVehicle()).isEqualTo(mockVehicle);
        assertThat(order.getOrderStatusEnum()).isEqualTo(ServiceOrderStatusEnum.EM_EXECUCAO);


        assertThat(order.getStartTime()).isCloseTo(LocalDateTime.now(), within(5, ChronoUnit.SECONDS));

        assertThat(order.getId()).isNull();
        assertThat(order.getEndTime()).isNull();
    }



    @Test
    void getDurationMinutes_should_return_null_when_startTime_is_null() {

        ServiceOrder order = new ServiceOrder();

        assertThat(order.getDurationMinutes()).isNull();
    }

    @Test
    void getDurationMinutes_should_calculate_duration_until_now_when_endTime_is_null() {

        ServiceOrder order = new ServiceOrder();
        order.setStartTime(fixedStartTime);


        Long duration = order.getDurationMinutes();

        assertThat(duration).isNotNull();
        assertThat(duration).isGreaterThanOrEqualTo(119L);
        assertThat(duration).isLessThanOrEqualTo(121L);
    }

    @Test
    void getDurationMinutes_should_calculate_duration_between_start_and_end_time() {

        ServiceOrder order = new ServiceOrder();
        LocalDateTime testEndTime = fixedStartTime.plusMinutes(90);

        order.setStartTime(fixedStartTime);
        order.setEndTime(testEndTime);

         assertThat(order.getDurationMinutes()).isEqualTo(90L);
    }


    @Test
    void setUpdateStatus_should_set_endTime_when_status_is_FINALIZADA() {

        ServiceOrder order = new ServiceOrder();
        order.setEndTime(null);

        order.setUpdateStatus(ServiceOrderStatusEnum.FINALIZADA);

         assertThat(order.getOrderStatusEnum()).isEqualTo(ServiceOrderStatusEnum.FINALIZADA);

        assertThat(order.getEndTime()).isCloseTo(LocalDateTime.now(), within(5, ChronoUnit.SECONDS));
    }

    @Test
    void setUpdateStatus_should_only_set_status_when_status_is_not_FINALIZADA() {

        ServiceOrder order = new ServiceOrder();
        LocalDateTime originalEndTime = LocalDateTime.now().minusDays(1);
        order.setEndTime(originalEndTime);


        order.setUpdateStatus(ServiceOrderStatusEnum.EM_EXECUCAO);


        assertThat(order.getOrderStatusEnum()).isEqualTo(ServiceOrderStatusEnum.EM_EXECUCAO);

        assertThat(order.getEndTime()).isEqualTo(originalEndTime);
    }
}
