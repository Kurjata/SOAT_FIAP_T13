package soat_fiap.siaes.interfaces.partStock.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateSupplyAvailableRequest(@NotNull  Boolean available) {
}
