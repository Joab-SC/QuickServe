package co.edu.uniquindio.quickserve.repository;

import co.edu.uniquindio.quickserve.model.Mesa;
import org.springframework.stereotype.Repository;

import java.util.*;

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
        // Solo agregar si no existe ya en la lista
        boolean existe = mesas.stream()
                .anyMatch(mesa -> mesa.getNumero().equals(m.getNumero()));
        if (!existe) {
            mesas.add(m);
        }
        // Si ya existe, el objeto ya fue mutado in-place (setOcupada), no hace falta nada más
        return m;
    }

    public List<Mesa> findDisponibles() {
        return mesas.stream().filter(m -> !m.getOcupada()).toList();
    }

    public List<Mesa> findOcupadas() {
        return mesas.stream()
                .filter(Mesa::getOcupada)
                .sorted(Comparator.comparing(Mesa::getNumero))
                .toList();
    }
}
