package resol.NizS;

import resol.NizS.controlador.VentaController;
import resol.NizS.controlador.VideojuegoController;
import resol.NizS.dao.VentaDao;
import resol.NizS.dao.VentaDaoImpl;
import resol.NizS.dao.VideojuegoDao;
import resol.NizS.dao.VideojuegoDaoImpl;
import resol.NizS.excepcion.ReglaNegocioException;
import resol.NizS.excepcion.VentaInvalidaException;
import resol.NizS.excepcion.VideojuegoNoEncontradoException;
import resol.NizS.modelo.Videojuego;
import resol.NizS.vista.PrincipalView;
import resol.NizS.vista.VentaVista;
import resol.NizS.vista.VideojuegoVista;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // El controlador depende de las interfaces DAO, no de las implementaciones.
        VideojuegoDao videojuegoDao = new VideojuegoDaoImpl();
        VentaDao ventaDao = new VentaDaoImpl();

        VideojuegoController videojuegoController =
                new VideojuegoController(videojuegoDao);
        VentaController ventaController =
                new VentaController(ventaDao, videojuegoDao);

        PrincipalView principalView = new PrincipalView(scanner);
        VideojuegoVista videojuegoVista = new VideojuegoVista(scanner);
        VentaVista ventaVista = new VentaVista(scanner);

        boolean salir = false;

        while (!salir) {
            try {
                switch (principalView.mostrarMenuPrincipal()) {
                    case 1 -> menuVideojuegos(videojuegoController, videojuegoVista);
                    case 2 -> menuVentas(ventaController, ventaVista);
                    case 0 -> salir = true;
                    default -> System.out.println("Opción inválida.");
                }
            } catch (SQLException e) {
                System.out.println("Error JDBC: " + e.getMessage());
            } catch (VideojuegoNoEncontradoException |
                     VentaInvalidaException |
                     ReglaNegocioException e) {
                System.out.println("Error de negocio: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Programa finalizado.");
    }

    private static void menuVideojuegos(VideojuegoController c, VideojuegoVista v)
            throws SQLException, VideojuegoNoEncontradoException, ReglaNegocioException {

        boolean volver = false;
        while (!volver) {
            try {
                switch (v.mostrarMenuVideojuegos()) {
                    case 1 -> v.mostrarVideojuegos(c.listarVideojuegos());
                    case 2 -> v.mostrarVideojuegos(
                            java.util.List.of(c.buscarPorId(v.pedirId())));
                    case 3 -> {
                        long id = c.agregarVideojuego(v.pedirNuevoVideojuego());
                        v.mensaje("Videojuego agregado correctamente. ID: " + id);
                    }
                    case 4 -> {
                        long id = v.pedirId();
                        Videojuego actual = c.obtenerEntidadPorId(id);
                        c.actualizarVideojuego(
                                v.pedirVideojuegoActualizado(id, actual));
                        v.mensaje("Videojuego actualizado correctamente.");
                    }
                    case 5 -> {
                        long id = v.pedirId();
                        if (v.confirmar("¿Eliminar el videojuego " + id + "?")) {
                            c.eliminarVideojuego(id);
                            v.mensaje("Videojuego eliminado correctamente.");
                        }
                    }
                    case 6 -> v.mostrarVideojuegos(c.listarQueNecesitanReposicion());
                    case 7 -> v.mostrarVideojuegos(c.listarDisponibles());
                    case 0 -> volver = true;
                    default -> v.mensaje("Opción inválida.");
                }
            } catch (SQLException | VideojuegoNoEncontradoException |
                     ReglaNegocioException e) {
                v.mensaje("Error: " + e.getMessage());
            }
        }
    }

    private static void menuVentas(VentaController c, VentaVista v)
            throws SQLException, VentaInvalidaException, ReglaNegocioException {

        boolean volver = false;
        while (!volver) {
            try {
                switch (v.mostrarMenuVentas()) {
                    case 1 -> v.mostrarVentas(c.listarVentas());
                    case 2 -> v.mostrarVentas(
                            java.util.List.of(c.buscarPorId(v.pedirIdVenta())));
                    case 3 -> {
                        long idVideojuego = v.pedirIdVideojuego();
                        int cantidad = v.pedirCantidad();
                        long id = c.registrarVenta(
                                LocalDate.now(), idVideojuego, cantidad);
                        v.mensaje("Venta registrada correctamente. ID: " + id);
                    }
                    case 4 -> v.mostrarVentas(
                            c.listarPorVideojuego(v.pedirIdVideojuego()));
                    case 5 -> v.mostrarVentas(c.reporteVentasDelMes());
                    case 0 -> volver = true;
                    default -> v.mensaje("Opción inválida.");
                }
            } catch (SQLException | VentaInvalidaException |
                     ReglaNegocioException e) {
                v.mensaje("Error: " + e.getMessage());
            }
        }
    }
}
