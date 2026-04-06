package consola;

import Cafeteria.*;
import Servicios.*;
import inventario.*;
import juegos.*;
import persistencia.PeristenciaCafe;
import sistema.SistemaCafe;
import transacciones.*;
import usuarios.*;

import java.io.IOException;
import java.util.*;

/**
 * ConsolaPrueba — Programa de demostración para el Proyecto 1.
 *
 * Demuestra las siguientes funcionalidades:
 *   1. Registro de usuarios y login
 *   2. Registro de mesas y validación de capacidad
 *   3. Catálogo de juegos e inventario
 *   4. Préstamo de juegos (con todas las validaciones)
 *   5. Devolución de juegos
 *   6. Ventas de tienda con IVA y descuentos
 *   7. Ventas de cafetería con impuesto al consumo y propina
 *   8. Puntos de fidelidad
 *   9. Turnos de empleados
 *  10. Juegos difíciles y meseros capacitados
 *  11. Sugerencias del menú
 *  12. Reportes del administrador
 *  13. Operaciones del administrador (inventario, reparar, marcar robado)
 *  14. Persistencia: guardar → recargar → verificar
 */
public class ConsolaPrueba {

    // ── Constantes de ruta ────────────────────────────────────
    private static final String RUTA_DATOS = "datos_prueba/";

    // ── Contadores de pruebas ─────────────────────────────────
    private static int pruebas  = 0;
    private static int exitosas = 0;
    private static int fallidas = 0;

    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        titulo("CONSOLA DE PRUEBA — Board Game Café");
        titulo("Proyecto 1 · Diseño y Programación Orientada a Objetos");

        SistemaCafe sistema = new SistemaCafe(30); // capacidad 30 personas
        poblarSistema(sistema);

        seccion("1. LOGIN / AUTENTICACIÓN");
        probarLogin(sistema);

        seccion("2. MESAS Y CAPACIDAD");
        probarMesas(sistema);

        seccion("3. PRÉSTAMOS DE JUEGOS");
        probarPrestamos(sistema);

        seccion("4. VALIDACIONES DE PRÉSTAMO");
        probarValidacionesPrestamo(sistema);

        seccion("5. JUEGOS DIFÍCILES Y MESEROS CAPACITADOS");
        probarJuegosDificiles(sistema);

        seccion("6. VENTAS DE TIENDA (con IVA y descuentos)");
        probarVentasTienda(sistema);

        seccion("7. VENTAS DE CAFETERÍA (impuesto y propina)");
        probarVentasCafeteria(sistema);

        seccion("8. PUNTOS DE FIDELIDAD");
        probarPuntosFidelidad(sistema);

        seccion("9. TURNOS DE EMPLEADOS");
        probarTurnos(sistema);

        seccion("10. SUGERENCIAS DE MENÚ");
        probarSugerencias(sistema);

        seccion("11. OPERACIONES DE ADMINISTRADOR");
        probarAdmin(sistema);

        seccion("12. REPORTES");
        probarReportes(sistema);

        seccion("13. PERSISTENCIA — Guardar y recargar");
        probarPersistencia(sistema);

