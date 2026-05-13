package co.edu.uniquindio.quickserve;

import co.edu.uniquindio.quickserve.controller.PedidoController;
import co.edu.uniquindio.quickserve.model.*;
import co.edu.uniquindio.quickserve.model.enums.EstadoPedido;
import co.edu.uniquindio.quickserve.model.enums.TipoProducto;
import co.edu.uniquindio.quickserve.service.PedidoService;
import co.edu.uniquindio.quickserve.service.ProductoService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoController — Tests")
class PedidoTest {

    @Mock private PedidoService pedidoService;
    @Mock private ProductoService productoService;

    @InjectMocks
    private PedidoController pedidoController;

    private MockMvc mockMvc;

    private Pedido pedidoCreado;
    private Pedido pedidoEntregado;
    private Mesa mesa;
    private Mesero mesero;
    private Producto producto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();

        mesa    = new Mesa(3, true);
        mesero  = new Mesero("1001", "Carlos Ramírez", "carlos", "mesa123");
        producto = new Producto(1, "Bandeja Paisa", "Desc", 32000.0, true, TipoProducto.PLATOFUERTE);

        pedidoCreado = new Pedido();
        pedidoCreado.setId(1);
        pedidoCreado.setMesa(mesa);
        pedidoCreado.setMesero(mesero);
        pedidoCreado.setEstado(EstadoPedido.CREADO);
        pedidoCreado.setHoraPedido(LocalDateTime.now().minusMinutes(10));
        pedidoCreado.setObservaciones("Sin cebolla");
        pedidoCreado.setNotificacionPendiente(false);
        pedidoCreado.setDetalles(List.of(new DetallePedido(null, pedidoCreado, producto, 2)));

