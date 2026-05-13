package co.edu.uniquindio.quickserve.repository;

import co.edu.uniquindio.quickserve.model.Mesa;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class MesaRepository {

    private final List<Mesa> mesas = new ArrayList<>();

    public List<Mesa> findAll() {
        return Collections.unmodifiableList(mesas);
    }

    public Optional<Mesa> findByNumero(Integer numero) {
        return mesas.stream()
                .filter(m -> m.getNumero().equals(numero))
                .findFirst();
    }

    public Mesa save(Mesa m) {
        mesas.add(m);
        return m;
    }

    public List<Mesa> findDisponibles() {
        return mesas.stream().filter(m -> !m.getOcupada()).toList();
    }
}