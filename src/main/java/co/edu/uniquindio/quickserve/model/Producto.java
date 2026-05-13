package co.edu.uniquindio.quickserve.model;


import co.edu.uniquindio.quickserve.model.enums.TipoProducto;

public class Producto {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Boolean disponible;
    private TipoProducto tipo;

    public Producto() {}

    public Producto(Integer id, String nombre, String descripcion,
                    Double precio, Boolean disponible, TipoProducto tipo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible;
        this.tipo = tipo;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public TipoProducto getTipo() { return tipo; }
    public void setTipo(TipoProducto tipo) { this.tipo = tipo; }

    public String getPrecioFormateado() {
        if (precio == null) return "$0";
        return String.format("$%,.0f", precio);
    }
}
