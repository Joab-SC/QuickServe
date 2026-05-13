package co.edu.uniquindio.quickserve.dataInitializer;

import co.edu.uniquindio.quickserve.model.*;
import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import co.edu.uniquindio.quickserve.repository.*;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

        // Entradas
        productoRepository.save(new Producto(
                1,
                "Arepa de Chócolo",
                "Arepa dulce con queso campesino",
                8000.0,
                true,
                TipoProducto.ENTRADA
        ));

        productoRepository.save(new Producto(
                2,
                "Empanadas x3",
                "Empanadas de pipián con ají",
                9500.0,
                true,
                TipoProducto.ENTRADA
        ));

        productoRepository.save(new Producto(
                3,
                "Patacones con hogao",
                "Patacones fritos con salsa de tomate",
                7000.0,
                true,
                TipoProducto.ENTRADA
        ));

        // Platos fuertes
        productoRepository.save(new Producto(
                4,
                "Bandeja Paisa",
                "Frijoles, chicharrón, arroz, carne, huevo",
                32000.0,
                true,
                TipoProducto.PLATOFUERTE
        ));

        productoRepository.save(new Producto(
                5,
                "Trucha al Ajillo",
                "Trucha fresca con papas y ensalada",
                28000.0,
                true,
                TipoProducto.PLATOFUERTE
        ));

        productoRepository.save(new Producto(
                6,
                "Pollo a la Plancha",
                "Pechuga con arroz y patacón",
                22000.0,
                true,
                TipoProducto.PLATOFUERTE
        ));

        productoRepository.save(new Producto(
                7,
                "Cazuela de Mariscos",
                "Camarones, almejas y calamar en crema",
                38000.0,
                true,
                TipoProducto.PLATOFUERTE
        ));

        productoRepository.save(new Producto(
                8,
                "Costilla BBQ",
                "Costilla de res a la parrilla con papas",
                35000.0,
                false,
                TipoProducto.PLATOFUERTE
        ));

        // Postres
        productoRepository.save(new Producto(
                9,
                "Tres Leches",
                "Bizcocho bañado en tres tipos de leche",
                9000.0,
                true,
                TipoProducto.POSTRE
        ));

        productoRepository.save(new Producto(
                10,
                "Brownie con Helado",
                "Brownie tibio con helado de vainilla",
                11000.0,
                true,
                TipoProducto.POSTRE
        ));

        productoRepository.save(new Producto(
                11,
                "Flan de Caramelo",
                "Flan casero con salsa de caramelo",
                8500.0,
                true,
                TipoProducto.POSTRE
        ));

        // Bebidas
        productoRepository.save(new Producto(
                12,
                "Limonada de Coco",
                "Limonada natural con leche de coco",
                8000.0,
                true,
                TipoProducto.BEBIDA
        ));

        productoRepository.save(new Producto(
                13,
                "Jugo de Lulo",
                "Jugo natural de lulo con leche o agua",
                6000.0,
                true,
                TipoProducto.BEBIDA
        ));

        productoRepository.save(new Producto(
                14,
                "Cerveza Artesanal",
                "Cerveza rubia local 330ml",
                10000.0,
                true,
                TipoProducto.BEBIDA
        ));

        productoRepository.save(new Producto(
                15,
                "Agua Mineral",
                "Agua con o sin gas 500ml",
                3000.0,
                true,
                TipoProducto.BEBIDA
        ));
    }

    // ───────────────────── PEDIDOS ─────────────────────

    private void inicializarPedidos() {

        LocalDateTime base = LocalDateTime.now().minusHours(3);


        crearPedidoSemilla(
                4,
                "1003",
                EstadoPedido.ENTREGADO,
                base.minusMinutes(70),
                base.minusMinutes(35),
                "Mesa VIP",
                false,
                7,
                new int[][]{
                        {7, 2},
                        {11, 2},
                        {15, 4}
                }
        );

        crearPedidoSemilla(
                2,
                "1002",
                EstadoPedido.ENTREGADO,
                base.minusMinutes(60),
                base.minusMinutes(20),
                "",
                false,
                3,
                new int[][]{
                        {2, 2},
                        {6, 1}
                }
        );

        crearPedidoSemilla(
                5,
                "1001",
                EstadoPedido.ENTREGADO,
                base.minusMinutes(50),
                base.minusMinutes(10),
                "Vegetariano",
                false,
                4,
                new int[][]{
                        {3, 2},
                        {11, 1},
                        {15, 2}
                }
        );

        // Pedidos activos

        crearPedidoSemilla(
                7,
                "1001",
                EstadoPedido.LISTO,
                base.minusMinutes(30),
                null,
                "Alergia al maní",
                true,
                5,
                new int[][]{
                        {1, 2},
                        {6, 3}
                }
        );

        crearPedidoSemilla(
                3,
                "1003",
                EstadoPedido.PREPARACION,
                base.minusMinutes(20),
                null,
                "",
                false,
                6,
                new int[][]{
                        {2, 1},
                        {7, 2},
                        {14, 3}
                }
        );

        crearPedidoSemilla(
                1,
                "1002",
                EstadoPedido.CREADO,
                base.minusMinutes(15),
                null,
                "Llevar cubiertos extra",
                false,
                7,
                new int[][]{
                        {3, 2},
                        {4, 1}
                }
        );

        crearPedidoSemilla(
                9,
                "1001",
                EstadoPedido.PREPARACION,
                base.minusMinutes(10),
                null,
                "",
                false,
                8,
                new int[][]{
                        {10, 2},
                        {12, 2}
                }
        );

        crearPedidoSemilla(
                6,
                "1002",
                EstadoPedido.LISTO,
                base.minusMinutes(8),
                null,
                "Sin cebolla",
                true,
                9,
                new int[][]{
                        {5, 2},
                        {13, 2}
                }
        );

        crearPedidoSemilla(
                8,
                "1001",
                EstadoPedido.CREADO,
                base.minusMinutes(5),
                null,
                "",
                false,
                10,
                new int[][]{
                        {1, 4},
                        {6, 4},
                        {14, 4}
                }
        );

        crearPedidoSemilla(
                10,
                "1003",
                EstadoPedido.PREPARACION,
                base.minusMinutes(3),
                null,
                "Cumpleaños, traer vela",
                false,
                11,
                new int[][]{
                        {4, 5},
                        {9, 5},
                        {12, 5}
                }
        );
    }

    // ───────────────────── HELPER ─────────────────────

    private void crearPedidoSemilla(
            Integer mesaNumero,
            String meseroCedula,
            EstadoPedido estado,
            LocalDateTime horaPedido,
            LocalDateTime horaEntrega,
            String observaciones,
            Boolean notificacion,
            Integer pedidoId,
            int[][] items
    ) {

        Mesa mesa = mesaRepository.findByNumero(mesaNumero).orElseThrow();

        Mesero mesero = meseroRepository
                .findByCedula(meseroCedula)
                .orElseThrow();

        Pedido pedido = new Pedido();

        pedido.setId(pedidoId);
        pedido.setMesa(mesa);
        pedido.setMesero(mesero);
        pedido.setEstado(estado);
        pedido.setHoraPedido(horaPedido);
        pedido.setHoraEntrega(horaEntrega);
        pedido.setObservaciones(observaciones);
        pedido.setNotificacionPendiente(notificacion);

        List<DetallePedido> detalles = new ArrayList<>();

        for (int[] item : items) {

            Producto producto = productoRepository
                    .findById(item[0])
                    .orElseThrow();

            detalles.add(
                    new DetallePedido(
                            null,
                            pedido,
                            producto,
                            item[1]
                    )
            );
        }

        pedido.setDetalles(detalles);

        if (estado != EstadoPedido.ENTREGADO) {
            mesa.setOcupada(true);
        }

        pedidoRepository.save(pedido);
    }
}