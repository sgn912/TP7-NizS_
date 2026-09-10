package resol.NizS.controlador;

import resol.NizS.dao.VentaDao;
import resol.NizS.dao.VideojuegoDao;
import resol.NizS.dto.VentaDto;
import resol.NizS.excepcion.ReglaNegocioException;
import resol.NizS.excepcion.VentaInvalidaException;
import resol.NizS.mapper.VentaMapper;
import resol.NizS.modelo.Venta;
import resol.NizS.modelo.Videojuego;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class VentaController {
    private final VentaDao ventaDao;
    private final VideojuegoDao videojuegoDao;

    public VentaController(VentaDao ventaDao, VideojuegoDao videojuegoDao) {
        this.ventaDao = ventaDao;
        this.videojuegoDao = videojuegoDao;
    }

    public List<VentaDto> listarVentas() throws SQLException {
        return ventaDao.listarVentas().stream().map(VentaMapper::toDto).toList();
    }

    public VentaDto buscarPorId(long id)
            throws SQLException, VentaInvalidaException {
        Venta venta = ventaDao.obtenerPorId(id);
        if (venta == null)
            throw new VentaInvalidaException("No existe la venta con ID " + id);
        return VentaMapper.toDto(venta);
    }

    public long registrarVenta(LocalDate fecha, long idVideojuego, int cantidad)
            throws SQLException, VentaInvalidaException, ReglaNegocioException {

        if (fecha == null)
            throw new VentaInvalidaException("La fecha es obligatoria.");
        if (fecha.isAfter(LocalDate.now()))
            throw new VentaInvalidaException("La fecha de venta no puede ser futura.");
        if (cantidad <= 0)
            throw new VentaInvalidaException("La cantidad debe ser mayor que cero.");

        Videojuego videojuego = videojuegoDao.obtenerPorId(idVideojuego);
        if (videojuego == null)
            throw new ReglaNegocioException("No existe el videojuego indicado.");
        if (videojuego.getSuspendido() != 1)
            throw new ReglaNegocioException("El videojuego no está disponible para la venta.");
        if (videojuego.getUnidadesDisponibles() < cantidad)
            throw new ReglaNegocioException(
                    "Stock insuficiente. Disponible: " + videojuego.getUnidadesDisponibles());

        Venta venta = new Venta(fecha, videojuego, cantidad);
        venta.calcularDescuentoYTotal();

        return ventaDao.registrarVenta(venta);
    }

    public List<VentaDto> listarPorVideojuego(long idVideojuego) throws SQLException {
        return ventaDao.listarPorIdVideojuego(idVideojuego)
                .stream().map(VentaMapper::toDto).toList();
    }

    public List<VentaDto> reporteVentasDelMes() throws SQLException {
        return ventaDao.listarVentasDelMes()
                .stream().map(VentaMapper::toDto).toList();
    }
}
