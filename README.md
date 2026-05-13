# QuickServe 🍽️

Sistema de gestión de pedidos para restaurantes — proyecto académico de la **Universidad del Quindío**. Coordina en tiempo real el flujo mesero → cocina → entrega, sin base de datos (todo en memoria).

---

## Stack

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Web MVC + Thymeleaf | (incluidos en Boot) |
| Tomcat | embebido |
| Maven | 3.8+ |
| JUnit 5 + Spring Boot Test | (incluidos en Boot) |

---

## Ejecutar el proyecto

```bash
# Con Maven Wrapper (no requiere Maven instalado)
./mvnw spring-boot:run          # Linux/macOS
mvnw.cmd spring-boot:run        # Windows

# Con Maven global
mvn spring-boot:run

# Compilar JAR y ejecutar
mvn clean package
java -jar target/QuickServe-0.0.1-SNAPSHOT.jar
```

La app queda disponible en `http://localhost:8080`.

---

## Ejecutar tests

```bash
mvn test                        # todos los tests
mvn test -pl .                  # desde raíz del proyecto
```

El proyecto tiene un test de integración que verifica que el contexto de Spring cargue correctamente:

```java
@SpringBootTest
class QuickServeApplicationTests {
    @Test
    void contextLoads() { }
}
```

---

## Configuración (`application.properties`)

```properties
spring.application.name=la-mesa-sobria
server.port=8080
spring.thymeleaf.cache=false
spring.thymeleaf.encoding=UTF-8
logging.level.com.restaurante=DEBUG
```

---

## Estructura del proyecto

```
src/
├── main/java/co/edu/uniquindio/quickserve/
│   ├── QuickServeApplication.java
│   ├── controller/
│   │   ├── HomeController.java         # GET /
│   │   ├── PedidoController.java       # /pedidos/**
│   │   ├── CocinaController.java       # /cocina/**
│   │   ├── MeseroController.java       # /mesero/**
│   │   └── ProductoController.java     # /productos/**
│   ├── service/
│   │   ├── PedidoService.java
│   │   └── ProductoService.java
│   ├── repository/
│   │   ├── PedidoRepository.java
│   │   ├── MesaRepository.java
│   │   ├── MeseroRepository.java
│   │   └── ProductoRepository.java
│   ├── model/
│   │   ├── Pedido.java
│   │   ├── Mesa.java
│   │   ├── Mesero.java
│   │   ├── Producto.java
│   │   ├── DetallePedido.java
│   │   └── enums/
│   │       ├── EstadoPedido.java       # CREADO · PREPARACION · LISTO · ENTREGADO
│   │       └── TipoProducto.java      # ENTRADA · PLATOFUERTE · POSTRE · BEBIDA
│   ├── dto/
│   │   ├── PedidoDTO.java
│   │   └── ProductoDTO.java
│   └── dataInitializer/
│       └── DataInitializer.java        # carga datos al arrancar
├── main/resources/
│   ├── application.properties
│   ├── static/css/style.css
│   └── templates/
│       ├── home.html
│       ├── layout.html
│       ├── cocina/panel.html
│       ├── mesero/panel.html
│       ├── pedidos/{registrar,listar,detalle,editar,confirmado}.html
│       └── productos/{form,listar}.html
└── test/java/co/edu/uniquindio/quickserve/
    └── QuickServeApplicationTests.java
```

---

## Arquitectura

Tres capas clásicas de Spring MVC. Inyección siempre por constructor, sin `@Autowired` en campos.

```
HTTP → Controller → Service → Repository → Model (en memoria)
```

Los repositorios guardan los objetos en `ArrayList`. Como Java pasa objetos por referencia, modificar un campo (`mesa.setOcupada(true)`) lo aplica directamente en la lista sin necesidad de llamar a `save` de nuevo. El método `save` solo es necesario para el insert inicial o cuando se reemplaza un pedido.

---

## Modelo de dominio

