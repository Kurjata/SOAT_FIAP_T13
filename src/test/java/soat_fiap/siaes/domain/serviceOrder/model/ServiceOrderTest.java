package soat_fiap.siaes.domain.serviceOrder.model;

import org.junit.jupiter.api.Test;
import soat_fiap.siaes.application.event.part.UpdateStockEvent;
import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatus;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceOrderTest {
    private User user;
    private Vehicle vehicle;

    private ServiceOrder createServiceOrder(ServiceOrderStatus status) {
        user = new User("Vinicius", "vinicius", "123456", RoleEnum.ADMIN, "930.488.160-97", "vinicius@email.com");
        vehicle = new Vehicle("ABC-1234", "Honda", "Civic", 2022);
        return new ServiceOrder(user, vehicle, status);
    }

    @Test
    void constructor__should_initialize_fields_correctly() {
        ServiceOrder order = createServiceOrder(ServiceOrderStatus.RECEBIDA);

        assertThat(order.getUser()).isEqualTo(user);
        assertThat(order.getVehicle()).isEqualTo(vehicle);
        assertThat(order.getOrderStatus()).isEqualTo(ServiceOrderStatus.RECEBIDA);
        assertThat(order.getStartTime()).isNotNull();
        assertThat(order.getEndTime()).isNull();
    }

    @Test
    void getDurationMinutes__should_return_correct_duration_when_endTime_is_present() {
        ServiceOrder order = createServiceOrder(ServiceOrderStatus.RECEBIDA);
        order.setStartTime(LocalDateTime.now().minusMinutes(90));
        order.setEndTime(LocalDateTime.now());

        assertThat(order.getDurationMinutes()).isBetween(89L, 91L);
    }

    @Test
    void getDurationMinutes__should_return_correct_duration_when_endTime_is_null() {
        ServiceOrder order = createServiceOrder(ServiceOrderStatus.ENTREGUE);
        order.setStartTime(LocalDateTime.now().minusMinutes(30));

        Long duration = order.getDurationMinutes();

        assertThat(duration).isBetween(29L, 31L);
    }

    @Test
    void getDurationMinutes__should_return_null_when_startTime_is_null() {
        ServiceOrder order = createServiceOrder(ServiceOrderStatus.RECEBIDA);
        order.setStartTime(null);

        assertThat(order.getDurationMinutes()).isNull();
    }

    @Test
    void setUpdateStatus__should_set_status_and_endTime_when_finalizada() {
        ServiceOrder order = createServiceOrder(ServiceOrderStatus.RECEBIDA);

        order.setUpdateStatus(ServiceOrderStatus.FINALIZADA);

        assertThat(order.getOrderStatus()).isEqualTo(ServiceOrderStatus.FINALIZADA);
        assertThat(order.getEndTime()).isNotNull();
    }

    @Test
    void setUpdateStatus__should_set_status_without_endTime_when_not_finalizada() {
        ServiceOrder order = createServiceOrder(ServiceOrderStatus.RECEBIDA);

        order.setUpdateStatus(ServiceOrderStatus.APROVADO_CLIENTE);

        assertThat(order.getOrderStatus()).isEqualTo(ServiceOrderStatus.APROVADO_CLIENTE);
        assertThat(order.getEndTime()).isNull();
    }

    @Test
    void updateStatus__should_register_event_when_status_is_aprovado_cliente() {
        ServiceOrder order = createServiceOrder(ServiceOrderStatus.AGUARDANDO_APROVACAO);

        order.updateStatus(ServiceOrderStatus.APROVADO_CLIENTE);

        assertThat(order.getOrderStatus()).isEqualTo(ServiceOrderStatus.APROVADO_CLIENTE);
        assertThat(order.getDomainEvents())
                .hasSize(1)
                .first()
                .isInstanceOfSatisfying(UpdateStockEvent.class, event ->
                        assertThat(event.stockOperation()).isEqualTo(StockOperation.RESERVE_STOCK));
    }

    @Test
    void updateStatus__should_register_event_when_status_is_reprovado_cliente() {
        ServiceOrder order = createServiceOrder(ServiceOrderStatus.RECEBIDA);

        order.updateStatus(ServiceOrderStatus.REPROVADO_CLIENTE);

        assertThat(order.getOrderStatus()).isEqualTo(ServiceOrderStatus.REPROVADO_CLIENTE);
        assertThat(order.getDomainEvents())
                .hasSize(1)
                .first()
                .isInstanceOfSatisfying(UpdateStockEvent.class, event ->
                        assertThat(event.stockOperation()).isEqualTo(StockOperation.CANCEL_RESERVATION));
    }

    @Test
    void updateStatus__should_register_event_when_status_is_em_execucao() {
        ServiceOrder order = createServiceOrder(ServiceOrderStatus.RECEBIDA);

        order.updateStatus(ServiceOrderStatus.EM_EXECUCAO);

        assertThat(order.getOrderStatus()).isEqualTo(ServiceOrderStatus.EM_EXECUCAO);
        assertThat(order.getDomainEvents())
                .hasSize(1)
                .first()
                .isInstanceOfSatisfying(UpdateStockEvent.class, event ->
                        assertThat(event.stockOperation()).isEqualTo(StockOperation.CONFIRM_RESERVATION));
    }

    @Test
    void updateStatus__should_not_register_event_when_status_is_finalizada_or_aberta() {
        ServiceOrder order = createServiceOrder(ServiceOrderStatus.RECEBIDA);

        order.updateStatus(ServiceOrderStatus.FINALIZADA);
        order.updateStatus(ServiceOrderStatus.RECEBIDA);

        assertThat(order.getDomainEvents()).isEmpty();
    }
}