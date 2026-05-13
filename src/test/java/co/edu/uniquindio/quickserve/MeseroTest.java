package co.edu.uniquindio.quickserve;

import co.edu.uniquindio.quickserve.controller.MeseroController;
import co.edu.uniquindio.quickserve.model.Mesa;
import co.edu.uniquindio.quickserve.model.Mesero;
import co.edu.uniquindio.quickserve.model.Pedido;
import co.edu.uniquindio.quickserve.model.DetallePedido;
import co.edu.uniquindio.quickserve.model.Producto;
import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import co.edu.uniquindio.quickserve.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeseroController — Tests")
class MeseroTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private MeseroController meseroController;

    private MockMvc mockMvc;

    private Pedido pedidoListo;
    private Pedido pedidoCreado;
    private Mesa mesaOcupada;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(meseroController).build();

        mesaOcupada = new Mesa(5, true);
        Mesero mesero = new Mesero("1001", "Carlos Ramírez", "carlos", "mesa123");
        Producto producto = new Producto(1, "Bandeja Paisa", "Desc", 32000.0, true, TipoProducto.PLATOFUERTE);

        pedidoListo = new Pedido();
        pedidoListo.setId(1);
        pedidoListo.setMesa(mesaOcupada);
        pedidoListo.setMesero(mesero);
        pedidoListo.setEstado(EstadoPedido.LISTO);
        pedidoListo.setHoraPedido(LocalDateTime.now().minusMinutes(15));
        pedidoListo.setObservaciones("Sin picante");
        pedidoListo.setNotificacionPendiente(true);
        pedidoListo.setDetalles(List.of(new DetallePedido(null, pedidoListo, producto, 2)));

        pedidoCreado = new Pedido();
        pedidoCreado.setId(2);
        pedidoCreado.setMesa(new Mesa(3, true));
        pedidoCreado.setMesero(mesero);
        pedidoCreado.setEstado(EstadoPedido.CREADO);
        pedidoCreado.setHoraPedido(LocalDateTime.now().minusMinutes(5));
        pedidoCreado.setObservaciones("");
        pedidoCreado.setNotificacionPendiente(false);
        pedidoCreado.setDetalles(List.of(new DetallePedido(null, pedidoCreado, producto, 1)));
    }

    // ── GET /mesero ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /mesero — retorna vista 'mesero/panel'")
    void panelMesero_retornaVista() throws Exception {
        when(pedidoService.listarPorEstado(EstadoPedido.LISTO)).thenReturn(List.of(pedidoListo));
        when(pedidoService.listarPorEstado(EstadoPedido.CREADO)).thenReturn(List.of(pedidoCreado));
        when(pedidoService.contarNotificaciones()).thenReturn(1L);
        when(pedidoService.getMesasParaDesocupar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/mesero"))
                .andExpect(status().isOk())
                .andExpect(view().name("mesero/panel"));
    }

    @Test
    @DisplayName("GET /mesero — modelo contiene pedidosListos")
    void panelMesero_modeloContienePedidosListos() throws Exception {
        when(pedidoService.listarPorEstado(EstadoPedido.LISTO)).thenReturn(List.of(pedidoListo));
        when(pedidoService.listarPorEstado(EstadoPedido.CREADO)).thenReturn(Collections.emptyList());
        when(pedidoService.contarNotificaciones()).thenReturn(1L);
        when(pedidoService.getMesasParaDesocupar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/mesero"))
                .andExpect(model().attribute("pedidosListos", List.of(pedidoListo)));
    }

    @Test
    @DisplayName("GET /mesero — modelo contiene pedidosCreados")
    void panelMesero_modeloContienePedidosCreados() throws Exception {
        when(pedidoService.listarPorEstado(EstadoPedido.LISTO)).thenReturn(Collections.emptyList());
        when(pedidoService.listarPorEstado(EstadoPedido.CREADO)).thenReturn(List.of(pedidoCreado));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);
        when(pedidoService.getMesasParaDesocupar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/mesero"))
                .andExpect(model().attribute("pedidosCreados", List.of(pedidoCreado)));
    }

    @Test
    @DisplayName("GET /mesero — modelo contiene notificaciones")
    void panelMesero_modeloContieneNotificaciones() throws Exception {
        when(pedidoService.listarPorEstado(any())).thenReturn(Collections.emptyList());
        when(pedidoService.contarNotificaciones()).thenReturn(2L);
        when(pedidoService.getMesasParaDesocupar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/mesero"))
                .andExpect(model().attribute("notificaciones", 2L));
    }

    @Test
    @DisplayName("GET /mesero — modelo contiene mesasOcupadas para desocupar")
    void panelMesero_modeloContieneMesasParaDesocupar() throws Exception {
        when(pedidoService.listarPorEstado(any())).thenReturn(Collections.emptyList());
        when(pedidoService.contarNotificaciones()).thenReturn(0L);
        when(pedidoService.getMesasParaDesocupar()).thenReturn(List.of(mesaOcupada));

        mockMvc.perform(get("/mesero"))
                .andExpect(model().attribute("mesasOcupadas", List.of(mesaOcupada)));
    }

    @Test
    @DisplayName("GET /mesero — listas vacías cuando no hay pedidos ni mesas")
    void panelMesero_todoVacio() throws Exception {
        when(pedidoService.listarPorEstado(any())).thenReturn(Collections.emptyList());
        when(pedidoService.contarNotificaciones()).thenReturn(0L);
        when(pedidoService.getMesasParaDesocupar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/mesero"))
                .andExpect(model().attribute("pedidosListos", Collections.emptyList()))
                .andExpect(model().attribute("pedidosCreados", Collections.emptyList()))
                .andExpect(model().attribute("mesasOcupadas", Collections.emptyList()));
    }

    // ── POST /mesero/{id}/entregar ────────────────────────────────────────────

    @Test
    @DisplayName("POST /mesero/{id}/entregar — llama marcarEntregado y redirige")
    void confirmarEntrega_redirige() throws Exception {
        when(pedidoService.marcarEntregado(1)).thenReturn(Optional.of(pedidoListo));

        mockMvc.perform(post("/mesero/1/entregar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mesero"));

        verify(pedidoService, times(1)).marcarEntregado(1);
    }

    @Test
    @DisplayName("POST /mesero/{id}/entregar — pedido inexistente redirige sin error")
    void confirmarEntrega_pedidoInexistente_redirigeSinError() throws Exception {
        when(pedidoService.marcarEntregado(999)).thenReturn(Optional.empty());

        mockMvc.perform(post("/mesero/999/entregar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mesero"));

        verify(pedidoService, times(1)).marcarEntregado(999);
    }

    // ── POST /mesero/{id}/notificacion/limpiar ────────────────────────────────

    @Test
    @DisplayName("POST /mesero/{id}/notificacion/limpiar — limpia notificacion y redirige")
    void limpiarNotificacion_redirige() throws Exception {
        mockMvc.perform(post("/mesero/1/notificacion/limpiar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mesero"));

        verify(pedidoService, times(1)).limpiarNotificacion(1);
    }

    @Test
    @DisplayName("POST /mesero/{id}/notificacion/limpiar — id inexistente redirige sin error")
    void limpiarNotificacion_idInexistente_redirigeSinError() throws Exception {
        doNothing().when(pedidoService).limpiarNotificacion(999);

        mockMvc.perform(post("/mesero/999/notificacion/limpiar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mesero"));

        verify(pedidoService, times(1)).limpiarNotificacion(999);
    }

    // ── POST /mesero/mesas/{numero}/desocupar ─────────────────────────────────

    @Test
    @DisplayName("POST /mesero/mesas/{numero}/desocupar — llama desocuparMesa y redirige")
    void desocuparMesa_redirige() throws Exception {
        doNothing().when(pedidoService).desocuparMesa(5);

        mockMvc.perform(post("/mesero/mesas/5/desocupar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mesero"));

        verify(pedidoService, times(1)).desocuparMesa(5);
    }

    @Test
    @DisplayName("POST /mesero/mesas/{numero}/desocupar — mesa inexistente redirige sin error")
    void desocuparMesa_mesaInexistente_redirigeSinError() throws Exception {
        doNothing().when(pedidoService).desocuparMesa(99);

        mockMvc.perform(post("/mesero/mesas/99/desocupar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mesero"));

        verify(pedidoService, times(1)).desocuparMesa(99);
    }

    // ── Flujo completo ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Flujo completo: LISTO → ENTREGADO → desocupar mesa")
    void flujoCompleto_listoEntregadoDesocupar() throws Exception {
        // Paso 1: panel muestra pedido LISTO con notificación
        when(pedidoService.listarPorEstado(EstadoPedido.LISTO)).thenReturn(List.of(pedidoListo));
        when(pedidoService.listarPorEstado(EstadoPedido.CREADO)).thenReturn(Collections.emptyList());
        when(pedidoService.contarNotificaciones()).thenReturn(1L);
        when(pedidoService.getMesasParaDesocupar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/mesero"))
                .andExpect(model().attribute("pedidosListos", List.of(pedidoListo)))
                .andExpect(model().attribute("notificaciones", 1L));

        // Paso 2: mesero marca notificación como vista
        mockMvc.perform(post("/mesero/1/notificacion/limpiar"))
                .andExpect(redirectedUrl("/mesero"));
        verify(pedidoService).limpiarNotificacion(1);

        // Paso 3: mesero confirma entrega
        pedidoListo.setEstado(EstadoPedido.ENTREGADO);
        pedidoListo.setNotificacionPendiente(false);
        when(pedidoService.marcarEntregado(1)).thenReturn(Optional.of(pedidoListo));

        mockMvc.perform(post("/mesero/1/entregar"))
                .andExpect(redirectedUrl("/mesero"));
        verify(pedidoService).marcarEntregado(1);

        // Paso 4: mesa aparece lista para desocupar
        when(pedidoService.getMesasParaDesocupar()).thenReturn(List.of(mesaOcupada));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/mesero"))
                .andExpect(model().attribute("mesasOcupadas", List.of(mesaOcupada)));

        // Paso 5: mesero desocupa la mesa
        doNothing().when(pedidoService).desocuparMesa(5);

        mockMvc.perform(post("/mesero/mesas/5/desocupar"))
                .andExpect(redirectedUrl("/mesero"));
        verify(pedidoService).desocuparMesa(5);
    }
}