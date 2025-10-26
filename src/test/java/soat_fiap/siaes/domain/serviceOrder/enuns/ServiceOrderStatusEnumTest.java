package soat_fiap.siaes.domain.serviceOrder.enuns;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.shared.BusinessException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum.*;
import static soat_fiap.siaes.domain.user.model.RoleEnum.*;

public class ServiceOrderStatusEnumTest {

    private final ServiceOrder mockOrder = mock(ServiceOrder.class);

    @Test
    @DisplayName("Deve retornar a descrição correta para cada status")
    void getDescricao_shouldReturnCorrectDescription() {
        assertEquals("OS criada e recebida pelo sistema", RECEBIDA.getDescricao());
        assertEquals("Serviço finalizado, pronto para retirada", FINALIZADA.getDescricao());
        assertEquals("Reprovado pelo cliente", REPROVADO_CLIENTE.getDescricao());
    }

    @Test
    @DisplayName("ADMIN deve poder transicionar de qualquer status para qualquer status")
    void canTransitionTo_Admin_shouldAlwaysBeTrue() {
        assertTrue(RECEBIDA.canTransitionTo(EM_DIAGNOSTICO, ADMIN));
        assertTrue(FINALIZADA.canTransitionTo(RECEBIDA, ADMIN));
        assertTrue(AGUARDANDO_APROVACAO.canTransitionTo(EM_EXECUCAO, ADMIN));
    }

    @Test
    @DisplayName("COLLABORATOR deve ter transições válidas a partir de RECEBIDA")
    void canTransitionTo_Collaborator_fromRecebida() {
        assertTrue(RECEBIDA.canTransitionTo(EM_DIAGNOSTICO, COLLABORATOR));
        assertTrue(RECEBIDA.canTransitionTo(AGUARDANDO_ESTOQUE, COLLABORATOR));
        assertFalse(RECEBIDA.canTransitionTo(FINALIZADA, COLLABORATOR));
        assertFalse(RECEBIDA.canTransitionTo(ENTREGUE, COLLABORATOR));
    }

    @Test
    @DisplayName("COLLABORATOR deve ter transições válidas a partir de EM_DIAGNOSTICO")
    void canTransitionTo_Collaborator_fromEmDiagnostico() {
        assertTrue(EM_DIAGNOSTICO.canTransitionTo(AGUARDANDO_APROVACAO, COLLABORATOR));
        assertTrue(EM_DIAGNOSTICO.canTransitionTo(AGUARDANDO_ESTOQUE, COLLABORATOR));
        assertFalse(EM_DIAGNOSTICO.canTransitionTo(FINALIZADA, COLLABORATOR));
    }

    @Test
    @DisplayName("COLLABORATOR deve ter transições válidas a partir de APROVADO_CLIENTE")
    void canTransitionTo_Collaborator_fromAprovadoCliente() {
        assertTrue(APROVADO_CLIENTE.canTransitionTo(EM_EXECUCAO, COLLABORATOR));
        assertTrue(APROVADO_CLIENTE.canTransitionTo(AGUARDANDO_ESTOQUE, COLLABORATOR));
        assertFalse(APROVADO_CLIENTE.canTransitionTo(EM_EXECUCAO, CLIENT));
    }

    @Test
    @DisplayName("COLLABORATOR deve ter transições válidas a partir de REPROVADO_CLIENTE")
    void canTransitionTo_Collaborator_fromReprovadoCliente() {
        assertTrue(REPROVADO_CLIENTE.canTransitionTo(EM_EXECUCAO, COLLABORATOR));
        assertTrue(REPROVADO_CLIENTE.canTransitionTo(AGUARDANDO_ESTOQUE, COLLABORATOR));
        assertTrue(REPROVADO_CLIENTE.canTransitionTo(FINALIZADA, COLLABORATOR));
        assertFalse(REPROVADO_CLIENTE.canTransitionTo(ENTREGUE, COLLABORATOR));
    }

    @Test
    @DisplayName("COLLABORATOR deve ter transições válidas a partir de EM_EXECUCAO")
    void canTransitionTo_Collaborator_fromEmExecucao() {
        assertTrue(EM_EXECUCAO.canTransitionTo(FINALIZADA, COLLABORATOR));
        assertTrue(EM_EXECUCAO.canTransitionTo(AGUARDANDO_ESTOQUE, COLLABORATOR));
    }

    @Test
    @DisplayName("COLLABORATOR deve ter transições válidas a partir de FINALIZADA")
    void canTransitionTo_Collaborator_fromFinalizada() {
        assertTrue(FINALIZADA.canTransitionTo(ENTREGUE, COLLABORATOR));
        assertFalse(FINALIZADA.canTransitionTo(EM_EXECUCAO, COLLABORATOR));
    }

    @Test
    @DisplayName("COLLABORATOR deve ter transições válidas a partir de AGUARDANDO_ESTOQUE")
    void canTransitionTo_Collaborator_fromAguardandoEstoque() {
        assertTrue(AGUARDANDO_ESTOQUE.canTransitionTo(EM_DIAGNOSTICO, COLLABORATOR));
        assertTrue(AGUARDANDO_ESTOQUE.canTransitionTo(EM_EXECUCAO, COLLABORATOR));
        assertFalse(AGUARDANDO_ESTOQUE.canTransitionTo(EM_DIAGNOSTICO, CLIENT));
    }