        pedidoEntregado = new Pedido();
        pedidoEntregado.setId(2);
        pedidoEntregado.setMesa(new Mesa(5, false));
        pedidoEntregado.setMesero(mesero);
        pedidoEntregado.setEstado(EstadoPedido.ENTREGADO);
        pedidoEntregado.setHoraPedido(LocalDateTime.now().minusMinutes(60));
        pedidoEntregado.setHoraEntrega(LocalDateTime.now().minusMinutes(30));
        pedidoEntregado.setObservaciones("");
        pedidoEntregado.setNotificacionPendiente(false);
        pedidoEntregado.setDetalles(List.of(new DetallePedido(null, pedidoEntregado, producto, 1)));
    }

    // ── GET /pedidos ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /pedidos — retorna vista 'pedidos/listar'")
    void listar_retornaVista() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of(pedidoCreado, pedidoEntregado));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedidos/listar"));
    }

    @Test
    @DisplayName("GET /pedidos — sin filtro muestra todos los pedidos")
    void listar_sinFiltro_muestraTodos() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of(pedidoCreado, pedidoEntregado));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/pedidos"))
                .andExpect(model().attribute("pedidos", List.of(pedidoCreado, pedidoEntregado)))
                .andExpect(model().attribute("filtroEstado", ""));
    }

    @Test
    @DisplayName("GET /pedidos?estado=CREADO — filtra por estado")
    void listar_conFiltroValido_filtraPorEstado() throws Exception {
        when(pedidoService.listarPorEstado(EstadoPedido.CREADO)).thenReturn(List.of(pedidoCreado));
        when(pedidoService.listarTodos()).thenReturn(List.of(pedidoCreado, pedidoEntregado));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/pedidos").param("estado", "CREADO"))
                .andExpect(model().attribute("pedidos", List.of(pedidoCreado)))
                .andExpect(model().attribute("filtroEstado", "CREADO"));
    }

    @Test
    @DisplayName("GET /pedidos?estado=INVALIDO — estado inválido muestra todos")
    void listar_conFiltroInvalido_muestraTodos() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of(pedidoCreado, pedidoEntregado));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/pedidos").param("estado", "INVALIDO"))
                .andExpect(model().attribute("pedidos", List.of(pedidoCreado, pedidoEntregado)));
    }

    // ── GET /pedidos/{id} ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /pedidos/{id} — pedido existente retorna detalle")
    void detalle_pedidoExistente_retornaVista() throws Exception {
        when(pedidoService.buscarPorId(1)).thenReturn(Optional.of(pedidoCreado));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedidos/detalle"))
                .andExpect(model().attribute("pedido", pedidoCreado));
    }

    @Test
    @DisplayName("GET /pedidos/{id} — pedido inexistente redirige a /pedidos")
    void detalle_pedidoInexistente_redirige() throws Exception {
        when(pedidoService.buscarPorId(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pedidos/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos"));
    }

    // ── GET /pedidos/registrar ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /pedidos/registrar — retorna formulario con datos")
    void formRegistrar_retornaVista() throws Exception {
        when(pedidoService.getMesasDisponibles()).thenReturn(List.of(new Mesa(1, false)));
        when(pedidoService.getMeseros()).thenReturn(List.of(mesero));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);
        when(productoService.listarPorTipo(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pedidos/registrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedidos/registrar"))
                .andExpect(model().attributeExists("mesasDisponibles", "meseros",
                        "entradas", "platosFuertes", "postres", "bebidas"));
    }

    @Test
    @DisplayName("GET /pedidos/registrar?mesa=3 — preselecciona mesa")
    void formRegistrar_conMesaParam_preseleccionaMesa() throws Exception {
        when(pedidoService.getMesasDisponibles()).thenReturn(List.of(mesa));
        when(pedidoService.getMeseros()).thenReturn(List.of(mesero));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);
        when(productoService.listarPorTipo(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pedidos/registrar").param("mesa", "3"))
                .andExpect(model().attribute("mesaPreseleccionada", 3));
    }

    // ── POST /pedidos/registrar ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /pedidos/registrar — registro exitoso redirige a confirmado")
    void registrar_exitoso_redirige() throws Exception {
        when(pedidoService.registrarPedido(anyInt(), anyString(), anyList(), anyList(), anyString()))
                .thenReturn(pedidoCreado);

        mockMvc.perform(post("/pedidos/registrar")
                        .param("mesaNumero", "3")
                        .param("meseroCedula", "1001")
                        .param("observaciones", "Sin cebolla")
                        .param("productoIds", "1")
                        .param("cantidades", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos/confirmado/1"));
    }

    @Test
    @DisplayName("POST /pedidos/registrar — sin productos redirige con error")
    void registrar_sinProductos_redirigeCon_error() throws Exception {
        mockMvc.perform(post("/pedidos/registrar")
                        .param("mesaNumero", "3")
                        .param("meseroCedula", "1001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos/registrar"));

        verify(pedidoService, never()).registrarPedido(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("POST /pedidos/registrar — excepción del servicio redirige con error")
    void registrar_excepcionServicio_redirigeCon_error() throws Exception {
        when(pedidoService.registrarPedido(anyInt(), anyString(), anyList(), anyList(), anyString()))
                .thenThrow(new IllegalArgumentException("Cantidad inválida"));

        mockMvc.perform(post("/pedidos/registrar")
                        .param("mesaNumero", "3")
                        .param("meseroCedula", "1001")
                        .param("productoIds", "1")
                        .param("cantidades", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos/registrar"));
    }

    // ── GET /pedidos/confirmado/{id} ──────────────────────────────────────────

    @Test
    @DisplayName("GET /pedidos/confirmado/{id} — pedido existente muestra confirmacion")
    void confirmado_pedidoExistente_retornaVista() throws Exception {
        when(pedidoService.buscarPorId(1)).thenReturn(Optional.of(pedidoCreado));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/pedidos/confirmado/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedidos/confirmado"))
                .andExpect(model().attribute("pedido", pedidoCreado));
    }

    @Test
    @DisplayName("GET /pedidos/confirmado/{id} — pedido inexistente redirige a /")
    void confirmado_pedidoInexistente_redirige() throws Exception {
        when(pedidoService.buscarPorId(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pedidos/confirmado/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    // ── GET /pedidos/{id}/editar ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /pedidos/{id}/editar — pedido CREADO retorna formulario editar")
    void formEditar_pedidoCreado_retornaVista() throws Exception {
        when(pedidoService.buscarPorId(1)).thenReturn(Optional.of(pedidoCreado));
        when(pedidoService.getMesasDisponibles()).thenReturn(Collections.emptyList());
        when(pedidoService.getMeseros()).thenReturn(List.of(mesero));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);
        when(productoService.listarPorTipo(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pedidos/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedidos/editar"))
                .andExpect(model().attribute("pedido", pedidoCreado));
    }

    @Test
    @DisplayName("GET /pedidos/{id}/editar — pedido ENTREGADO redirige a /pedidos")
    void formEditar_pedidoEntregado_redirige() throws Exception {
        when(pedidoService.buscarPorId(2)).thenReturn(Optional.of(pedidoEntregado));

        mockMvc.perform(get("/pedidos/2/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos"));
    }

    @Test
    @DisplayName("GET /pedidos/{id}/editar — pedido inexistente redirige a /pedidos")
    void formEditar_pedidoInexistente_redirige() throws Exception {
        when(pedidoService.buscarPorId(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pedidos/999/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos"));
    }

    // ── POST /pedidos/{id}/editar ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /pedidos/{id}/editar — edición exitosa redirige al detalle")
    void editar_exitoso_redirige() throws Exception {
        when(pedidoService.actualizar(anyInt(), anyInt(), anyString(), anyList(), anyList(), anyString()))
                .thenReturn(Optional.of(pedidoCreado));

        mockMvc.perform(post("/pedidos/1/editar")
                        .param("mesaNumero", "3")
                        .param("meseroCedula", "1001")
                        .param("observaciones", "Con limón")
                        .param("productoIds", "1")
                        .param("cantidades", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos/1"));
    }

    @Test
    @DisplayName("POST /pedidos/{id}/editar — sin productos redirige con error")
    void editar_sinProductos_redirigeCon_error() throws Exception {
        mockMvc.perform(post("/pedidos/1/editar")
                        .param("mesaNumero", "3")
                        .param("meseroCedula", "1001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos/1/editar"));

        verify(pedidoService, never()).actualizar(any(), any(), any(), any(), any(), any());
    }

    // ── POST /pedidos/{id}/eliminar ───────────────────────────────────────────

    @Test
    @DisplayName("POST /pedidos/{id}/eliminar — elimina y redirige a /pedidos")
    void eliminar_redirige() throws Exception {
        doNothing().when(pedidoService).eliminar(1);

        mockMvc.perform(post("/pedidos/1/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos"));

        verify(pedidoService, times(1)).eliminar(1);
    }

    // ── POST /pedidos/{id}/estado ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /pedidos/{id}/estado — estado válido actualiza y redirige")
    void cambiarEstado_estadoValido_redirige() throws Exception {
        when(pedidoService.actualizarEstado(1, EstadoPedido.PREPARACION))
                .thenReturn(Optional.of(pedidoCreado));

        mockMvc.perform(post("/pedidos/1/estado")
                        .param("estado", "PREPARACION"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos"));

        verify(pedidoService, times(1)).actualizarEstado(1, EstadoPedido.PREPARACION);
    }

    @Test
    @DisplayName("POST /pedidos/{id}/estado — estado inválido no llama al servicio y redirige")
    void cambiarEstado_estadoInvalido_redirigeSinLlamarServicio() throws Exception {
        mockMvc.perform(post("/pedidos/1/estado")
                        .param("estado", "ESTADO_RARO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pedidos"));

        verify(pedidoService, never()).actualizarEstado(any(), any());
    }

    @Test
    @DisplayName("POST /pedidos/{id}/estado — param 'next' personaliza redirección")
    void cambiarEstado_conNext_redirigaANext() throws Exception {
        when(pedidoService.actualizarEstado(1, EstadoPedido.LISTO))
                .thenReturn(Optional.of(pedidoCreado));

        mockMvc.perform(post("/pedidos/1/estado")
                        .param("estado", "LISTO")
                        .param("next", "/cocina"))
                .andExpect(redirectedUrl("/cocina"));
    }

    // ── POST /pedidos/{id}/notificacion/limpiar ───────────────────────────────

    @Test
    @DisplayName("POST /pedidos/{id}/notificacion/limpiar — limpia y redirige a /mesero por defecto")
    void limpiarNotificacion_redirigePorDefecto() throws Exception {
        doNothing().when(pedidoService).limpiarNotificacion(1);

        mockMvc.perform(post("/pedidos/1/notificacion/limpiar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mesero"));

        verify(pedidoService, times(1)).limpiarNotificacion(1);
    }

    @Test
    @DisplayName("POST /pedidos/{id}/notificacion/limpiar — param 'next' personaliza redirección")
    void limpiarNotificacion_conNext_redirigaANext() throws Exception {
        doNothing().when(pedidoService).limpiarNotificacion(1);

        mockMvc.perform(post("/pedidos/1/notificacion/limpiar")
                        .param("next", "/pedidos/1"))
                .andExpect(redirectedUrl("/pedidos/1"));
    }
}