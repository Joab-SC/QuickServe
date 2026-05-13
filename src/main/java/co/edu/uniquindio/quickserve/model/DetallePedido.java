package co.edu.uniquindio.quickserve.model;

public class DetallePedido {

    private Integer id;
    private Pedido pedido;
    private Producto producto;
    private Integer cantidad;

    public DetallePedido() {}

    public DetallePedido(Integer id, Pedido pedido, Producto producto, Integer cantidad) {
        this.id = id;
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Double getSubtotal() {
        if (producto == null || cantidad == null) return 0.0;
        return producto.getPrecio() * cantidad;
    }

    // getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}