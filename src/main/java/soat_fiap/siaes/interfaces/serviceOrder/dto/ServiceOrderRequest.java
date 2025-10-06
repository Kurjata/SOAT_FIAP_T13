package soat_fiap.siaes.interfaces.serviceOrder.dto;

import jakarta.validation.Valid;
import soat_fiap.siaes.interfaces.serviceOrderItem.dto.ServiceOrderItemRequest;
import soat_fiap.siaes.interfaces.shared.Document;
import soat_fiap.siaes.interfaces.shared.validation.BrazilianLicensePlate;

import java.util.List;

public record ServiceOrderRequest(
        @BrazilianLicensePlate
        String vehiclePlate,   // placa do veículo

        @Document
        String userDocument,   // CPF ou CNPJ do usuário

        @Valid
        List<ServiceOrderItemRequest> items //Itens do orçamento
) {
}
