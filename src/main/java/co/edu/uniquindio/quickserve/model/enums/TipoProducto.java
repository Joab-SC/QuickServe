package co.edu.uniquindio.quickserve.model.enums;

public enum TipoProducto {
    ENTRADA("Entrada"),
    PLATOFUERTE("Plato Fuerte"),
    POSTRE("Postre"),
    BEBIDA("Bebida");

    private final String label;

    TipoProducto(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
