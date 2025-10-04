package soat_fiap.siaes.interfaces.serviceLabor.dto;

import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;

import java.math.BigDecimal;

public record ServiceLaborResponse(
        String id,
        String description,
        BigDecimal laborCost
) {
    public ServiceLaborResponse(ServiceLabor labor) {
        this(
                labor.getId().toString(),
                labor.getDescription(),
                labor.getLaborCost()
        );
    }
}
