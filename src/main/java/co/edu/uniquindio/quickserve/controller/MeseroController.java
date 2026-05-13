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
@RequestMapping("/mesero")
public class MeseroController {

    private final PedidoService pedidoService;

    public MeseroController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Panel del mesero: ve pedidos LISTOS para entregar (con notificación de cocina)
     * y sus pedidos CREADOS pendientes de envío.
     */
    @GetMapping
    public String panelMesero(Model model) {
        model.addAttribute("pedidosListos",
                pedidoService.listarPorEstado(EstadoPedido.LISTO));
        model.addAttribute("pedidosCreados",
                pedidoService.listarPorEstado(EstadoPedido.CREADO));
        model.addAttribute("notificaciones",
                pedidoService.contarNotificaciones());
        return "mesero/panel";
    }

}
