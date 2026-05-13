package co.edu.uniquindio.quickserve;

import co.edu.uniquindio.quickserve.controller.CocinaController;
import co.edu.uniquindio.quickserve.model.DetallePedido;
import co.edu.uniquindio.quickserve.model.Mesa;
import co.edu.uniquindio.quickserve.model.Mesero;
import co.edu.uniquindio.quickserve.model.Pedido;
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
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CocinaController — Tests")
class CocinaTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private CocinaController cocinaController;

    private MockMvc mockMvc;

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private Pedido pedidoCreado;
    private Pedido pedidoPreparacion;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cocinaController).build();

        Mesa mesa = new Mesa(3, true);
        Mesero mesero = new Mesero("1001", "Carlos Ramírez", "carlos", "mesa123");

        Producto producto = new Producto(1, "Bandeja Paisa", "Descripción",
                32000.0, true, TipoProducto.PLATOFUERTE);

        pedidoCreado = new Pedido();
        pedidoCreado.setId(1);
        pedidoCreado.setMesa(mesa);
        pedidoCreado.setMesero(mesero);
        pedidoCreado.setEstado(EstadoPedido.CREADO);
        pedidoCreado.setHoraPedido(LocalDateTime.now().minusMinutes(10));
        pedidoCreado.setObservaciones("Sin picante");
        pedidoCreado.setNotificacionPendiente(false);
        pedidoCreado.setDetalles(List.of(new DetallePedido(null, pedidoCreado, producto, 2)));

        pedidoPreparacion = new Pedido();
        pedidoPreparacion.setId(2);
        pedidoPreparacion.setMesa(mesa);
        pedidoPreparacion.setMesero(mesero);
        pedidoPreparacion.setEstado(EstadoPedido.PREPARACION);
        pedidoPreparacion.setHoraPedido(LocalDateTime.now().minusMinutes(5));
        pedidoPreparacion.setObservaciones("");
        pedidoPreparacion.setNotificacionPendiente(false);
        pedidoPreparacion.setDetalles(List.of(new DetallePedido(null, pedidoPreparacion, producto, 1)));
    }

    // ── GET /cocina ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /cocina — retorna vista 'cocina/panel'")
    void panelCocina_retornaVista() throws Exception {
        when(pedidoService.listarPorEstado(EstadoPedido.CREADO))
                .thenReturn(List.of(pedidoCreado));
        when(pedidoService.listarPorEstado(EstadoPedido.PREPARACION))
                .thenReturn(List.of(pedidoPreparacion));
        when(pedidoService.contarNotificaciones()).thenReturn(1L);

        mockMvc.perform(get("/cocina"))
                .andExpect(status().isOk())
                .andExpect(view().name("cocina/panel"));
    }

    @Test
    @DisplayName("GET /cocina — modelo contiene pedidosCreados")
    void panelCocina_modeloContienePedidosCreados() throws Exception {
        when(pedidoService.listarPorEstado(EstadoPedido.CREADO))
                .thenReturn(List.of(pedidoCreado));
        when(pedidoService.listarPorEstado(EstadoPedido.PREPARACION))
                .thenReturn(Collections.emptyList());
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/cocina"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("pedidosCreados"))
                .andExpect(model().attribute("pedidosCreados", List.of(pedidoCreado)));
    }

    @Test
    @DisplayName("GET /cocina — modelo contiene pedidosPreparacion")
    void panelCocina_modeloContienePedidosPreparacion() throws Exception {
        when(pedidoService.listarPorEstado(EstadoPedido.CREADO))
                .thenReturn(Collections.emptyList());
        when(pedidoService.listarPorEstado(EstadoPedido.PREPARACION))
                .thenReturn(List.of(pedidoPreparacion));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/cocina"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("pedidosPreparacion"))
                .andExpect(model().attribute("pedidosPreparacion", List.of(pedidoPreparacion)));
    }

    @Test
    @DisplayName("GET /cocina — modelo contiene notificaciones")
    void panelCocina_modeloContieneNotificaciones() throws Exception {
        when(pedidoService.listarPorEstado(any())).thenReturn(Collections.emptyList());
        when(pedidoService.contarNotificaciones()).thenReturn(3L);

        mockMvc.perform(get("/cocina"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("notificaciones", 3L));
    }

    @Test
    @DisplayName("GET /cocina — listas vacías cuando no hay pedidos")
    void panelCocina_listasVacias() throws Exception {
        when(pedidoService.listarPorEstado(any())).thenReturn(Collections.emptyList());
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/cocina"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("pedidosCreados", Collections.emptyList()))
                .andExpect(model().attribute("pedidosPreparacion", Collections.emptyList()));
    }

    // ── POST /cocina/{id}/preparacion ─────────────────────────────────────────

    @Test
    @DisplayName("POST /cocina/{id}/preparacion — llama marcarEnPreparacion y redirige")
    void marcarPreparacion_redirige() throws Exception {
        when(pedidoService.marcarEnPreparacion(1))
                .thenReturn(Optional.of(pedidoCreado));

        mockMvc.perform(post("/cocina/1/preparacion"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cocina"));

        verify(pedidoService, times(1)).marcarEnPreparacion(1);
    }

    @Test
    @DisplayName("POST /cocina/{id}/preparacion — pedido inexistente igual redirige sin error")
    void marcarPreparacion_pedidoInexistente_redirigeSinError() throws Exception {
        when(pedidoService.marcarEnPreparacion(999))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/cocina/999/preparacion"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cocina"));

        verify(pedidoService, times(1)).marcarEnPreparacion(999);
    }

    // ── POST /cocina/{id}/listo ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /cocina/{id}/listo — llama marcarListo y redirige")
    void marcarListo_redirige() throws Exception {
        when(pedidoService.marcarListo(2))
                .thenReturn(Optional.of(pedidoPreparacion));

        mockMvc.perform(post("/cocina/2/listo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cocina"));

        verify(pedidoService, times(1)).marcarListo(2);
    }

    @Test
    @DisplayName("POST /cocina/{id}/listo — activa notificacion al mesero")
    void marcarListo_activaNotificacion() throws Exception {
        pedidoPreparacion.setNotificacionPendiente(true);
        when(pedidoService.marcarListo(2))
                .thenReturn(Optional.of(pedidoPreparacion));

        mockMvc.perform(post("/cocina/2/listo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cocina"));

        verify(pedidoService, times(1)).marcarListo(2);
    }

    @Test
    @DisplayName("POST /cocina/{id}/listo — pedido inexistente igual redirige sin error")
    void marcarListo_pedidoInexistente_redirigeSinError() throws Exception {
        when(pedidoService.marcarListo(999))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/cocina/999/listo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cocina"));

        verify(pedidoService, times(1)).marcarListo(999);
    }

    // ── Flujo completo ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Flujo completo: CREADO → PREPARACION → LISTO")
    void flujoCompleto_creadoPreparacionListo() throws Exception {
        // Paso 1: panel muestra pedido en CREADO
        when(pedidoService.listarPorEstado(EstadoPedido.CREADO))
                .thenReturn(List.of(pedidoCreado));
        when(pedidoService.listarPorEstado(EstadoPedido.PREPARACION))
                .thenReturn(Collections.emptyList());
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/cocina"))
                .andExpect(model().attribute("pedidosCreados", List.of(pedidoCreado)));

        // Paso 2: cocina inicia preparación
        pedidoCreado.setEstado(EstadoPedido.PREPARACION);
        when(pedidoService.marcarEnPreparacion(1))
                .thenReturn(Optional.of(pedidoCreado));

        mockMvc.perform(post("/cocina/1/preparacion"))
                .andExpect(redirectedUrl("/cocina"));

        verify(pedidoService).marcarEnPreparacion(1);

        // Paso 3: cocina marca listo → notifica mesero
        pedidoCreado.setEstado(EstadoPedido.LISTO);
        pedidoCreado.setNotificacionPendiente(true);
        when(pedidoService.marcarListo(1))
                .thenReturn(Optional.of(pedidoCreado));

        mockMvc.perform(post("/cocina/1/listo"))
                .andExpect(redirectedUrl("/cocina"));

        verify(pedidoService).marcarListo(1);
    }
}