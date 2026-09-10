package resol.NizS.vista;

import resol.NizS.dto.VentaDto;

import java.util.List;
import java.util.Scanner;

public class VentaVista {
    private final Scanner scanner;

    public VentaVista(Scanner scanner) {
        this.scanner = scanner;
    }

    public int mostrarMenuVentas() {
        System.out.println("\n=== GESTIÓN DE VENTAS ===");
        System.out.println("1. Listar ventas");
        System.out.println("2. Buscar venta por ID");
        System.out.println("3. Registrar venta");
        System.out.println("4. Buscar ventas de un videojuego");
        System.out.println("5. Reporte de ventas del mes actual");
        System.out.println("0. Volver al menú principal");
        return leerEntero("Seleccione una opción: ");
    }

    public void mostrarVentas(List<VentaDto> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay ventas para mostrar.");
            return;
        }
        lista.forEach(System.out::println);
    }

    public long pedirIdVideojuego() { return leerLong("ID del videojuego: "); }
    public long pedirIdVenta() { return leerLong("ID de la venta: "); }
    public int pedirCantidad() { return leerEntero("Cantidad: "); }

    public void mensaje(String mensaje) { System.out.println(mensaje); }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un entero válido.");
            }
        }
    }

    private long leerLong(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un ID válido.");
            }
        }
    }
}
