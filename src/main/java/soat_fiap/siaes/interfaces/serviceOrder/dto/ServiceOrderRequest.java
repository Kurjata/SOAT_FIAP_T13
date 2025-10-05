package soat_fiap.siaes.interfaces.serviceOrder.dto;

import soat_fiap.siaes.interfaces.serviceOrderItem.dto.ServiceOrderItemRequest;

import java.util.List;

public record ServiceOrderRequest(
        String vehiclePlate,   // placa do veículo
        String userDocument,   // CPF ou CNPJ do usuário
        List<ServiceOrderItemRequest> items //Itens do orçamento
) {
}
