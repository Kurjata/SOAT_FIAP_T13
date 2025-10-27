package soat_fiap.siaes.domain.serviceOrder.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.shared.BusinessException;

@Getter
@RequiredArgsConstructor
public enum ServiceOrderStatus {
    RECEBIDA("OS criada e recebida pelo sistema"),
    EM_DIAGNOSTICO("Veículo em fase de diagnóstico"),
    AGUARDANDO_ESTOQUE("Aguardando estoque"),
    AGUARDANDO_APROVACAO("Aguardando aprovação do cliente"),
    EM_EXECUCAO("Serviço em execução"),
    FINALIZADA("Serviço finalizado, pronto para retirada"),
    ENTREGUE("Veículo entregue ao cliente"),
    APROVADO_CLIENTE("Aprovado pelo cliente"),
    REPROVADO_CLIENTE("Reprovado pelo cliente");

    private final String descricao;

    public String getDescricao() {
        return descricao;
    }

    public boolean canTransitionTo(ServiceOrderStatus novoStatus, RoleEnum role) {
        if (role == RoleEnum.ADMIN) {
            return true;
        }

        return switch (this) {
            case RECEBIDA -> (novoStatus == EM_DIAGNOSTICO || novoStatus == AGUARDANDO_ESTOQUE)
                    && role == RoleEnum.COLLABORATOR;

            case EM_DIAGNOSTICO -> (novoStatus == AGUARDANDO_APROVACAO || novoStatus == AGUARDANDO_ESTOQUE)
                    && role == RoleEnum.COLLABORATOR;

            case AGUARDANDO_APROVACAO -> (novoStatus == APROVADO_CLIENTE || novoStatus == REPROVADO_CLIENTE)
                    && role == RoleEnum.CLIENT;

            case APROVADO_CLIENTE -> (novoStatus == EM_EXECUCAO || novoStatus == AGUARDANDO_ESTOQUE)
                    && role == RoleEnum.COLLABORATOR;

            case REPROVADO_CLIENTE -> (novoStatus == EM_EXECUCAO || novoStatus == AGUARDANDO_ESTOQUE || novoStatus == FINALIZADA)
                    && role == RoleEnum.COLLABORATOR;

            case EM_EXECUCAO -> (novoStatus == FINALIZADA || novoStatus == AGUARDANDO_ESTOQUE)
                    && role == RoleEnum.COLLABORATOR;

            case FINALIZADA -> (novoStatus == ENTREGUE)
                    && role == RoleEnum.COLLABORATOR;

            case AGUARDANDO_ESTOQUE -> role == RoleEnum.COLLABORATOR;

            default -> false;
        };
    }

    public String getAllowedTransitions(RoleEnum role) {
        if (role == RoleEnum.ADMIN) {
            return "Qualquer status (ADMIN possui permissão total).";
        }

        return switch (this) {
            case RECEBIDA -> "Pode alterar para: EM_DIAGNOSTICO ou AGUARDANDO_ESTOQUE (COLLABORATOR).";
            case EM_DIAGNOSTICO -> "Pode alterar para: AGUARDANDO_APROVACAO ou AGUARDANDO_ESTOQUE (COLLABORATOR).";
            case AGUARDANDO_APROVACAO -> "Pode alterar para: APROVADO_CLIENTE ou REPROVADO_CLIENTE (CLIENT).";
            case APROVADO_CLIENTE -> "Pode alterar para: EM_EXECUCAO ou AGUARDANDO_ESTOQUE (COLLABORATOR).";
            case REPROVADO_CLIENTE -> "Pode alterar para: EM_EXECUCAO, AGUARDANDO_ESTOQUE ou FINALIZADA (COLLABORATOR).";
            case EM_EXECUCAO -> "Pode alterar para: FINALIZADA ou AGUARDANDO_ESTOQUE (COLLABORATOR).";
            case FINALIZADA -> "Pode alterar para: ENTREGUE (COLLABORATOR).";
            case AGUARDANDO_ESTOQUE -> "Pode alterar para qualquer status conforme necessidade (COLLABORATOR).";
            default -> "Nenhuma transição permitida a partir deste status.";
        };
    }

    public static void validatePermissionForStatus(
            ServiceOrder order,
            ServiceOrderStatus novoStatus,
            RoleEnum role
    ) {
        if (!order.getOrderStatus().canTransitionTo(novoStatus, role)) {
            throw new BusinessException(
                    String.format(
                            "Transição inválida: o usuário com perfil '%s' não pode alterar o status de '%s' para '%s'. %n%s",
                            role,
                            order.getOrderStatus().name(),
                            novoStatus.name(),
                            order.getOrderStatus().getAllowedTransitions(role)
                    )
            );
        }
    }
}
