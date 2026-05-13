package co.edu.uniquindio.quickserve.repository;

import co.edu.uniquindio.quickserve.model.*;
import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria. Actúa como única fuente de verdad.
 * No usa base de datos — los datos nuevos se pierden al reiniciar.
 */
@Repository
public class DataStore {

    private final List<Producto>  productos  = new ArrayList<>();
    private final List<Pedido>    pedidos    = new ArrayList<>();
    private final List<Mesa>      mesas      = new ArrayList<>();
    private final List<Mesero>    meseros    = new ArrayList<>();

    private final AtomicInteger productoIdSeq  = new AtomicInteger(16);
    private final AtomicInteger pedidoIdSeq    = new AtomicInteger(13);
    private final AtomicInteger detalleIdSeq   = new AtomicInteger(40);


    // ── Productos ──────────────────────────────────────────────────────────────

    public List<Producto> getAllProductos() { return Collections.unmodifiableList(productos); }

    public List<Producto> getProductosDisponibles() {
        return productos.stream().filter(Producto::getDisponible).collect(Collectors.toList());
    }

    public List<Producto> getProductosPorTipo(TipoProducto tipo) {
        return productos.stream()
            .filter(p -> p.getTipo() == tipo && p.getDisponible())
            .collect(Collectors.toList());
    }

