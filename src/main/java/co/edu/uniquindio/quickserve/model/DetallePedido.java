package co.edu.uniquindio.quickserve.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {

    private Integer id;
    private Pedido pedido;
    private Producto producto;
    private Integer cantidad;

    public Double getSubtotal() {
        if (producto == null || cantidad == null) return 0.0;
        return producto.getPrecio() * cantidad;
    }

}