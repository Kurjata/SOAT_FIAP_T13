package soat_fiap.siaes.domain.partStock.event;


import soat_fiap.siaes.domain.partStock.model.PartStock;

public class StockBelowMinimumEvent {

    private final PartStock partStock;

    public StockBelowMinimumEvent(PartStock partStock) {
        this.partStock = partStock;
    }

    public PartStock getPartStock() {
        return partStock;
    }

    public String getMessage() {
        return "Estoque abaixo do mínimo para peça: "
                + partStock.getName()
                + " (EAN: " + partStock.getEan() + ")";
    }
}