    public Optional<Producto> getProductoPorId(Integer id) {
        return productos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Producto crearProducto(String nombre, String descripcion, Double precio,
                                   TipoProducto tipo, Boolean disponible) {
        Producto p = new Producto(productoIdSeq.getAndIncrement(), nombre, descripcion,
                                   precio, disponible, tipo);
        productos.add(p);
        return p;
    }

    public Optional<Producto> actualizarProducto(Integer id, String nombre, String descripcion,
                                                   Double precio, TipoProducto tipo, Boolean disponible) {
        return getProductoPorId(id).map(p -> {
            p.setNombre(nombre);
            p.setDescripcion(descripcion);
            p.setPrecio(precio);
            p.setTipo(tipo);
            p.setDisponible(disponible);
            return p;
        });
    }

    public void eliminarProducto(Integer id) {
        productos.removeIf(p -> p.getId().equals(id));
    }

    // ── Mesas ──────────────────────────────────────────────────────────────────

    public List<Mesa> getAllMesas() { return Collections.unmodifiableList(mesas); }

    public List<Mesa> getMesasDisponibles() {
        return mesas.stream().filter(m -> !m.getOcupada()).collect(Collectors.toList());
    }

    public Optional<Mesa> getMesaPorNumero(Integer numero) {
        return mesas.stream().filter(m -> m.getNumero().equals(numero)).findFirst();
    }

    // ── Meseros ────────────────────────────────────────────────────────────────

    public List<Mesero> getAllMeseros() { return Collections.unmodifiableList(meseros); }

    public Optional<Mesero> getMeseroPorCedula(String cedula) {
        return meseros.stream().filter(m -> m.getCedula().equals(cedula)).findFirst();
    }

    // ── Pedidos ────────────────────────────────────────────────────────────────

    public List<Pedido> getAllPedidos() { return Collections.unmodifiableList(pedidos); }

    public List<Pedido> getPedidosActivos() {
        return pedidos.stream()
            .filter(p -> p.getEstado() != EstadoPedido.ENTREGADO)
            .sorted(Comparator.comparing(Pedido::getHoraPedido))
            .collect(Collectors.toList());
    }

    public List<Pedido> getPedidosPorEstado(EstadoPedido estado) {
        return pedidos.stream()
            .filter(p -> p.getEstado() == estado)
            .sorted(Comparator.comparing(Pedido::getHoraPedido))
            .collect(Collectors.toList());
    }

    /** Pedidos que la cocina debe ver: CREADO y PREPARACION */
    public List<Pedido> getPedidosParaCocina() {
        return pedidos.stream()
            .filter(p -> p.getEstado() == EstadoPedido.CREADO
                      || p.getEstado() == EstadoPedido.PREPARACION)
            .sorted(Comparator.comparing(Pedido::getHoraPedido))
            .collect(Collectors.toList());
    }

    /** Pedidos LISTOS que el mesero debe entregar */
    public List<Pedido> getPedidosListosParaMesero() {
        return pedidos.stream()
            .filter(p -> p.getEstado() == EstadoPedido.LISTO)
            .sorted(Comparator.comparing(Pedido::getHoraPedido))
            .collect(Collectors.toList());
    }

    public long contarNotificacionesPendientes() {
        return pedidos.stream()
            .filter(p -> Boolean.TRUE.equals(p.getNotificacionPendiente()))
            .count();
    }

    public Optional<Pedido> getPedidoPorId(Integer id) {
        return pedidos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Pedido crearPedido(Integer mesaNumero, String meseroCedula,
                               List<Integer> productoIds, List<Integer> cantidades,
                               String observaciones) {
        Mesa mesa = getMesaPorNumero(mesaNumero).orElseThrow();
        Mesero mesero = getMeseroPorCedula(meseroCedula).orElseThrow();

        Pedido p = new Pedido();
        p.setId(pedidoIdSeq.getAndIncrement());
        p.setMesa(mesa);
        p.setMesero(mesero);
        p.setEstado(EstadoPedido.CREADO);
        p.setHoraPedido(LocalDateTime.now());
        p.setObservaciones(observaciones != null ? observaciones : "");
        p.setNotificacionPendiente(false);

        List<DetallePedido> detalles = new ArrayList<>();
        for (int i = 0; i < productoIds.size(); i++) {
            Integer prodId = productoIds.get(i);
            Integer cant   = cantidades.get(i);
            if (cant != null && cant > 0) {
                Producto prod = getProductoPorId(prodId).orElseThrow();
                detalles.add(new DetallePedido(detalleIdSeq.getAndIncrement(), p.getId(), prod, cant));
            }
        }
        p.setDetalles(detalles);
        pedidos.add(p);
        mesa.setOcupada(true);
        return p;
    }

    public Optional<Pedido> actualizarEstadoPedido(Integer pedidoId, EstadoPedido nuevoEstado) {
        return getPedidoPorId(pedidoId).map(p -> {
            p.setEstado(nuevoEstado);
            if (nuevoEstado == EstadoPedido.LISTO) {
                // Cocina notifica al mesero
                p.setNotificacionPendiente(true);
            }
            if (nuevoEstado == EstadoPedido.ENTREGADO) {
                p.setHoraEntrega(LocalDateTime.now());
                p.setNotificacionPendiente(false);
                // Liberar mesa
                if (p.getMesa() != null) p.getMesa().setOcupada(false);
            }
            return p;
        });
    }

    public void limpiarNotificacion(Integer pedidoId) {
        getPedidoPorId(pedidoId).ifPresent(p -> p.setNotificacionPendiente(false));
    }

    public Optional<Pedido> actualizarPedido(Integer pedidoId, Integer mesaNumero,
                                              String meseroCedula, List<Integer> productoIds,
                                              List<Integer> cantidades, String observaciones) {
        return getPedidoPorId(pedidoId).map(p -> {
            getMesaPorNumero(mesaNumero).ifPresent(m -> {
                if (p.getMesa() != null) p.getMesa().setOcupada(false);
                p.setMesa(m);
                m.setOcupada(true);
            });
            getMeseroPorCedula(meseroCedula).ifPresent(p::setMesero);
            p.setObservaciones(observaciones != null ? observaciones : "");

            List<DetallePedido> detalles = new ArrayList<>();
            for (int i = 0; i < productoIds.size(); i++) {
                Integer prodId = productoIds.get(i);
                Integer cant   = cantidades.get(i);
                if (cant != null && cant > 0) {
                    Producto prod = getProductoPorId(prodId).orElseThrow();
                    detalles.add(new DetallePedido(detalleIdSeq.getAndIncrement(), pedidoId, prod, cant));
                }
            }
            p.setDetalles(detalles);
            return p;
        });
    }

    public void eliminarPedido(Integer pedidoId) {
        getPedidoPorId(pedidoId).ifPresent(p -> {
            if (p.getMesa() != null) p.getMesa().setOcupada(false);
            pedidos.removeIf(x -> x.getId().equals(pedidoId));
        });
    }
}
