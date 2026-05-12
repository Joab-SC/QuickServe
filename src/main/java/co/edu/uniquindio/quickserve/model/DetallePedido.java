package co.edu.uniquindio.quickserve.model;

public class DetallePedido {
    private Integer id;
    private Integer pedidoId;
    private Producto producto;
    private Integer cantidad;

    public DetallePedido() {}

    public DetallePedido(Integer id, Integer pedidoId, Producto producto, Integer cantidad) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Double getSubtotal() {
        if (producto == null || producto.getPrecio() == null || cantidad == null) return 0.0;
        return producto.getPrecio() * cantidad;
    }

    public String getSubtotalFormateado() {
        return String.format("$%,.0f", getSubtotal());
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
