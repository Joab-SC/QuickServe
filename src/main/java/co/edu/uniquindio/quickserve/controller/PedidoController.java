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

    // ── FORMULARIO REGISTRAR (PANTALLA 1 - transacción) ──────────────────────

    @GetMapping("/registrar")
    public String formRegistrar(@RequestParam(required = false) Integer mesa, Model model) {
        model.addAttribute("mesasDisponibles", pedidoService.getMesasDisponibles());
        model.addAttribute("meseros",          pedidoService.getMeseros());
        model.addAttribute("entradas",         productoService.listarPorTipo(TipoProducto.ENTRADA));
        model.addAttribute("platosFuertes",    productoService.listarPorTipo(TipoProducto.PLATOFUERTE));
        model.addAttribute("postres",          productoService.listarPorTipo(TipoProducto.POSTRE));
        model.addAttribute("bebidas",          productoService.listarPorTipo(TipoProducto.BEBIDA));
        model.addAttribute("mesaPreseleccionada", mesa);
        model.addAttribute("notificaciones",   pedidoService.contarNotificaciones());
        return "pedidos/registrar";
    }

    @PostMapping("/registrar")
    public String registrar(
            @RequestParam Integer mesaNumero,
            @RequestParam String meseroCedula,
            @RequestParam(required = false) String observaciones,
            @RequestParam(name = "productoIds", required = false) List<Integer> productoIds,
            @RequestParam(name = "cantidades",  required = false) List<Integer> cantidades,
            RedirectAttributes ra) {

        if (productoIds == null || productoIds.isEmpty()) {
            ra.addFlashAttribute("error", "Debe agregar al menos un producto al pedido.");
            return "redirect:/pedidos/registrar";
        }

        try {
            Pedido nuevo = pedidoService.registrarPedido(
                    mesaNumero, meseroCedula, productoIds, cantidades, observaciones);
            return "redirect:/pedidos/confirmado/" + nuevo.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pedidos/registrar";
        }
    }

    // ── CONFIRMACIÓN ──────────────────────────────────────────────────────────

    @GetMapping("/confirmado/{id}")
    public String confirmado(@PathVariable Integer id, Model model) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        if (pedido.isEmpty()) return "redirect:/";
        model.addAttribute("pedido",         pedido.get());
        model.addAttribute("notificaciones", pedidoService.contarNotificaciones());
        return "pedidos/confirmado";
    }

    // ── EDITAR ────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Integer id, Model model) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        if (pedido.isEmpty() || pedido.get().getEstado() == EstadoPedido.ENTREGADO)
            return "redirect:/pedidos";

        // Mesa actual siempre disponible para edición
        List<Mesa> mesas = pedidoService.getMesasDisponibles();
        Mesa mesaActual = pedido.get().getMesa();
        if (mesas.stream().noneMatch(m -> m.getNumero().equals(mesaActual.getNumero()))) {
            mesas.add(0, mesaActual);
        }

        model.addAttribute("pedido",           pedido.get());
        model.addAttribute("mesasDisponibles", mesas);
        model.addAttribute("meseros",          pedidoService.getMeseros());
        model.addAttribute("entradas",         productoService.listarPorTipo(TipoProducto.ENTRADA));
        model.addAttribute("platosFuertes",    productoService.listarPorTipo(TipoProducto.PLATOFUERTE));
        model.addAttribute("postres",          productoService.listarPorTipo(TipoProducto.POSTRE));
        model.addAttribute("bebidas",          productoService.listarPorTipo(TipoProducto.BEBIDA));
        model.addAttribute("notificaciones",   pedidoService.contarNotificaciones());
        return "pedidos/editar";
    }

    @PostMapping("/{id}/editar")
    public String editar(
            @PathVariable Integer id,
            @RequestParam Integer mesaNumero,
            @RequestParam String meseroCedula,
            @RequestParam(required = false) String observaciones,
            @RequestParam(name = "productoIds", required = false) List<Integer> productoIds,
            @RequestParam(name = "cantidades",  required = false) List<Integer> cantidades,
            RedirectAttributes ra) {

        if (productoIds == null || productoIds.isEmpty()) {
            ra.addFlashAttribute("error", "Debe agregar al menos un producto.");
            return "redirect:/pedidos/" + id + "/editar";
        }
        pedidoService.actualizar(id, mesaNumero, meseroCedula, productoIds, cantidades, observaciones);
        return "redirect:/pedidos/" + id;
    }


}
