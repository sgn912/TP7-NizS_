package resol.NizS.controlador;

import resol.NizS.dao.VideojuegoDao;
import resol.NizS.dto.VideojuegoDto;
import resol.NizS.excepcion.ReglaNegocioException;
import resol.NizS.excepcion.VideojuegoNoEncontradoException;
import resol.NizS.mapper.VideojuegoMapper;
import resol.NizS.modelo.Videojuego;

import java.sql.SQLException;
import java.util.List;

public class VideojuegoController {
    private final VideojuegoDao videojuegoDao;

    public VideojuegoController(VideojuegoDao videojuegoDao) {
        this.videojuegoDao = videojuegoDao;
    }

    public List<VideojuegoDto> listarVideojuegos() throws SQLException {
        return videojuegoDao.listarVideojuegos()
                .stream().map(VideojuegoMapper::toDto).toList();
    }

    public Videojuego obtenerEntidadPorId(long id)
            throws SQLException, VideojuegoNoEncontradoException {
        Videojuego v = videojuegoDao.obtenerPorId(id);
        if (v == null)
            throw new VideojuegoNoEncontradoException("No existe el videojuego con ID " + id);
        return v;
    }

    public VideojuegoDto buscarPorId(long id)
            throws SQLException, VideojuegoNoEncontradoException {
        return VideojuegoMapper.toDto(obtenerEntidadPorId(id));
    }

    public long agregarVideojuego(Videojuego videojuego)
            throws SQLException, ReglaNegocioException {
        validar(videojuego);
        return videojuegoDao.agregarVideojuego(videojuego);
    }

    public void actualizarVideojuego(Videojuego videojuego)
            throws SQLException, ReglaNegocioException, VideojuegoNoEncontradoException {
        if (videojuego == null || videojuego.getId() == null)
            throw new VideojuegoNoEncontradoException("ID de videojuego inválido.");
        obtenerEntidadPorId(videojuego.getId());
        validar(videojuego);
        if (!videojuegoDao.actualizarVideojuego(videojuego))
            throw new SQLException("No se pudo actualizar el videojuego.");
    }

    public void eliminarVideojuego(long id)
            throws SQLException, VideojuegoNoEncontradoException {
        obtenerEntidadPorId(id);
        if (!videojuegoDao.eliminarVideojuego(id))
            throw new SQLException("No se pudo eliminar el videojuego.");
    }

    public List<VideojuegoDto> listarDisponibles() throws SQLException {
        return videojuegoDao.listarDisponibles()
                .stream().map(VideojuegoMapper::toDto).toList();
    }

    public List<VideojuegoDto> listarQueNecesitanReposicion() throws SQLException {
        return videojuegoDao.listarQueNecesitanReposicion()
                .stream().map(VideojuegoMapper::toDto).toList();
    }

    private void validar(Videojuego v) throws ReglaNegocioException {
        if (v == null) throw new ReglaNegocioException("El videojuego no puede ser nulo.");
        if (v.getNombre() == null || v.getNombre().isBlank())
            throw new ReglaNegocioException("El nombre es obligatorio.");
        if (v.getPrecio() <= 0)
            throw new ReglaNegocioException("El precio debe ser mayor a 0.");
        if (v.getUnidadesDisponibles() < 0)
            throw new ReglaNegocioException("Las unidades disponibles no pueden ser negativas.");
        if (v.getNivelReposicion() < 0)
            throw new ReglaNegocioException("El nivel de reposición no puede ser negativo.");
        if (v.getSuspendido() != 0 && v.getSuspendido() != 1)
            throw new ReglaNegocioException("Suspendido debe ser 1 o 0.");
    }
}
