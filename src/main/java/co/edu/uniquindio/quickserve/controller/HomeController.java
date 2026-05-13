package co.edu.uniquindio.quickserve.controller;


import co.edu.uniquindio.quickserve.model.Pedido;
import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final PedidoService pedidoService;

    public HomeController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        var mesas = pedidoService.getTodasLasMesas();
        var pedidosActivos = pedidoService.listarActivos();

        long mesasDisponibles = mesas.stream()
                .filter(m -> !m.getOcupada())
                .count();

        // Mapa mesaNumero → pedido activo
        Map<Integer, Pedido> pedidosPorMesa = pedidosActivos.stream()
                .collect(Collectors.toMap(
                        p -> p.getMesa().getNumero(),
                        p -> p,
                        (a, b) -> a  // si hay duplicado, queda el primero
                ));

        model.addAttribute("mesas", mesas);
        model.addAttribute("mesasDisponibles", mesasDisponibles);
        model.addAttribute("pedidosActivos", pedidosActivos);
        model.addAttribute("pedidosPorMesa", pedidosPorMesa);
        model.addAttribute("totalPedidos", pedidoService.listarTodos().size());
        model.addAttribute("notificaciones", pedidoService.contarNotificaciones());
        model.addAttribute("EstadoPedido", EstadoPedido.values());

        return "home";
    }
}