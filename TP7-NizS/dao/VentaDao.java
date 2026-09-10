package resol.NizS.dao;

import resol.NizS.modelo.Venta;
import java.sql.SQLException;
import java.util.List;

public interface VentaDao {
    long registrarVenta(Venta venta) throws SQLException;
    List<Venta> listarPorIdVideojuego(long idVideojuego) throws SQLException;
    List<Venta> listarVentas() throws SQLException;
    List<Venta> listarVentasDelMes() throws SQLException;
    Venta obtenerPorId(long id) throws SQLException;
}
