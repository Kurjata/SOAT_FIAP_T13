package soat_fiap.siaes.domain.serviceOrder.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import soat_fiap.siaes.domain.user.model.RoleEnum;

@Getter
@RequiredArgsConstructor
public enum ServiceOrderStatusEnum {
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

    /**
     * Valida se a ordem de serviço pode transitar do status atual (this)
     * para um novo status (novoStatus) com base no papel do usuário (role).
     *
     * Fluxo do processo:
     * 1. RECEBIDA -> EM_DIAGNOSTICO ou AGUARDANDO_ESTOQUE (somente COLLABORATOR)
     * 2. EM_DIAGNOSTICO -> AGUARDANDO_APROVACAO ou AGUARDANDO_ESTOQUE (somente COLLABORATOR)
     * 3. AGUARDANDO_APROVACAO -> APROVADO_CLIENTE ou REPROVADO_CLIENTE (somente CLIENT)
     * 4. APROVADO_CLIENTE -> EM_EXECUCAO ou AGUARDANDO_ESTOQUE (somente COLLABORATOR)
     * 5. REPROVADO_CLIENTE -> EM_EXECUCAO ou AGUARDANDO_ESTOQUE ou FINALIZADA (somente COLLABORATOR)
     *      - Permite finalizar a OS mesmo após reprovação do cliente
     * 6. EM_EXECUCAO -> FINALIZADA ou AGUARDANDO_ESTOQUE (COLLABORATOR ou ADMIN)
     * 7. FINALIZADA -> ENTREGUE (COLLABORATOR ou ADMIN)
     * 8. AGUARDANDO_ESTOQUE -> pode ser alcançado de várias etapas automaticamente (COLLABORATOR)
     */
    public boolean canTransitionTo(ServiceOrderStatusEnum novoStatus, RoleEnum role) {
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
                    && (role == RoleEnum.COLLABORATOR || role == RoleEnum.ADMIN);

            case FINALIZADA -> (novoStatus == ENTREGUE)
                    && (role == RoleEnum.COLLABORATOR || role == RoleEnum.ADMIN);

            case AGUARDANDO_ESTOQUE -> (role == RoleEnum.COLLABORATOR);

            default -> false;
        };
    }
}
