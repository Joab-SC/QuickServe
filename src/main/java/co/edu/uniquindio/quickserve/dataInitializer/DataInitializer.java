package co.edu.uniquindio.quickserve.dataInitializer;

import co.edu.uniquindio.quickserve.model.*;
import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import co.edu.uniquindio.quickserve.repository.*;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer {

    private final ProductoRepository productoRepository;
    private final MesaRepository mesaRepository;
    private final MeseroRepository meseroRepository;
    private final PedidoRepository pedidoRepository;

    public DataInitializer(
            ProductoRepository productoRepository,
            MesaRepository mesaRepository,
            MeseroRepository meseroRepository,
            PedidoRepository pedidoRepository
    ) {
        this.productoRepository = productoRepository;
        this.mesaRepository = mesaRepository;
        this.meseroRepository = meseroRepository;
        this.pedidoRepository = pedidoRepository;

        inicializarMeseros();
        inicializarMesas();
        inicializarProductos();
        inicializarPedidos();
    }

    // ───────────────────── MESEROS ─────────────────────
    private void inicializarMeseros() {
        meseroRepository.save(new Mesero("1001", "Carlos Ramírez", "carlos", "mesa123"));
        meseroRepository.save(new Mesero("1002", "Laura Gómez", "laura", "mesa123"));
        meseroRepository.save(new Mesero("1003", "Andrés Patiño", "andres", "mesa123"));
    }

    // ───────────────────── MESAS ─────────────────────
    private void inicializarMesas() {
        for (int i = 1; i <= 10; i++) {
            mesaRepository.save(new Mesa(i, false));
        }
    }

    // ───────────────────── PRODUCTOS ─────────────────────
    private void inicializarProductos() {
        productoRepository.save(new Producto(1, "Arepa de Chócolo", "Arepa dulce", 8000.0, true, TipoProducto.ENTRADA));
        productoRepository.save(new Producto(2, "Empanadas x3", "Empanadas", 9500.0, true, TipoProducto.ENTRADA));
        productoRepository.save(new Producto(4, "Bandeja Paisa", "Completo", 32000.0, true, TipoProducto.PLATOFUERTE));
        productoRepository.save(new Producto(5, "Trucha", "Trucha al ajillo", 28000.0, true, TipoProducto.PLATOFUERTE));
        productoRepository.save(new Producto(9, "Tres Leches", "Postre", 9000.0, true, TipoProducto.POSTRE));
        productoRepository.save(new Producto(12, "Limonada de Coco", "Bebida", 8000.0, true, TipoProducto.BEBIDA));
    }

    // ───────────────────── PEDIDOS ─────────────────────
    private void inicializarPedidos() {

        Pedido p1 = crearPedido(
                mesaRepository.findByNumero(1).orElseThrow(),
                meseroRepository.findByCedula("1001").orElseThrow(),
                "Sin picante",
                List.of(
                        new Object[]{4, 2},
                        new Object[]{12, 2}
                )
        );

        Pedido p2 = crearPedido(
                mesaRepository.findByNumero(5).orElseThrow(),
                meseroRepository.findByCedula("1002").orElseThrow(),
                "",
                List.of(
                        new Object[]{5, 1},
                        new Object[]{9, 2}
                )
        );

        Pedido p3 = crearPedido(
                mesaRepository.findByNumero(7).orElseThrow(),
                meseroRepository.findByCedula("1003").orElseThrow(),
                "Mesa VIP",
                List.of(
                        new Object[]{4, 1},
                        new Object[]{9, 1},
                        new Object[]{12, 2}
                )
        );

        pedidoRepository.save(p1);
        pedidoRepository.save(p2);
        pedidoRepository.save(p3);
    }

    // ───────────────────── HELPER ─────────────────────
    private Pedido crearPedido(Mesa mesa, Mesero mesero, String obs, List<Object[]> items) {

        Pedido p = new Pedido();
        p.setMesa(mesa);
        p.setMesero(mesero);
        p.setObservaciones(obs);
        p.setEstado(EstadoPedido.CREADO);

        List<DetallePedido> detalles = new java.util.ArrayList<>();

        for (Object[] item : items) {
            Integer prodId = (Integer) item[0];
            Integer cant = (Integer) item[1];

            Producto prod = productoRepository.findById(prodId).orElseThrow();

            detalles.add(new DetallePedido(null, p, prod, cant));
        }

        p.setDetalles(detalles);

        return p;
    }
}