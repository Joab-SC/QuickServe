package co.edu.uniquindio.quickserve.dataInitializer;

import co.edu.uniquindio.quickserve.model.*;
import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import co.edu.uniquindio.quickserve.repository.DataStore;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer {

    private final DataStore dataStore;

    public DataInitializer(DataStore dataStore) {
        this.dataStore = dataStore;

        inicializarMeseros();
        inicializarMesas();
        inicializarProductos();
        inicializarPedidos();
    }

    // ─────────────────────────────── MESEROS ───────────────────────────────
    private void inicializarMeseros() {
        dataStore.getAllMeseros().addAll(List.of(
                new Mesero("1001", "Carlos Ramírez", "carlos", "mesa123"),
                new Mesero("1002", "Laura Gómez", "laura", "mesa123"),
                new Mesero("1003", "Andrés Patiño", "andres", "mesa123")
        ));
    }

    // ─────────────────────────────── MESAS ───────────────────────────────
    private void inicializarMesas() {
        for (int i = 1; i <= 10; i++) {
            dataStore.getAllMesas().add(new Mesa(i, false));
        }
    }

    // ─────────────────────────────── PRODUCTOS ───────────────────────────────
    private void inicializarProductos() {
        dataStore.getAllProductos().addAll(List.of(
                new Producto(1, "Arepa de Chócolo", "Arepa dulce con queso campesino", 8000.0, true, TipoProducto.ENTRADA),
                new Producto(2, "Empanadas x3", "Empanadas de pipián con ají", 9500.0, true, TipoProducto.ENTRADA),
                new Producto(3, "Patacones con hogao", "Patacones fritos con salsa", 7000.0, true, TipoProducto.ENTRADA),

                new Producto(4, "Bandeja Paisa", "Frijoles, carne, arroz, huevo", 32000.0, true, TipoProducto.PLATOFUERTE),
                new Producto(5, "Trucha al Ajillo", "Trucha con papas", 28000.0, true, TipoProducto.PLATOFUERTE),
                new Producto(6, "Pollo a la Plancha", "Pechuga con arroz", 22000.0, true, TipoProducto.PLATOFUERTE),

                new Producto(7, "Cazuela de Mariscos", "Mariscos en crema", 38000.0, true, TipoProducto.PLATOFUERTE),
                new Producto(8, "Costilla BBQ", "Costilla a la parrilla", 35000.0, false, TipoProducto.PLATOFUERTE),

                new Producto(9, "Tres Leches", "Postre clásico", 9000.0, true, TipoProducto.POSTRE),
                new Producto(10, "Brownie", "Brownie con helado", 11000.0, true, TipoProducto.POSTRE),

                new Producto(11, "Flan", "Flan de caramelo", 8500.0, true, TipoProducto.POSTRE),

                new Producto(12, "Limonada de Coco", "Bebida tropical", 8000.0, true, TipoProducto.BEBIDA),
                new Producto(13, "Jugo de Lulo", "Jugo natural", 6000.0, true, TipoProducto.BEBIDA),
                new Producto(14, "Cerveza Artesanal", "330ml", 10000.0, true, TipoProducto.BEBIDA),
                new Producto(15, "Agua Mineral", "500ml", 3000.0, true, TipoProducto.BEBIDA)
        ));
    }

    // ─────────────────────────────── PEDIDOS ───────────────────────────────
    private void inicializarPedidos() {
        LocalDateTime base = LocalDateTime.now().minusHours(3);

        dataStore.crearPedido(1, "1001",
                List.of(4, 12),
                List.of(2, 2),
                "Sin picante");

        dataStore.crearPedido(5, "1002",
                List.of(5, 9, 13),
                List.of(1, 2, 2),
                "");

        dataStore.crearPedido(7, "1003",
                List.of(7, 11, 15),
                List.of(2, 2, 4),
                "Mesa VIP");
    }
}