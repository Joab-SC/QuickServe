package co.edu.uniquindio.quickserve.model;

public class Mesa {
    private Integer numero;
    private Boolean ocupada;

    public Mesa() {}

    public Mesa(Integer numero, Boolean ocupada) {
        this.numero = numero;
        this.ocupada = ocupada;
    }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public Boolean getOcupada() { return ocupada; }
    public void setOcupada(Boolean ocupada) { this.ocupada = ocupada; }
}
