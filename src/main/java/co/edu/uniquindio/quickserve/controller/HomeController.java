package co.edu.uniquindio.quickserve.controller;


import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PedidoService pedidoService;

    public HomeController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        var mesas = pedidoService.getTodasLasMesas();

        long mesasDisponibles = mesas.stream()
                .filter(m -> !m.getOcupada())
                .count();

        model.addAttribute("mesas", mesas);
        model.addAttribute("mesasDisponibles", mesasDisponibles);

        model.addAttribute("pedidosActivos", pedidoService.listarActivos());
        model.addAttribute("totalPedidos", pedidoService.listarTodos().size());
        model.addAttribute("notificaciones", pedidoService.contarNotificaciones());
        model.addAttribute("EstadoPedido", EstadoPedido.values());

        return "home";
    }
}