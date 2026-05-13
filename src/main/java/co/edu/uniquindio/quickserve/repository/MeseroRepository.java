package co.edu.uniquindio.quickserve.repository;

import co.edu.uniquindio.quickserve.model.Mesero;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class MeseroRepository {

    private final List<Mesero> meseros = new ArrayList<>();

    public List<Mesero> findAll() {
        return Collections.unmodifiableList(meseros);
    }

    public Optional<Mesero> findByCedula(String cedula) {
        return meseros.stream()
                .filter(m -> m.getCedula().equals(cedula))
                .findFirst();
    }

    public Mesero save(Mesero m) {
        meseros.add(m);
        return m;
    }
}