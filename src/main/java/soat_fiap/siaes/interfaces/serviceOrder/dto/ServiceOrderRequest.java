package soat_fiap.siaes.interfaces.serviceOrder.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.CreateOrderActivityRequest;
import soat_fiap.siaes.shared.validation.Document;
import soat_fiap.siaes.shared.validation.BrazilianLicensePlate;

import java.util.List;

public record ServiceOrderRequest(
        @BrazilianLicensePlate
        String vehiclePlate,
        @Document
        String userDocument,
        @Valid
        @NotNull
        List<CreateOrderActivityRequest> orderActivities
) {
}
