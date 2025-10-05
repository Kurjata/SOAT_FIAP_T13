package soat_fiap.siaes.domain.serviceOrder.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceOrderStatusEnum {
    RECEBIDA("OS criada e recebida pelo sistema"),
    EM_DIAGNOSTICO("Veículo em fase de diagnóstico"),
    AGUARDANDO_APROVACAO("Aguardando aprovação do cliente"),
    EM_EXECUCAO("Serviço em execução"),
    FINALIZADA("Serviço finalizado, pronto para retirada"),
    ENTREGUE("Veículo entregue ao cliente"),
    APROVADO_CLIENTE("Aprovado pelo cliente"),
    REPROVADO_CLIENTE("Reprovado pelo cliente");

    private final String descricao;
}