package co.edu.uniquindio.quickserve.service;

import co.edu.uniquindio.quickserve.model.Producto;
import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import co.edu.uniquindio.quickserve.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public List<Producto> listarDisponibles() {
        return productoRepository.findAll().stream()
                .filter(Producto::getDisponible)
                .toList();
    }

    public List<Producto> listarPorTipo(TipoProducto tipo) {
        return productoRepository.findByTipo(tipo);
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public Producto crear(String nombre, String descripcion, Double precio,
                          TipoProducto tipo, Boolean disponible) {
        Producto p = new Producto(null, nombre, descripcion, precio,
                disponible != null && disponible, tipo);
        return productoRepository.save(p);
    }

    public Optional<Producto> actualizar(Integer id, String nombre, String descripcion,
                                         Double precio, TipoProducto tipo, Boolean disponible) {
        return productoRepository.findById(id).map(p -> {
            productoRepository.deleteById(id);
            Producto actualizado = new Producto(id, nombre, descripcion, precio,
                    disponible != null && disponible, tipo);
            return productoRepository.save(actualizado);
        });
    }

    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    public TipoProducto[] getTipos() {
        return TipoProducto.values();
    }
}