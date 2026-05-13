package co.edu.uniquindio.quickserve.controller;


import co.edu.uniquindio.quickserve.model.Producto;
import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import co.edu.uniquindio.quickserve.service.PedidoService;
import co.edu.uniquindio.quickserve.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;

    public ProductoController(ProductoService productoService, PedidoService pedidoService) {
        this.productoService = productoService;
        this.pedidoService   = pedidoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String tipo, Model model) {
        if (tipo != null && !tipo.isBlank()) {
            try {
                model.addAttribute("productos", productoService.listarPorTipo(TipoProducto.valueOf(tipo)));
                model.addAttribute("filtroTipo", tipo);
            } catch (IllegalArgumentException e) {
                model.addAttribute("productos", productoService.listarTodos());
            }
        } else {
            model.addAttribute("productos", productoService.listarTodos());
            model.addAttribute("filtroTipo", "");
        }
        model.addAttribute("tipos",          TipoProducto.values());
        model.addAttribute("notificaciones", pedidoService.contarNotificaciones());
        return "productos/listar";
    }

    @GetMapping("/crear")
    public String formCrear(Model model) {
        model.addAttribute("accion",         "Crear");
        model.addAttribute("tipos",          TipoProducto.values());
        model.addAttribute("producto",       new Producto());
        model.addAttribute("notificaciones", pedidoService.contarNotificaciones());
        return "productos/form";
    }

    @PostMapping("/crear")
    public String crear(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Double precio,
            @RequestParam TipoProducto tipo,
            @RequestParam(required = false) Boolean disponible,
            RedirectAttributes ra) {

        if (nombre == null || nombre.isBlank()) {
            ra.addFlashAttribute("error", "El nombre es obligatorio.");
            return "redirect:/productos/crear";
        }
        productoService.crear(nombre.trim(), descripcion != null ? descripcion.trim() : "",
                precio, tipo, disponible);
        ra.addFlashAttribute("exito", "Producto creado exitosamente.");
        return "redirect:/productos";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Integer id, Model model) {
        Optional<Producto> p = productoService.buscarPorId(id);
        if (p.isEmpty()) return "redirect:/productos";
        model.addAttribute("accion",         "Editar");
        model.addAttribute("producto",       p.get());
        model.addAttribute("tipos",          TipoProducto.values());
        model.addAttribute("notificaciones", pedidoService.contarNotificaciones());
        return "productos/form";
    }

    @PostMapping("/{id}/editar")
    public String editar(
            @PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Double precio,
            @RequestParam TipoProducto tipo,
            @RequestParam(required = false) Boolean disponible,
            RedirectAttributes ra) {

        productoService.actualizar(id, nombre.trim(),
                descripcion != null ? descripcion.trim() : "",
                precio, tipo, disponible);
        ra.addFlashAttribute("exito", "Producto actualizado.");
        return "redirect:/productos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        productoService.eliminar(id);
        ra.addFlashAttribute("exito", "Producto eliminado.");
        return "redirect:/productos";
    }
}
