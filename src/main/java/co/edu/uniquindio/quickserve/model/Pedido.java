package co.edu.uniquindio.quickserve.model;


import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private Integer id;
    private Mesa mesa;
    private Mesero mesero;
    private EstadoPedido estado;
    private LocalDateTime horaPedido;
    private LocalDateTime horaEntrega;
    private String observaciones;
    private List<DetallePedido> detalles;
    // Notificación para el mesero: true si cocina marcó como LISTO y el mesero aún no la ha visto
    private Boolean notificacionPendiente;

    public Pedido() {
        this.detalles = new ArrayList<>();
        this.notificacionPendiente = false;
    }

    public Double getTotal() {
        if (detalles == null) return 0.0;
        return detalles.stream().mapToDouble(DetallePedido::getSubtotal).sum();
    }

    public String getTotalFormateado() {
        return String.format("$%,.0f", getTotal());
    }

    public String getHoraPedidoFormateada() {
        if (horaPedido == null) return "—";
        return horaPedido.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
    }

    public String getHoraEntregaFormateada() {
        if (horaEntrega == null) return "—";
        return horaEntrega.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getEstadoCss() {
        if (estado == null) return "";
        return switch (estado) {
            case CREADO      -> "estado-creado";
            case PREPARACION -> "estado-preparacion";
            case LISTO       -> "estado-listo";
            case ENTREGADO   -> "estado-entregado";
        };
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

    public Mesero getMesero() { return mesero; }
    public void setMesero(Mesero mesero) { this.mesero = mesero; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public LocalDateTime getHoraPedido() { return horaPedido; }
    public void setHoraPedido(LocalDateTime horaPedido) { this.horaPedido = horaPedido; }

    public LocalDateTime getHoraEntrega() { return horaEntrega; }
    public void setHoraEntrega(LocalDateTime horaEntrega) { this.horaEntrega = horaEntrega; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    public Boolean getNotificacionPendiente() { return notificacionPendiente; }
    public void setNotificacionPendiente(Boolean notificacionPendiente) { this.notificacionPendiente = notificacionPendiente; }
}
