package co.edu.uniquindio.quickserve.dto;

import java.util.List;

public class PedidoDTO {
    private Integer mesaNumero;
    private String meseroCedula;
    private String observaciones;
    private List<Integer> productoIds;
    private List<Integer> cantidades;

    public PedidoDTO() {}

    // Getters & Setters
    public Integer getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(Integer mesaNumero) { this.mesaNumero = mesaNumero; }

    public String getMeseroCedula() { return meseroCedula; }
    public void setMeseroCedula(String meseroCedula) { this.meseroCedula = meseroCedula; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public List<Integer> getProductoIds() { return productoIds; }
    public void setProductoIds(List<Integer> productoIds) { this.productoIds = productoIds; }

    public List<Integer> getCantidades() { return cantidades; }
    public void setCantidades(List<Integer> cantidades) { this.cantidades = cantidades; }
}
