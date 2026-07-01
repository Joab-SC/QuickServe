package co.edu.uniquindio.quickserve.model;


import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Boolean disponible;
    private TipoProducto tipo;

    public String getPrecioFormateado() {
        if (precio == null) return "$0";
        return String.format("$%,.0f", precio);
    }
}
