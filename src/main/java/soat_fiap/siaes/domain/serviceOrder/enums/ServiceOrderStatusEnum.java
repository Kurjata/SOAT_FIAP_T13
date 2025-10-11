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

    public boolean canTransitionTo(ServiceOrderStatusEnum novoStatus, RoleEnum role) {
        return switch (this) {
            case RECEBIDA -> (novoStatus == EM_DIAGNOSTICO || novoStatus == AGUARDANDO_ESTOQUE)
                    && role == RoleEnum.COLLABORATOR;

            case EM_DIAGNOSTICO -> (novoStatus == AGUARDANDO_APROVACAO || novoStatus == AGUARDANDO_ESTOQUE)
                    && role == RoleEnum.COLLABORATOR;

            case AGUARDANDO_APROVACAO -> (novoStatus == APROVADO_CLIENTE || novoStatus == REPROVADO_CLIENTE)
                    && role == RoleEnum.CLIENT;

            case APROVADO_CLIENTE ->
                    (novoStatus == EM_EXECUCAO || novoStatus == AGUARDANDO_ESTOQUE)
                            && role == RoleEnum.COLLABORATOR;

            case REPROVADO_CLIENTE ->
                    (novoStatus == EM_EXECUCAO || novoStatus == AGUARDANDO_ESTOQUE || novoStatus == FINALIZADA)
                            && role == RoleEnum.COLLABORATOR;


            case EM_EXECUCAO -> (novoStatus == FINALIZADA || novoStatus == AGUARDANDO_ESTOQUE)
                    && (role == RoleEnum.COLLABORATOR || role == RoleEnum.ADMIN);

            case FINALIZADA -> (novoStatus == ENTREGUE)
                    && (role == RoleEnum.COLLABORATOR || role == RoleEnum.ADMIN);

            // AGUARDANDO_ESTOQUE pode ser alcançado de várias etapas automaticamente
            case AGUARDANDO_ESTOQUE -> (role == RoleEnum.COLLABORATOR);

            default -> false;
        };
    }
}
