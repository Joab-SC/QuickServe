package co.edu.uniquindio.quickserve.controller;

import co.edu.uniquindio.quickserve.model.Mesa;
import co.edu.uniquindio.quickserve.model.Pedido;
import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import co.edu.uniquindio.quickserve.service.PedidoService;
import co.edu.uniquindio.quickserve.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ProductoService productoService;

    public PedidoController(PedidoService pedidoService, ProductoService productoService) {
        this.pedidoService   = pedidoService;
        this.productoService = productoService;
    }

    // ── LISTAR ────────────────────────────────────────────────────────────────

    @GetMapping
    public String listar(@RequestParam(required = false) String estado, Model model) {
        if (estado != null && !estado.isBlank()) {
            try {
                EstadoPedido ep = EstadoPedido.valueOf(estado);
                model.addAttribute("pedidos", pedidoService.listarPorEstado(ep));
                model.addAttribute("filtroEstado", estado);
            } catch (IllegalArgumentException e) {
                model.addAttribute("pedidos", pedidoService.listarTodos());
            }
        } else {
            model.addAttribute("pedidos", pedidoService.listarTodos());
            model.addAttribute("filtroEstado", "");
        }
        model.addAttribute("estados",        EstadoPedido.values());
        model.addAttribute("totalPedidos",   pedidoService.listarTodos().size());
        model.addAttribute("notificaciones", pedidoService.contarNotificaciones());
        return "pedidos/listar";
    }

    // ── DETALLE ───────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        if (pedido.isEmpty()) return "redirect:/pedidos";
        model.addAttribute("pedido",         pedido.get());
        model.addAttribute("estados",        EstadoPedido.values());
        model.addAttribute("notificaciones", pedidoService.contarNotificaciones());
        return "pedidos/detalle";
    }


}