        resumenFinal();
    }

    // ══════════════════════════════════════════════════════════
    //  CONFIGURACIÓN INICIAL DEL SISTEMA
    // ══════════════════════════════════════════════════════════

    private static void poblarSistema(SistemaCafe sistema) {
        titulo("Inicializando sistema con datos de prueba...");

        // ── Juegos ────────────────────────────────────────────
        JuegoDeMesa uno       = new JuegoDeMesa("J1", "Uno",       2020, "Mattel",   2, 10, 0,  "Cartas",  false);
        JuegoDeMesa parques   = new JuegoDeMesa("J2", "Parqués",   1970, "Celupal",  2,  4, 0,  "Tablero", false);
        JuegoDeMesa twister   = new JuegoDeMesa("J3", "Twister",   1966, "Hasbro",   2,  6, 0,  "Accion",  false);
        JuegoDeMesa catan     = new JuegoDeMesa("J4", "Catan",     1995, "Catan Studio", 3, 4, 0, "Tablero", true);
        JuegoDeMesa ajedrez   = new JuegoDeMesa("J5", "Ajedrez",   1500, "Clásico",  2,  2, 0,  "Tablero", true);
        JuegoDeMesa domiNinos = new JuegoDeMesa("J6", "Domino Jr", 2010, "Hasbro",   2,  4, 5,  "Tablero", false);

        // ── Copias de préstamo ────────────────────────────────
        InventarioPrestamo invPrest = sistema.getServicioInventario().getInventarioPrestamo();
        invPrest.agregarCopia(new CopiaJuego("C1", uno,       "Nuevo", true));
        invPrest.agregarCopia(new CopiaJuego("C2", parques,   "Bueno", true));
        invPrest.agregarCopia(new CopiaJuego("C3", twister,   "Bueno", true));
        invPrest.agregarCopia(new CopiaJuego("C4", catan,     "Nuevo", true));
        invPrest.agregarCopia(new CopiaJuego("C5", ajedrez,   "Bueno", true));
        invPrest.agregarCopia(new CopiaJuego("C6", domiNinos, "Nuevo", true));

        // ── Inventario de venta ───────────────────────────────
        InventarioVenta invVenta = sistema.getServicioInventario().getInventarioVenta();
        invVenta.reabastecer(uno,     5, 35000);
        invVenta.reabastecer(parques, 3, 45000);
        invVenta.reabastecer(catan,   2, 180000);

        // ── Usuarios ──────────────────────────────────────────
        Cliente cli1 = new Cliente("U1", "maria", "1234", "María García",    "CL1");
        Cliente cli2 = new Cliente("U2", "pedro", "5678", "Pedro Martínez",  "CL2");

        Mesero  mes1 = new Mesero ("U3", "ana",   "pass", "Ana López",       "EMP-DESC-001");
        Mesero  mes2 = new Mesero ("U4", "luis",  "pass", "Luis Rodríguez",  "EMP-DESC-002");
        Cocinero coc = new Cocinero("U5","chef",  "pass", "Carlos Gómez",    "EMP-DESC-003");

        Administrador admin = new Administrador("U6", "admin", "admin123", "Admin Principal");

        // El mesero Ana conoce Catan y Ajedrez (juegos difíciles)
        mes1.aprenderJuego(catan);
        mes1.aprenderJuego(ajedrez);

        sistema.agregarUsuario(cli1);
        sistema.agregarUsuario(cli2);
        sistema.agregarUsuario(mes1);
        sistema.agregarUsuario(mes2);
        sistema.agregarUsuario(coc);
        sistema.agregarUsuario(admin);

        // ── Mesas ─────────────────────────────────────────────
        for (int i = 1; i <= 5; i++) {
            sistema.agregarMesa(new Mesa("M" + i, false, 0, 0, 0, new ArrayList<>(), null));
        }

        // ── Menú ──────────────────────────────────────────────
        ServicioCafeteria sc = sistema.getServicioCafeteria();
        sc.agregarAlMenu(new Bebida("A1", "Café Americano",  5000, false, true));
        sc.agregarAlMenu(new Bebida("A2", "Jugo Natural",    6000, false, false));
        sc.agregarAlMenu(new Bebida("A3", "Cerveza Artesanal", 10000, true, false));
        sc.agregarAlMenu(new Pasteleria("A4", "Brownie",     8000, Arrays.asList("gluten", "maní")));
        sc.agregarAlMenu(new Pasteleria("A5", "Macarrón",    7000, new ArrayList<>()));

        // ── Turnos ────────────────────────────────────────────
        ServicioTurnos st = sistema.getServicioTurnos();
        Turno t1 = st.crearTurno("T1", mes1, "Lunes");
        Turno t2 = st.crearTurno("T2", mes2, "Lunes");
        Turno t3 = st.crearTurno("T3", coc,  "Lunes");
        mes1.setEnTurno(true);
        mes2.setEnTurno(true);
        coc.setEnTurno(true);

        info("Sistema inicializado: 6 juegos · 6 copias · 6 usuarios · 5 mesas · 5 menú · 3 turnos");
    }

    // ══════════════════════════════════════════════════════════
    //  PRUEBAS
    // ══════════════════════════════════════════════════════════

    private static void probarLogin(SistemaCafe sistema) {
        verificar("Login correcto (maria/1234)",
                sistema.login("maria", "1234") != null);

        verificar("Login incorrecto (maria/wrong) → null",
                sistema.login("maria", "wrong") == null);

        verificar("Login admin correcto",
                sistema.login("admin", "admin123") instanceof Administrador);

        verificar("Login mesero correcto",
                sistema.login("ana", "pass") instanceof Mesero);
    }

    private static void probarMesas(SistemaCafe sistema) {
        Cliente maria = (Cliente) sistema.login("maria", "1234");
        Cliente pedro = (Cliente) sistema.login("pedro", "5678");

        // Mesa normal con 3 personas
        Mesa m = sistema.registrarMesa(3, 0, 0, maria);
        verificar("Reservar mesa para 3 personas", m != null);
        verificar("Cliente tiene mesa asignada", maria.tieneMesa());

        // Capacidad máxima: ya hay 3, intentamos meter 29 más (total 32 > 30)
        Mesa mRechazada = sistema.registrarMesa(29, 0, 0, pedro);
        verificar("Rechazar reserva que supera capacidad máxima (30)", mRechazada == null);

        // Pedro toma una mesa válida
        Mesa m2 = sistema.registrarMesa(4, 1, 1, pedro);
        verificar("Reservar mesa con niño y joven para Pedro", m2 != null);
        verificar("Pedro tiene niños en mesa", pedro.getMesaActual().hayNinos());
    }

    private static void probarPrestamos(SistemaCafe sistema) {
        Cliente maria = (Cliente) sistema.login("maria", "1234");
        Mesa mesaMaria = maria.getMesaActual();

        JuegoDeMesa uno    = buscarJuego(sistema, "J1");
        JuegoDeMesa parques = buscarJuego(sistema, "J2");
        JuegoDeMesa twister = buscarJuego(sistema, "J3");

        // Primer préstamo
        Prestamo p1 = sistema.prestarJuego(maria, uno, mesaMaria);
        verificar("Primer préstamo de Uno exitoso", p1 != null);
        verificar("Copia de Uno queda no disponible", !getCopiaJuego(sistema, "C1").isDisponible());

        // Segundo préstamo
        Prestamo p2 = sistema.prestarJuego(maria, parques, mesaMaria);
        verificar("Segundo préstamo de Parqués exitoso", p2 != null);
        verificar("Mesa tiene 2 préstamos activos", mesaMaria.cantidadJuegosPrestados() == 2);

        // Tercer préstamo (debe rechazarse — límite 2)
        Prestamo p3 = sistema.prestarJuego(maria, twister, mesaMaria);
        verificar("Tercer préstamo rechazado (límite 2 por mesa)", p3 == null);

        // Devolución
        sistema.devolverJuego(p1);
        verificar("Devolver Uno → copia disponible otra vez", getCopiaJuego(sistema, "C1").isDisponible());
        verificar("Préstamo p1 marcado como inactivo", !p1.isActivo());
    }

    private static void probarValidacionesPrestamo(SistemaCafe sistema) {
        // Pedro tiene niño (menores de 5) y joven en su mesa
        Cliente pedro = (Cliente) sistema.login("pedro", "5678");
        Mesa mesaPedro = pedro.getMesaActual();

        JuegoDeMesa domiNinos = buscarJuego(sistema, "J6"); // edadMinima=5 → apto para niños
        JuegoDeMesa twister   = buscarJuego(sistema, "J3"); // edadMinima=0, Acción
        JuegoDeMesa ajedrez   = buscarJuego(sistema, "J5"); // 2 jugadores exacto

        // Validación número de personas: ajedrez solo 2 jugadores, mesa tiene 4
        Prestamo pAjedrez = sistema.prestarJuego(pedro, ajedrez, mesaPedro);
        verificar("Ajedrez rechazado (solo 2 jugadores, mesa tiene 4)", pAjedrez == null);

        // Domino Jr. apto para niños (edadMinima=5)
        Prestamo pDomi = sistema.prestarJuego(pedro, domiNinos, mesaPedro);
        verificar("Domino Jr. permitido en mesa con niño (edadMin=5)", pDomi != null);

        // Administrador no puede pedir prestado
        Administrador admin = (Administrador) sistema.login("admin", "admin123");
        Prestamo pAdmin = sistema.prestarJuego(admin, twister, null);
        verificar("Administrador no puede pedir juegos prestados", pAdmin == null);

        // Cliente sin mesa no puede pedir prestado
        Cliente sinMesa = new Cliente("U99", "sinMesa", "x", "Sin Mesa", "CL99");
        Prestamo pSinMesa = sistema.prestarJuego(sinMesa, twister, null);
        verificar("Cliente sin mesa no puede pedir prestado", pSinMesa == null);
    }

    private static void probarJuegosDificiles(SistemaCafe sistema) {
        Cliente maria = (Cliente) sistema.login("maria", "1234");
        Mesa mesaMaria = maria.getMesaActual();

        JuegoDeMesa catan = buscarJuego(sistema, "J4");
        Mesero ana = (Mesero) sistema.login("ana", "pass");

        verificar("Ana conoce Catan", ana.conoceJuego(catan));
        verificar("Ana está en turno", ana.estaEnTurno());

        // Pedir Catan (difícil): debe advertir si no hay mesero, pero permitir
        // En este caso Ana está capacitada y en turno → no advertencia
        info("Solicitando Catan (juego difícil) con mesero capacitado disponible:");
        Prestamo pCatan = sistema.prestarJuego(maria, catan, mesaMaria);
        verificar("Catan prestado (mesero capacitado disponible)", pCatan != null);

        // Devolver para liberar
        if (pCatan != null) sistema.devolverJuego(pCatan);

        // Sacar a Ana del turno y volver a pedir → advertencia pero permite
        ana.setEnTurno(false);
        info("Solicitando Catan sin mesero capacitado en turno (debe advertir):");
        Prestamo pCatan2 = sistema.prestarJuego(maria, catan, mesaMaria);
        verificar("Catan prestado bajo advertencia (sin mesero)", pCatan2 != null);
        ana.setEnTurno(true);

        if (pCatan2 != null) sistema.devolverJuego(pCatan2);
    }

    private static void probarVentasTienda(SistemaCafe sistema) {
        Cliente maria  = (Cliente) sistema.login("maria", "1234");
        Mesero  ana    = (Mesero)  sistema.login("ana",   "pass");
        Administrador admin = (Administrador) sistema.login("admin", "admin123");

        JuegoDeMesa uno    = buscarJuego(sistema, "J1");
        JuegoDeMesa parques = buscarJuego(sistema, "J2");
        JuegoDeMesa catan  = buscarJuego(sistema, "J4");

        InventarioVenta inv = sistema.getServicioInventario().getInventarioVenta();

        // Venta normal a cliente
        List<ItemVenta> items1 = new ArrayList<>();
        items1.add(new ItemVenta(uno, 2, inv.getPrecio(uno), 0));
        Venta v1 = sistema.realizarVentaTienda(maria, items1);
        verificar("Venta de 2 Uno a cliente exitosa", v1 != null);
        if (v1 != null) {
            double esperado = 2 * 35000 * 1.19;
            verificar(String.format("Total con IVA 19%% correcto ($%.0f)", v1.getTotal()),
                    Math.abs(v1.getTotal() - esperado) < 1);
        }

        // Venta a empleado (20% descuento)
        List<ItemVenta> items2 = new ArrayList<>();
        items2.add(new ItemVenta(parques, 1, inv.getPrecio(parques), 0));
        Venta v2 = sistema.realizarVentaTienda(ana, items2);
        verificar("Venta a empleado con 20%% descuento exitosa", v2 != null);
        if (v2 != null) {
            double esperado = 45000 * 0.80 * 1.19;
            verificar(String.format("Descuento empleado aplicado (total $%.0f)", v2.getTotal()),
                    Math.abs(v2.getTotal() - esperado) < 1);
        }

        // Administrador no puede comprar
        List<ItemVenta> items3 = new ArrayList<>();
        items3.add(new ItemVenta(catan, 1, inv.getPrecio(catan), 0));
        Venta v3 = sistema.realizarVentaTienda(admin, items3);
        verificar("Administrador no puede comprar en tienda", v3 == null);
    }

    private static void probarVentasCafeteria(SistemaCafe sistema) {
        Cliente pedro = (Cliente) sistema.login("pedro", "5678");
        Mesa mesaPedro = pedro.getMesaActual();
        Administrador admin = (Administrador) sistema.login("admin", "admin123");

        ServicioCafeteria sc = sistema.getServicioCafeteria();
        Alimento jugo    = buscarAlimento(sc, "A2"); // Jugo, frío, no alcohólico
        Alimento cerveza = buscarAlimento(sc, "A3"); // Cerveza, alcohólica
        Alimento brownie = buscarAlimento(sc, "A4"); // Brownie con alérgenos

        // Venta normal (jugo + brownie con advertencia de alérgeno)
        List<ItemVenta> items = new ArrayList<>();
        items.add(new ItemVenta(jugo, 1, jugo.getPrecio(), 0));
        items.add(new ItemVenta(brownie, 2, brownie.getPrecio(), 0));
        info("Comprando brownie (tiene alérgenos gluten/maní — debe advertir):");
        Venta v = sistema.realizarVentaCafeteria(pedro, mesaPedro, items);
        verificar("Venta de cafetería con alérgenos permite (solo avisa)", v != null);
        if (v != null) {
            double subtotal = jugo.getPrecio() + 2 * brownie.getPrecio();
            double esperado = subtotal + subtotal * 0.08 + subtotal * 0.10;
            verificar(String.format("Total con impuesto 8%% + propina 10%% ($%.0f)", v.getTotal()),
                    Math.abs(v.getTotal() - esperado) < 1);
        }

        // Cerveza rechazada en mesa con menores
        List<ItemVenta> itemsCerveza = new ArrayList<>();
        itemsCerveza.add(new ItemVenta(cerveza, 1, cerveza.getPrecio(), 0));
        Venta vCerveza = sistema.realizarVentaCafeteria(pedro, mesaPedro, itemsCerveza);
        verificar("Cerveza rechazada en mesa con menor de edad", vCerveza == null || vCerveza.getItems().isEmpty());

        // Admin no puede ordenar
        Venta vAdmin = sistema.realizarVentaCafeteria(admin, null, items);
        verificar("Administrador no puede ordenar en cafetería", vAdmin == null);
    }

    private static void probarPuntosFidelidad(SistemaCafe sistema) {
        Cliente maria = (Cliente) sistema.login("maria", "1234");

        double puntos = maria.getPuntosFidelidad();
        verificar("María acumuló puntos tras sus compras (>0)", puntos > 0);
        info(String.format("  Puntos acumulados por María: %.2f pts = $%.0f de descuento", puntos, puntos));

        // Usar puntos
        double puntosAntes = maria.getPuntosFidelidad();
        boolean uso = maria.usarPuntos(puntosAntes / 2);
        verificar("Usar mitad de puntos exitoso", uso);
        verificar("Puntos reducidos correctamente", maria.getPuntosFidelidad() < puntosAntes);

        // Intentar usar más de los disponibles
        boolean exceso = maria.usarPuntos(99999);
        verificar("No se puede usar más puntos de los disponibles", !exceso);
    }

    private static void probarTurnos(SistemaCafe sistema) {
        Mesero  mes1  = (Mesero)  sistema.login("ana",  "pass");
        Mesero  mes2  = (Mesero)  sistema.login("luis", "pass");
        Cocinero coc  = (Cocinero) sistema.login("chef", "pass");
        ServicioTurnos st = sistema.getServicioTurnos();

        verificar("Ana está en turno el Lunes", mes1.getTurno() != null && "Lunes".equals(mes1.getTurno().getDiaSemana()));
        verificar("Personal mínimo en Lunes (≥1 cocinero, ≥2 meseros)", st.validarMinimosPersonal("Lunes"));

        // Solicitud de intercambio de turno
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(
                "SOL1", mes1, mes1.getTurno(), mes2, SolicitudCambioTurno.INTERCAMBIO);
        sistema.solicitarCambioTurno(solicitud);
        verificar("Solicitud registrada como PENDIENTE", SolicitudCambioTurno.PENDIENTE.equals(solicitud.getEstado()));

        // Aprobar
        sistema.aprobarCambio(solicitud);
        verificar("Solicitud aprobada por administrador", SolicitudCambioTurno.APROBADA.equals(solicitud.getEstado()));

        // Solicitud de cambio general (solamente 1 mesero quedaría → rechazar)
        mes2.setEnTurno(false); // simulamos que Luis ya no está
        SolicitudCambioTurno sol2 = new SolicitudCambioTurno(
                "SOL2", mes1, mes1.getTurno(), null, SolicitudCambioTurno.CAMBIO_GENERAL);
        boolean registrada = st.solicitarCambio(sol2);
        verificar("Solicitud rechazada al incumplir mínimos de personal", !registrada);
        mes2.setEnTurno(true);
    }

    private static void probarSugerencias(SistemaCafe sistema) {
        Mesero ana   = (Mesero) sistema.login("ana", "pass");
        Sugerencia sug = new Sugerencia("SG1", ana, "Cheesecake de Maracuyá", "PASTELERIA");
        sistema.agregarSugerencia(sug);
        verificar("Sugerencia registrada como PENDIENTE",
                Sugerencia.EstadoSugerencia.PENDIENTE == sug.getEstado());

        sistema.aprobarSugerencia(sug);
        verificar("Administrador aprueba sugerencia",
                Sugerencia.EstadoSugerencia.APROBADA == sug.getEstado());
    }

    private static void probarAdmin(SistemaCafe sistema) {
        JuegoDeMesa uno = buscarJuego(sistema, "J1");
        ServicioInventario si = sistema.getServicioInventario();

        // Mover juego de venta a préstamo
        int stockAntes = sistema.getServicioInventario().getInventarioVenta().getCantidad(uno);
        boolean movido = si.moverDeVentaAPrestamo(uno, "C10");
        verificar("Mover Uno de venta a préstamo", movido);
        int stockDespues = sistema.getServicioInventario().getInventarioVenta().getCantidad(uno);
        verificar("Stock de venta se reduce en 1", stockDespues == stockAntes - 1);

        // Marcar copia como robada
        CopiaJuego copiaUno = getCopiaJuego(sistema, "C1");
        si.marcarComoRobada(copiaUno);
        verificar("Copia marcada como Desaparecida", "Desaparecido".equals(copiaUno.getEstadoCopia()));
        verificar("Copia robada no disponible", !copiaUno.isDisponible());

        // Reparar (usar una copia del inventario de venta para reemplazar)
        CopiaJuego copiaParques = getCopiaJuego(sistema, "C2");
        si.actualizarEstadoCopia(copiaParques, "Falta una pieza");
        JuegoDeMesa parques = buscarJuego(sistema, "J2");
        boolean reparado = si.repararCopia(copiaParques, parques);
        verificar("Reparar copia de Parqués usando una del inventario de venta",
                reparado && "Nuevo".equals(copiaParques.getEstadoCopia()));

        // Ver historial de préstamos
        List<Prestamo> historial = sistema.getServicioPrestamo().getHistorialPrestamos();
        verificar("Historial de préstamos no vacío (admin puede verlo)", !historial.isEmpty());
        info("  Total préstamos registrados: " + historial.size());
    }

    private static void probarReportes(SistemaCafe sistema) {
        ServicioReportes sr = sistema.getServicioReportes();
        Date ahora = new Date();
        Date ayer  = new Date(ahora.getTime() - 86400000L);

        List<Venta> ventasJuego = sr.generarInformeVentas(Venta.TipoVenta.JUEGO, ayer, ahora);
        List<Venta> ventasCafe  = sr.generarInformeVentas(Venta.TipoVenta.CAFETERIA, ayer, ahora);

        verificar("Informe de ventas de juego no vacío", !ventasJuego.isEmpty());
        info(String.format("  Ventas tienda hoy:      %d venta(s)", ventasJuego.size()));
        info(String.format("  Subtotal:  $%.0f", sr.getTotalSubtotal(ventasJuego)));
        info(String.format("  Impuestos: $%.0f", sr.getTotalImpuestos(ventasJuego)));

        info(String.format("  Ventas cafetería hoy:   %d venta(s)", ventasCafe.size()));
        info(String.format("  Subtotal:  $%.0f", sr.getTotalSubtotal(ventasCafe)));
        info(String.format("  Propinas:  $%.0f", sr.getTotalPropinas(ventasCafe)));

        verificar("Informe de ventas de cafetería no vacío", !ventasCafe.isEmpty());
    }

    private static void probarPersistencia(SistemaCafe sistema) {
        // ── GUARDAR ───────────────────────────────────────────
        PeristenciaCafe persistencia = new PeristenciaCafe(RUTA_DATOS);
        try {
            persistencia.guardar(sistema);
            verificar("Sistema guardado en disco sin errores", true);
        } catch (IOException e) {
            verificar("Error al guardar: " + e.getMessage(), false);
            return;
        }

        // Capturar valores antes de recargar
        int ventasAntes   = sistema.getVentas().size();
        int usuariosAntes = sistema.getUsuarios().size();
        int mesasAntes    = sistema.getMesas().size();
        int prestAntes    = sistema.getServicioPrestamo().getHistorialPrestamos().size();
        Cliente maria     = (Cliente) sistema.login("maria", "1234");
        double puntosAntes = maria.getPuntosFidelidad();
        int copiasPrestAntes = sistema.getServicioInventario().getInventarioPrestamo().getCopias().size();

        // ── RECARGAR EN SISTEMA NUEVO ────────────────────────
        int cap = PeristenciaCafe.leerCapacidadMaxima(RUTA_DATOS);
        SistemaCafe sistemaRecargado = new SistemaCafe(cap);

        try {
            boolean habia = new PeristenciaCafe(RUTA_DATOS).cargar(sistemaRecargado);
            verificar("Datos encontrados y cargados desde disco", habia);
        } catch (IOException e) {
            verificar("Error al cargar: " + e.getMessage(), false);
            return;
        }

        // ── VERIFICAR INTEGRIDAD ─────────────────────────────
        verificar("Capacidad máxima preservada (" + cap + ")", cap == 30);

        verificar("Misma cantidad de usuarios tras recarga (" + usuariosAntes + ")",
                sistemaRecargado.getUsuarios().size() == usuariosAntes);

        verificar("Misma cantidad de mesas (" + mesasAntes + ")",
                sistemaRecargado.getMesas().size() == mesasAntes);

        verificar("Misma cantidad de ventas (" + ventasAntes + ")",
                sistemaRecargado.getVentas().size() == ventasAntes);

        verificar("Mismo historial de préstamos (" + prestAntes + ")",
                sistemaRecargado.getServicioPrestamo().getHistorialPrestamos().size() == prestAntes);

        verificar("Mismo número de copias en préstamo (" + copiasPrestAntes + ")",
                sistemaRecargado.getServicioInventario().getInventarioPrestamo().getCopias().size() == copiasPrestAntes);

        // Login funciona en sistema recargado
        Usuario mariaRecargada = sistemaRecargado.login("maria", "1234");
        verificar("Login de María funciona en sistema recargado", mariaRecargada instanceof Cliente);

        // Puntos de fidelidad preservados
        if (mariaRecargada instanceof Cliente) {
            double puntosRecargados = ((Cliente) mariaRecargada).getPuntosFidelidad();
            verificar(String.format("Puntos de fidelidad preservados (%.2f pts)", puntosAntes),
                    Math.abs(puntosRecargados - puntosAntes) < 0.01);
        }

        // Mesero conoce juegos
        Usuario anaRecargada = sistemaRecargado.login("ana", "pass");
        if (anaRecargada instanceof Mesero) {
            JuegoDeMesa catanRecargado = buscarJuego(sistemaRecargado, "J4");
            verificar("Mesero Ana sigue conociendo Catan tras recarga",
                    catanRecargado != null && ((Mesero) anaRecargada).conoceJuego(catanRecargado));
        }

        // Ventas de cafetería accesibles para reportes
        ServicioReportes srRecargado = sistemaRecargado.getServicioReportes();
        Date ahora = new Date();
        Date ayer  = new Date(ahora.getTime() - 86400000L);
        List<Venta> vCafeRecar = srRecargado.generarInformeVentas(Venta.TipoVenta.CAFETERIA, ayer, ahora);
        verificar("Reportes funcionan en sistema recargado", !vCafeRecar.isEmpty());
    }

    // ══════════════════════════════════════════════════════════
    //  UTILIDADES
    // ══════════════════════════════════════════════════════════

    private static JuegoDeMesa buscarJuego(SistemaCafe sistema, String id) {
        for (CopiaJuego c : sistema.getServicioInventario().getInventarioPrestamo().getCopias()) {
            if (c.getJuego().getIdJuego().equals(id)) return c.getJuego();
        }
        for (JuegoDeMesa j : sistema.getServicioInventario().getInventarioVenta().getJuegos()) {
            if (j.getIdJuego().equals(id)) return j;
        }
        return null;
    }

    private static CopiaJuego getCopiaJuego(SistemaCafe sistema, String idCopia) {
        for (CopiaJuego c : sistema.getServicioInventario().getInventarioPrestamo().getCopias()) {
            if (c.getIdCopia().equals(idCopia)) return c;
        }
        return null;
    }

    private static Alimento buscarAlimento(ServicioCafeteria sc, String id) {
        for (Alimento a : sc.getMenu()) {
            if (a.getIdProducto().equals(id)) return a;
        }
        return null;
    }

    // ══════════════════════════════════════════════════════════
    //  SALIDA POR CONSOLA
    // ══════════════════════════════════════════════════════════

    private static void verificar(String descripcion, boolean condicion) {
        pruebas++;
        if (condicion) {
            exitosas++;
            System.out.printf("  ✔  %s%n", descripcion);
        } else {
            fallidas++;
            System.out.printf("  ✘  [FALLO] %s%n", descripcion);
        }
    }

    private static void seccion(String titulo) {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────┐");
        System.out.printf( "│  %-51s│%n", titulo);
        System.out.println("└─────────────────────────────────────────────────────┘");
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf( "  %s%n", texto);
        System.out.println("══════════════════════════════════════════════════════");
    }

    private static void info(String texto) {
        System.out.println("  ℹ  " + texto);
    }

    private static void resumenFinal() {
        System.out.println();
        System.out.println("══════════════════════════════════════════════════════");
        System.out.println("  RESUMEN DE PRUEBAS");
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  Total:    %d pruebas%n", pruebas);
        System.out.printf("  Exitosas: %d  ✔%n", exitosas);
        System.out.printf("  Fallidas: %d  ✘%n", fallidas);
        System.out.println("──────────────────────────────────────────────────────");
        if (fallidas == 0) {
            System.out.println("  ✔  TODAS LAS PRUEBAS PASARON");
        } else {
            System.out.printf("  ✘  HAY %d PRUEBA(S) FALLIDA(S) — revisar salida arriba%n", fallidas);
        }
        System.out.println("══════════════════════════════════════════════════════");
    }
}
