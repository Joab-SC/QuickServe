package co.edu.uniquindio.quickserve.model;

public class Mesero {
    private String cedula;
    private String nombre;
    private String usuario;
    private String contrasena;

    public Mesero() {}

    public Mesero(String cedula, String nombre, String usuario, String contrasena) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
