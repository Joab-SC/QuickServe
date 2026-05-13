package co.edu.uniquindio.quickserve.dto;


import co.edu.uniquindio.quickserve.model.enums.TipoProducto;

public class ProductoDTO {
    private String nombre;
    private String descripcion;
    private Double precio;
    private TipoProducto tipo;
    private Boolean disponible;

    public ProductoDTO() {
        this.disponible = true;
    }

    // Getters & Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public TipoProducto getTipo() { return tipo; }
    public void setTipo(TipoProducto tipo) { this.tipo = tipo; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
}
