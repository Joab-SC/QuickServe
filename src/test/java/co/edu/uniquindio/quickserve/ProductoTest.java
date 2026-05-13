package co.edu.uniquindio.quickserve;

import co.edu.uniquindio.quickserve.controller.ProductoController;
import co.edu.uniquindio.quickserve.model.Producto;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoController — Tests")
class ProductoTest {

    @Mock private ProductoService productoService;
    @Mock private PedidoService   pedidoService;

    @InjectMocks
    private ProductoController productoController;

    private MockMvc mockMvc;

    private Producto productoActivo;
    private Producto productoInactivo;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();

        productoActivo = new Producto(1, "Bandeja Paisa", "Frijoles y más",
                32000.0, true, TipoProducto.PLATOFUERTE);

        productoInactivo = new Producto(2, "Costilla BBQ", "Costilla de res",
                35000.0, false, TipoProducto.PLATOFUERTE);
    }

    // ── GET /productos ────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /productos — retorna vista 'productos/listar'")
    void listar_retornaVista() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(productoActivo, productoInactivo));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/listar"));
    }

    @Test
    @DisplayName("GET /productos — sin filtro muestra todos los productos")
    void listar_sinFiltro_muestraTodos() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(productoActivo, productoInactivo));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/productos"))
                .andExpect(model().attribute("productos", List.of(productoActivo, productoInactivo)))
                .andExpect(model().attribute("filtroTipo", ""));
    }

    @Test
    @DisplayName("GET /productos?tipo=PLATOFUERTE — filtra por tipo válido")
    void listar_conFiltroValido_filtraPorTipo() throws Exception {
        when(productoService.listarPorTipo(TipoProducto.PLATOFUERTE))
                .thenReturn(List.of(productoActivo, productoInactivo));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/productos").param("tipo", "PLATOFUERTE"))
                .andExpect(model().attribute("productos", List.of(productoActivo, productoInactivo)))
                .andExpect(model().attribute("filtroTipo", "PLATOFUERTE"));
    }

    @Test
    @DisplayName("GET /productos?tipo=INVALIDO — tipo inválido muestra todos sin explotar")
    void listar_conFiltroInvalido_muestraTodos() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(productoActivo));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/productos").param("tipo", "INVALIDO"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("productos", List.of(productoActivo)));
    }

    @Test
    @DisplayName("GET /productos — modelo contiene todos los tipos y notificaciones")
    void listar_modeloContieneAtributosNecesarios() throws Exception {
        when(productoService.listarTodos()).thenReturn(Collections.emptyList());
        when(pedidoService.contarNotificaciones()).thenReturn(3L);

        mockMvc.perform(get("/productos"))
                .andExpect(model().attributeExists("tipos"))
                .andExpect(model().attribute("notificaciones", 3L));
    }

    // ── GET /productos/crear ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /productos/crear — retorna formulario con atributos")
    void formCrear_retornaVista() throws Exception {
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/productos/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/form"))
                .andExpect(model().attribute("accion", "Crear"))
                .andExpect(model().attributeExists("tipos", "producto"));
    }

    // ── POST /productos/crear ─────────────────────────────────────────────────

    @Test
    @DisplayName("POST /productos/crear — creación exitosa redirige a /productos")
    void crear_exitoso_redirige() throws Exception {
        Producto nuevo = new Producto(16, "Limonada", "Natural", 8000.0, true, TipoProducto.BEBIDA);
        when(productoService.crear(anyString(), anyString(), anyDouble(), any(), any()))
                .thenReturn(nuevo);

        mockMvc.perform(post("/productos/crear")
                        .param("nombre", "Limonada")
                        .param("descripcion", "Natural")
                        .param("precio", "8000.0")
                        .param("tipo", "BEBIDA")
                        .param("disponible", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(productoService, times(1))
                .crear(eq("Limonada"), eq("Natural"), eq(8000.0), eq(TipoProducto.BEBIDA), eq(true));
    }

    @Test
    @DisplayName("POST /productos/crear — nombre vacío redirige con error sin llamar al servicio")
    void crear_nombreVacio_redirigeCon_error() throws Exception {
        mockMvc.perform(post("/productos/crear")
                        .param("nombre", "   ")
                        .param("precio", "8000.0")
                        .param("tipo", "BEBIDA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos/crear"));

        verify(productoService, never()).crear(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("POST /productos/crear — disponible null se trata como false")
    void crear_disponibleNull_seEnviaComoNull() throws Exception {
        when(productoService.crear(anyString(), anyString(), anyDouble(), any(), isNull()))
                .thenReturn(productoInactivo);

        mockMvc.perform(post("/productos/crear")
                        .param("nombre", "Costilla BBQ")
                        .param("descripcion", "Costilla de res")
                        .param("precio", "35000.0")
                        .param("tipo", "PLATOFUERTE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(productoService).crear(eq("Costilla BBQ"), eq("Costilla de res"),
                eq(35000.0), eq(TipoProducto.PLATOFUERTE), isNull());
    }

    // ── GET /productos/{id}/editar ────────────────────────────────────────────

    @Test
    @DisplayName("GET /productos/{id}/editar — producto existente retorna formulario")
    void formEditar_productoExistente_retornaVista() throws Exception {
        when(productoService.buscarPorId(1)).thenReturn(Optional.of(productoActivo));
        when(pedidoService.contarNotificaciones()).thenReturn(0L);

        mockMvc.perform(get("/productos/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/form"))
                .andExpect(model().attribute("accion", "Editar"))
                .andExpect(model().attribute("producto", productoActivo));
    }

    @Test
    @DisplayName("GET /productos/{id}/editar — producto inexistente redirige a /productos")
    void formEditar_productoInexistente_redirige() throws Exception {
        when(productoService.buscarPorId(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/productos/999/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));
    }

    // ── POST /productos/{id}/editar ───────────────────────────────────────────

    @Test
    @DisplayName("POST /productos/{id}/editar — edición exitosa redirige a /productos")
    void editar_exitoso_redirige() throws Exception {
        when(productoService.actualizar(anyInt(), anyString(), anyString(),
                anyDouble(), any(), any())).thenReturn(Optional.of(productoActivo));

        mockMvc.perform(post("/productos/1/editar")
                        .param("nombre", "Bandeja Paisa")
                        .param("descripcion", "Actualizada")
                        .param("precio", "33000.0")
                        .param("tipo", "PLATOFUERTE")
                        .param("disponible", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(productoService, times(1)).actualizar(eq(1), eq("Bandeja Paisa"),
                eq("Actualizada"), eq(33000.0), eq(TipoProducto.PLATOFUERTE), eq(true));
    }

    @Test
    @DisplayName("POST /productos/{id}/editar — descripcion null se convierte a cadena vacía")
    void editar_descripcionNull_seConvierteVacio() throws Exception {
        when(productoService.actualizar(anyInt(), anyString(), eq(""),
                anyDouble(), any(), any())).thenReturn(Optional.of(productoActivo));

        mockMvc.perform(post("/productos/1/editar")
                        .param("nombre", "Bandeja Paisa")
                        .param("precio", "32000.0")
                        .param("tipo", "PLATOFUERTE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(productoService).actualizar(eq(1), eq("Bandeja Paisa"), eq(""),
                eq(32000.0), eq(TipoProducto.PLATOFUERTE), isNull());
    }

    // ── POST /productos/{id}/eliminar ─────────────────────────────────────────

    @Test
    @DisplayName("POST /productos/{id}/eliminar — elimina y redirige a /productos")
    void eliminar_redirige() throws Exception {
        doNothing().when(productoService).eliminar(1);

        mockMvc.perform(post("/productos/1/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(productoService, times(1)).eliminar(1);
    }

    @Test
    @DisplayName("POST /productos/{id}/eliminar — id inexistente redirige sin explotar")
    void eliminar_idInexistente_redirigeSinError() throws Exception {
        doNothing().when(productoService).eliminar(999);

        mockMvc.perform(post("/productos/999/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"));

        verify(productoService, times(1)).eliminar(999);
    }
}