### `Mesa`
| Campo | Tipo | Notas |
|---|---|---|
| `numero` | `Integer` | Identificador (1–10) |
| `ocupada` | `Boolean` | `true` = comensales presentes |

### `Mesero`
| Campo | Tipo |
|---|---|
| `cedula` | `String` |
| `nombre` | `String` |
| `usuario` | `String` |
| `contrasena` | `String` |

### `Producto`
| Campo | Tipo | Notas |
|---|---|---|
| `id` | `Integer` | |
| `nombre` | `String` | |
| `descripcion` | `String` | |
| `precio` | `Double` | |
| `disponible` | `Boolean` | `false` = no aparece en formulario de pedido |
| `tipo` | `TipoProducto` | |

### `Pedido`
| Campo | Tipo | Notas |
|---|---|---|
| `id` | `Integer` | Auto-asignado desde 13 |
| `mesa` | `Mesa` | |
| `mesero` | `Mesero` | |
| `estado` | `EstadoPedido` | |
| `horaPedido` | `LocalDateTime` | |
| `horaEntrega` | `LocalDateTime` | `null` hasta ENTREGADO |
| `observaciones` | `String` | |
| `detalles` | `List<DetallePedido>` | |
| `notificacionPendiente` | `Boolean` | `true` cuando cocina marca LISTO |

Métodos calculados: `getTotal()`, `getTotalFormateado()`, `getHoraPedidoFormateada()`, `getHoraEntregaFormateada()`, `getEstadoCss()`.

### `DetallePedido`
| Campo | Tipo |
|---|---|
| `pedido` | `Pedido` |
| `producto` | `Producto` |
| `cantidad` | `Integer` |

Métodos calculados: `getSubtotal()`, `getSubtotalFormateado()`.

---

## Enums

### `EstadoPedido`
| Valor | Label | Significado |
|---|---|---|
| `CREADO` | "Creado" | Registrado, esperando cocina |
| `PREPARACION` | "En Preparación" | Cocina trabajando |
| `LISTO` | "Listo" | Listo para recoger, notifica al mesero |
| `ENTREGADO` | "Entregado" | Llevado a la mesa — **no libera la mesa automáticamente** |

### `TipoProducto`
`ENTRADA` · `PLATOFUERTE` · `POSTRE` · `BEBIDA`

---

## Rutas HTTP

### `HomeController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Dashboard: mesas, pedidos activos, estadísticas |

### `PedidoController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/pedidos` | Lista pedidos. `?estado=LISTO` para filtrar |
| GET | `/pedidos/{id}` | Detalle |
| GET/POST | `/pedidos/registrar` | Crear pedido. `?mesa=3` preselecciona |
| GET | `/pedidos/confirmado/{id}` | Confirmación tras crear |
| GET/POST | `/pedidos/{id}/editar` | Editar |
| POST | `/pedidos/{id}/eliminar` | Eliminar |
| POST | `/pedidos/{id}/estado` | Cambiar estado. Params: `estado`, `next` |
| POST | `/pedidos/{id}/notificacion/limpiar` | Descartar notificación. Param: `next` |

### `CocinaController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/cocina` | Panel con pedidos CREADO y PREPARACION |
| POST | `/cocina/{id}/preparacion` | → PREPARACION |
| POST | `/cocina/{id}/listo` | → LISTO + activa notificación |

### `MeseroController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/mesero` | Panel con LISTOS, CREADOS y mesas a desocupar |
| POST | `/mesero/{id}/entregar` | → ENTREGADO |
| POST | `/mesero/{id}/notificacion/limpiar` | Descartar notificación |
| POST | `/mesero/mesas/{numero}/desocupar` | Liberar mesa manualmente |

### `ProductoController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/productos` | Lista productos |
| GET/POST | `/productos/nuevo` | Crear |
| GET/POST | `/productos/{id}/editar` | Editar |
| POST | `/productos/{id}/eliminar` | Eliminar |

---

## Flujo de un pedido

