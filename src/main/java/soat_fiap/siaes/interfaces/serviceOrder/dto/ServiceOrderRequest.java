package soat_fiap.siaes.interfaces.serviceOrder.dto;

import jakarta.validation.Valid;
import soat_fiap.siaes.domain.serviceOrderItem.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ActivityItem;
import soat_fiap.siaes.interfaces.serviceOrderItem.dto.OrderActivityRequest;
import soat_fiap.siaes.interfaces.shared.Document;
import soat_fiap.siaes.interfaces.shared.validation.BrazilianLicensePlate;

import java.util.List;

public record ServiceOrderRequest(
        @BrazilianLicensePlate
        String vehiclePlate,   // placa do veículo
        @Document
        String userDocument,   // CPF ou CNPJ do usuário
        @Valid
        List<OrderActivityRequest> orderActivities //Itens do orçamento
) {
}
