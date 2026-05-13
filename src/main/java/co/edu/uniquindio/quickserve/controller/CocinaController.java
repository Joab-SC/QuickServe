package co.edu.uniquindio.quickserve.controller;

import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cocina")
public class CocinaController {

    private final PedidoService pedidoService;

    public CocinaController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /** Panel de cocina: ve CREADO y PREPARACION */
    @GetMapping
    public String panelCocina(Model model) {
        model.addAttribute("pedidosCreados",
                pedidoService.listarPorEstado(EstadoPedido.CREADO));
        model.addAttribute("pedidosPreparacion",
                pedidoService.listarPorEstado(EstadoPedido.PREPARACION));
        model.addAttribute("notificaciones", pedidoService.contarNotificaciones());
        return "cocina/panel";
    }
}
