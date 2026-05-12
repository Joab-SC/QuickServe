package co.edu.uniquindio.quickserve.model.enums;

public enum EstadoPedido {
    CREADO("Creado"),
    PREPARACION("En Preparación"),
    LISTO("Listo"),
    ENTREGADO("Entregado");

    private final String label;

    EstadoPedido(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
