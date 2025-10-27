package soat_fiap.siaes.domain.serviceOrder.enums;

import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.shared.BusinessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceOrderStatusTest {

    private ServiceOrder mockOrder() {
        ServiceOrder order = new ServiceOrder();
        order.setOrderStatus(ServiceOrderStatus.RECEBIDA);
        return order;
    }

    @Test
    void canTransitionTo__admin_can_transition_to_any_status() {
        for (ServiceOrderStatus from : ServiceOrderStatus.values()) {
            for (ServiceOrderStatus to : ServiceOrderStatus.values()) {
                assertThat(from.canTransitionTo(to, RoleEnum.ADMIN))
                        .as("%s -> %s (ADMIN)", from, to)
                        .isTrue();
            }
        }
    }

    @Test
    void canTransitionTo__collaborator_transitions() {
        assertThat(ServiceOrderStatus.RECEBIDA.canTransitionTo(ServiceOrderStatus.EM_DIAGNOSTICO, RoleEnum.COLLABORATOR)).isTrue();
        assertThat(ServiceOrderStatus.RECEBIDA.canTransitionTo(ServiceOrderStatus.AGUARDANDO_ESTOQUE, RoleEnum.COLLABORATOR)).isTrue();
        assertThat(ServiceOrderStatus.RECEBIDA.canTransitionTo(ServiceOrderStatus.EM_EXECUCAO, RoleEnum.COLLABORATOR)).isFalse();

        assertThat(ServiceOrderStatus.EM_DIAGNOSTICO.canTransitionTo(ServiceOrderStatus.AGUARDANDO_APROVACAO, RoleEnum.COLLABORATOR)).isTrue();
        assertThat(ServiceOrderStatus.EM_DIAGNOSTICO.canTransitionTo(ServiceOrderStatus.AGUARDANDO_ESTOQUE, RoleEnum.COLLABORATOR)).isTrue();
        assertThat(ServiceOrderStatus.EM_DIAGNOSTICO.canTransitionTo(ServiceOrderStatus.EM_EXECUCAO, RoleEnum.COLLABORATOR)).isFalse();

        assertThat(ServiceOrderStatus.APROVADO_CLIENTE.canTransitionTo(ServiceOrderStatus.EM_EXECUCAO, RoleEnum.COLLABORATOR)).isTrue();
        assertThat(ServiceOrderStatus.APROVADO_CLIENTE.canTransitionTo(ServiceOrderStatus.AGUARDANDO_ESTOQUE, RoleEnum.COLLABORATOR)).isTrue();
        assertThat(ServiceOrderStatus.APROVADO_CLIENTE.canTransitionTo(ServiceOrderStatus.REPROVADO_CLIENTE, RoleEnum.COLLABORATOR)).isFalse();
    }

    @Test
    void canTransitionTo__client_transitions() {
        assertThat(ServiceOrderStatus.AGUARDANDO_APROVACAO.canTransitionTo(ServiceOrderStatus.APROVADO_CLIENTE, RoleEnum.CLIENT)).isTrue();
        assertThat(ServiceOrderStatus.AGUARDANDO_APROVACAO.canTransitionTo(ServiceOrderStatus.REPROVADO_CLIENTE, RoleEnum.CLIENT)).isTrue();
        assertThat(ServiceOrderStatus.AGUARDANDO_APROVACAO.canTransitionTo(ServiceOrderStatus.EM_EXECUCAO, RoleEnum.CLIENT)).isFalse();
    }

    @Test
    void getAllowedTransitions__admin_always_returns_any() {
        for (ServiceOrderStatus status : ServiceOrderStatus.values()) {
            assertThat(status.getAllowedTransitions(RoleEnum.ADMIN))
                    .contains("Qualquer status");
        }
    }

    @Test
    void getAllowedTransitions__non_admin_contains_expected_text() {
        String text = ServiceOrderStatus.RECEBIDA.getAllowedTransitions(RoleEnum.COLLABORATOR);
        assertThat(text).contains("EM_DIAGNOSTICO").contains("AGUARDANDO_ESTOQUE");
    }

    @Test
    void validatePermissionForStatus__throws_exception_when_role_cannot_transition() {
        ServiceOrder order = mockOrder();

        assertThatThrownBy(() ->
                ServiceOrderStatus.validatePermissionForStatus(order, ServiceOrderStatus.EM_EXECUCAO, RoleEnum.COLLABORATOR)
        ).isInstanceOf(BusinessException.class)
                .hasMessageContaining("Transição inválida");
    }

    @Test
    void validatePermissionForStatus__does_not_throw_for_allowed_transition() {
        ServiceOrder order = mockOrder();

        ServiceOrderStatus.validatePermissionForStatus(order, ServiceOrderStatus.EM_DIAGNOSTICO, RoleEnum.COLLABORATOR);
        ServiceOrderStatus.validatePermissionForStatus(order, ServiceOrderStatus.AGUARDANDO_ESTOQUE, RoleEnum.COLLABORATOR);
    }

    @Test
    void validatePermissionForStatus__admin_never_throws() {
        ServiceOrder order = mockOrder();

        for (ServiceOrderStatus status : ServiceOrderStatus.values()) {
            ServiceOrderStatus.validatePermissionForStatus(order, status, RoleEnum.ADMIN);
        }
    }
}