```
MESERO registra        → estado: CREADO      · mesa.ocupada = true
COCINA toma            → estado: PREPARACION
COCINA termina         → estado: LISTO        · notificacionPendiente = true
MESERO entrega         → estado: ENTREGADO    · horaEntrega = now()  · mesa SIGUE ocupada
MESERO desocupa mesa   → mesa.ocupada = false  (acción manual, clientes ya se fueron)
```

---

## Datos de prueba

`DataInitializer` carga al arrancar: **meseros → mesas → productos → pedidos**.

### Meseros
| Cédula | Nombre | Usuario | Contraseña |
|---|---|---|---|
| 1001 | Carlos Ramírez | carlos | mesa123 |
| 1002 | Laura Gómez | laura | mesa123 |
| 1003 | Andrés Patiño | andres | mesa123 |

### Mesas
10 mesas (1–10), todas libres al arrancar.

### Productos
| ID | Nombre | Tipo | Precio | Disponible |
|---|---|---|---|---|
| 1 | Arepa de Chócolo | ENTRADA | $8.000 | ✓ |
| 2 | Empanadas x3 | ENTRADA | $9.500 | ✓ |
| 3 | Patacones con hogao | ENTRADA | $7.000 | ✓ |
| 4 | Bandeja Paisa | PLATOFUERTE | $32.000 | ✓ |
| 5 | Trucha al Ajillo | PLATOFUERTE | $28.000 | ✓ |
| 6 | Pollo a la Plancha | PLATOFUERTE | $22.000 | ✓ |
| 7 | Cazuela de Mariscos | PLATOFUERTE | $38.000 | ✓ |
| 8 | Costilla BBQ | PLATOFUERTE | $35.000 | ✗ |
| 9 | Tres Leches | POSTRE | $9.000 | ✓ |
| 10 | Brownie con Helado | POSTRE | $11.000 | ✓ |
| 11 | Flan de Caramelo | POSTRE | $8.500 | ✓ |
| 12 | Limonada de Coco | BEBIDA | $8.000 | ✓ |
| 13 | Jugo de Lulo | BEBIDA | $6.000 | ✓ |
| 14 | Cerveza Artesanal | BEBIDA | $10.000 | ✓ |
| 15 | Agua Mineral | BEBIDA | $3.000 | ✓ |

### Pedidos semilla (IDs 5–12, horarios relativos a -3h del arranque)
| ID | Mesa | Mesero | Estado | Mesa al arrancar |
|---|---|---|---|---|
| 5 | 7 | Andrés | ENTREGADO | Libre |
| 3 | 2 | Laura | ENTREGADO | Libre |
| 4 | 5 | Carlos | ENTREGADO | Libre |
| 7 | 7 | Carlos | LISTO | Ocupada |
| 6 | 3 | Andrés | PREPARACION | Ocupada |
| 8 | 1 | Laura | CREADO | Ocupada |
| 9 | 9 | Carlos | PREPARACION | Ocupada |
| 10 | 6 | Laura | LISTO | Ocupada |
| 11 | 8 | Carlos | CREADO | Ocupada |
| 12 | 10 | Andrés | PREPARACION | Ocupada |

Los IDs 1–12 están reservados para semillas. Pedidos nuevos en ejecución reciben IDs desde el **13**. Productos nuevos, desde el **16**.

---

## Convenciones

**Nomenclatura:** clases en `PascalCase`, métodos y variables en `camelCase`, enums en `UPPER_SNAKE_CASE`, paquetes en `lowercase`.

**Repositorios:** siguen la convención de Spring Data (`findAll`, `findById`, `findByXxx`, `save`, `deleteById`) aunque no usan JPA. Todos los `findAll()` retornan `Collections.unmodifiableList`.

**Rutas:** HTML no soporta `DELETE`/`PUT` nativos, por lo que todas las acciones destructivas y de negocio van por `POST`. Los redirects post-acción siguen el patrón `redirect:/ruta`.

**Thymeleaf:** formularios siempre con `method="post"` y `th:action`. Los paneles de cocina y mesero tienen `<meta http-equiv="refresh" content="20"/>` para auto-recarga cada 20 segundos.
