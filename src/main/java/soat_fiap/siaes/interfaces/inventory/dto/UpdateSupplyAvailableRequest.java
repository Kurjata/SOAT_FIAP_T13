package soat_fiap.siaes.interfaces.inventory.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateSupplyAvailableRequest(@NotNull  Boolean available) {
}