    @Test
    @DisplayName("CLIENT deve ter transições válidas a partir de AGUARDANDO_APROVACAO")
    void canTransitionTo_Client_fromAguardandoAprovacao() {
        assertTrue(AGUARDANDO_APROVACAO.canTransitionTo(APROVADO_CLIENTE, CLIENT));
        assertTrue(AGUARDANDO_APROVACAO.canTransitionTo(REPROVADO_CLIENTE, CLIENT));
        assertFalse(AGUARDANDO_APROVACAO.canTransitionTo(APROVADO_CLIENTE, COLLABORATOR));
    }

    @Test
    @DisplayName("Outras combinações de status e roles devem ser falsas")
    void canTransitionTo_shouldBeFalseForInvalidCombinations() {
        assertFalse(EM_DIAGNOSTICO.canTransitionTo(AGUARDANDO_APROVACAO, CLIENT));
        assertFalse(AGUARDANDO_APROVACAO.canTransitionTo(APROVADO_CLIENTE, COLLABORATOR));
        assertFalse(ENTREGUE.canTransitionTo(RECEBIDA, COLLABORATOR));
        assertFalse(ENTREGUE.canTransitionTo(FINALIZADA, CLIENT));
    }

    @Test
    @DisplayName("getAllowedTransitions deve retornar mensagem correta para ADMIN")
    void getAllowedTransitions_shouldReturnAdminMessage() {
        assertEquals("Qualquer status (ADMIN possui permissão total).", RECEBIDA.getAllowedTransitions(ADMIN));
    }

    @Test
    @DisplayName("getAllowedTransitions deve retornar mensagem correta para AGUARDANDO_APROVACAO (CLIENT)")
    void getAllowedTransitions_shouldReturnClientMessage() {
        String expected = "Pode alterar para: APROVADO_CLIENTE ou REPROVADO_CLIENTE (CLIENT).";
        assertEquals(expected, AGUARDANDO_APROVACAO.getAllowedTransitions(CLIENT));
    }

    @Test
    @DisplayName("getAllowedTransitions deve retornar mensagem correta para FINALIZADA (COLLABORATOR)")
    void getAllowedTransitions_shouldReturnCollaboratorMessage() {
        String expected = "Pode alterar para: ENTREGUE (COLLABORATOR).";
        assertEquals(expected, FINALIZADA.getAllowedTransitions(COLLABORATOR));
    }

    @Test
    @DisplayName("getAllowedTransitions deve retornar mensagem padrão para status final (ENTREGUE)")
    void getAllowedTransitions_shouldReturnDefaultMessage() {
        String expected = "Nenhuma transição permitida a partir deste status.";
        assertEquals(expected, ENTREGUE.getAllowedTransitions(COLLABORATOR));
    }

    @Test
    @DisplayName("validatePermissionForStatus não deve lançar exceção para transição válida")
    void validatePermissionForStatus_shouldNotThrowException_forValidTransition() {

        when(mockOrder.getOrderStatusEnum()).thenReturn(RECEBIDA);

        assertDoesNotThrow(() -> ServiceOrderStatusEnum.validatePermissionForStatus(
                mockOrder, EM_DIAGNOSTICO, COLLABORATOR));
    }

    @Test
    @DisplayName("validatePermissionForStatus deve lançar BusinessException para transição inválida")
    void validatePermissionForStatus_shouldThrowException_forInvalidTransition() {
        when(mockOrder.getOrderStatusEnum()).thenReturn(RECEBIDA);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> ServiceOrderStatusEnum.validatePermissionForStatus(mockOrder, FINALIZADA, COLLABORATOR));

        String expectedMessagePart = "Transição inválida: o usuário com perfil 'COLLABORATOR' não pode alterar o status de 'RECEBIDA' para 'FINALIZADA'.";
        assertTrue(exception.getMessage().contains(expectedMessagePart));
        assertTrue(exception.getMessage().contains(RECEBIDA.getAllowedTransitions(COLLABORATOR)));
    }

    @Test
    @DisplayName("validatePermissionForStatus deve lançar BusinessException para role incorreta")
    void validatePermissionForStatus_shouldThrowException_forInvalidRole() {

        when(mockOrder.getOrderStatusEnum()).thenReturn(AGUARDANDO_APROVACAO);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> ServiceOrderStatusEnum.validatePermissionForStatus(mockOrder, APROVADO_CLIENTE, COLLABORATOR));

        String expectedMessagePart = "o usuário com perfil 'COLLABORATOR' não pode alterar o status de 'AGUARDANDO_APROVACAO' para 'APROVADO_CLIENTE'.";
        assertTrue(exception.getMessage().contains(expectedMessagePart));
        assertTrue(exception.getMessage().contains(AGUARDANDO_APROVACAO.getAllowedTransitions(COLLABORATOR)));
    }
}
