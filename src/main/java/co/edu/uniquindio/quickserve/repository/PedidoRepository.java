package co.edu.uniquindio.quickserve.repository;

import co.edu.uniquindio.quickserve.model.Pedido;
import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
@Repository
public class PedidoRepository {

    private final List<Pedido> pedidos = new ArrayList<>();
    private final AtomicInteger idSeq = new AtomicInteger(13);

    public List<Pedido> findAll() {
        return Collections.unmodifiableList(pedidos);
    }

    public Optional<Pedido> findById(Integer id) {
        return pedidos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Pedido save(Pedido p) {

        if (p.getId() == null) {
            p.setId(idSeq.getAndIncrement());
            pedidos.add(p);
        } else {
            deleteById(p.getId());
            pedidos.add(p);
        }

        return p;
    }

    public void deleteById(Integer id) {
        pedidos.removeIf(p -> p.getId().equals(id));
    }

    public List<Pedido> findActivos() {
        return pedidos.stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGADO)
                .toList();
    }
}