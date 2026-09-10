package resol.NizS.dao;

import resol.NizS.modelo.Videojuego;
import java.sql.SQLException;
import java.util.List;

public interface VideojuegoDao {
    List<Videojuego> listarVideojuegos() throws SQLException;
    List<Videojuego> listarDisponibles() throws SQLException;
    List<Videojuego> listarQueNecesitanReposicion() throws SQLException;
    long agregarVideojuego(Videojuego videojuego) throws SQLException;
    Videojuego obtenerPorId(long id) throws SQLException;
    boolean actualizarVideojuego(Videojuego videojuego) throws SQLException;
    boolean eliminarVideojuego(long id) throws SQLException;
}
