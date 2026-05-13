package co.edu.uniquindio.quickserve.service;

import co.edu.uniquindio.quickserve.model.*;
import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final MeseroRepository meseroRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         MesaRepository mesaRepository,
                         MeseroRepository meseroRepository,
                         ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.meseroRepository = meseroRepository;
        this.productoRepository = productoRepository;
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll().stream()
                .sorted(Comparator.comparing(Pedido::getHoraPedido).reversed())
                .toList();
    }

    public List<Pedido> listarActivos() {
        return pedidoRepository.findActivos().stream()
                .sorted(Comparator.comparing(Pedido::getHoraPedido))
                .toList();
    }

    public List<Pedido> listarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findAll().stream()
                .filter(p -> p.getEstado() == estado)
                .sorted(Comparator.comparing(Pedido::getHoraPedido))
                .toList();
    }

    public List<Pedido> listarParaCocina() {
        return pedidoRepository.findAll().stream()
                .filter(p -> p.getEstado() == EstadoPedido.CREADO
                        || p.getEstado() == EstadoPedido.PREPARACION)
                .sorted(Comparator.comparing(Pedido::getHoraPedido))
                .toList();
    }

    public List<Pedido> listarListosParaMesero() {
        return pedidoRepository.findAll().stream()
                .filter(p -> p.getEstado() == EstadoPedido.LISTO)
                .sorted(Comparator.comparing(Pedido::getHoraPedido))
                .toList();
    }

    public long contarNotificaciones() {
        return pedidoRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getNotificacionPendiente()))
                .count();
    }

    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidoRepository.findById(id);
    }

    public List<Mesa> getMesasDisponibles() {
        return mesaRepository.findDisponibles();
    }

    public List<Mesa> getTodasLasMesas() {
        return mesaRepository.findAll();
    }

    public List<Mesero> getMeseros() {
        return meseroRepository.findAll();
    }

    public EstadoPedido[] getEstados() {
        return EstadoPedido.values();
    }

    // ── Registrar Pedido ──────────────────────────────────────────────────────

    public Pedido registrarPedido(Integer mesaNumero, String meseroCedula,
                                  List<Integer> productoIds, List<Integer> cantidades,
                                  String observaciones) {
        if (productoIds == null || productoIds.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto.");
        }

        boolean alguienValido = false;
        for (int i = 0; i < productoIds.size(); i++) {
            if (cantidades != null && i < cantidades.size() && cantidades.get(i) > 0) {
                alguienValido = true;
                break;
            }
        }
        if (!alguienValido) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto con cantidad mayor a 0.");
        }

        Mesa mesa = mesaRepository.findByNumero(mesaNumero).orElseThrow();
        Mesero mesero = meseroRepository.findByCedula(meseroCedula).orElseThrow();

        Pedido p = new Pedido();
        p.setMesa(mesa);
        p.setMesero(mesero);
        p.setEstado(EstadoPedido.CREADO);
        p.setHoraPedido(LocalDateTime.now());
        p.setObservaciones(observaciones != null ? observaciones : "");
        p.setNotificacionPendiente(false);

        List<DetallePedido> detalles = new ArrayList<>();
        for (int i = 0; i < productoIds.size(); i++) {
            Integer cant = cantidades.get(i);
            if (cant != null && cant > 0) {
                Producto prod = productoRepository.findById(productoIds.get(i)).orElseThrow();
                detalles.add(new DetallePedido(null, p, prod, cant));
            }
        }
        p.setDetalles(detalles);

        mesa.setOcupada(true);
        return pedidoRepository.save(p);
    }

    // ── Cambios de estado ─────────────────────────────────────────────────────

    public Optional<Pedido> marcarEnPreparacion(Integer pedidoId) {
        return actualizarEstado(pedidoId, EstadoPedido.PREPARACION);
    }

    public Optional<Pedido> marcarListo(Integer pedidoId) {
        return actualizarEstado(pedidoId, EstadoPedido.LISTO);
    }

    public Optional<Pedido> marcarEntregado(Integer pedidoId) {
        return actualizarEstado(pedidoId, EstadoPedido.ENTREGADO);
    }

    public Optional<Pedido> actualizarEstado(Integer pedidoId, EstadoPedido nuevoEstado) {
        return pedidoRepository.findById(pedidoId).map(p -> {
            p.setEstado(nuevoEstado);
            if (nuevoEstado == EstadoPedido.LISTO) {
                p.setNotificacionPendiente(true);
            }
            if (nuevoEstado == EstadoPedido.ENTREGADO) {
                p.setHoraEntrega(LocalDateTime.now());
                p.setNotificacionPendiente(false);
                if (p.getMesa() != null) p.getMesa().setOcupada(false);
            }
            return pedidoRepository.save(p);
        });
    }

    public void limpiarNotificacion(Integer pedidoId) {
        pedidoRepository.findById(pedidoId).ifPresent(p -> {
            p.setNotificacionPendiente(false);
            pedidoRepository.save(p);
        });
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public Optional<Pedido> actualizar(Integer pedidoId, Integer mesaNumero, String meseroCedula,
                                       List<Integer> productoIds, List<Integer> cantidades,
                                       String observaciones) {
        return pedidoRepository.findById(pedidoId).map(p -> {
            mesaRepository.findByNumero(mesaNumero).ifPresent(m -> {
                if (p.getMesa() != null) p.getMesa().setOcupada(false);
                p.setMesa(m);
                m.setOcupada(true);
            });
            meseroRepository.findByCedula(meseroCedula).ifPresent(p::setMesero);
            p.setObservaciones(observaciones != null ? observaciones : "");

            List<DetallePedido> detalles = new ArrayList<>();
            for (int i = 0; i < productoIds.size(); i++) {
                Integer cant = cantidades.get(i);
                if (cant != null && cant > 0) {
                    Producto prod = productoRepository.findById(productoIds.get(i)).orElseThrow();
                    detalles.add(new DetallePedido(null, p, prod, cant));
                }
            }
            p.setDetalles(detalles);
            return pedidoRepository.save(p);
        });
    }

    public void eliminar(Integer pedidoId) {
        pedidoRepository.findById(pedidoId).ifPresent(p -> {
            if (p.getMesa() != null) p.getMesa().setOcupada(false);
            pedidoRepository.deleteById(pedidoId);
        });
    }
}