package co.edu.uniquindio.quickserve.repository;

import co.edu.uniquindio.quickserve.model.Producto;
import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class ProductoRepository {

    private final List<Producto> productos = new ArrayList<>();
    private final AtomicInteger idSeq = new AtomicInteger(16);

    public List<Producto> findAll() {
        return Collections.unmodifiableList(productos);
    }

    public Optional<Producto> findById(Integer id) {
        return productos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Producto save(Producto p) {
        if (p.getId() == null) {
            p.setId(idSeq.getAndIncrement());
        }
        productos.add(p);
        return p;
    }

    public void deleteById(Integer id) {
        productos.removeIf(p -> p.getId().equals(id));
    }

    public List<Producto> findByTipo(TipoProducto tipo) {
        return productos.stream()
                .filter(p -> p.getTipo() == tipo && p.getDisponible())
                .toList();
    }
}