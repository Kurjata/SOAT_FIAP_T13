package soat_fiap.siaes.domain.inventory.model;

public enum UnitMeasure {
    UNIT("Unidade"),
    BOX("Caixa"),
    LT("Litro");

    private final String description;

    UnitMeasure(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
