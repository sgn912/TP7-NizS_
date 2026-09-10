package resol.NizS.dao;

import resol.NizS.modelo.Venta;
import resol.NizS.modelo.Videojuego;
import resol.NizS.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements VentaDao {

    private static final String SELECT = """
            SELECT ve.id, ve.fecha, ve.videojuego_id, ve.cantidad, ve.descuento, ve.total,
                   v.nombre AS videojuego_nombre, v.precio,
                   v.unidadesDisponibles, v.nivelReposicion, v.suspendido
            FROM ventas ve
            JOIN videojuegos v ON v.id = ve.videojuego_id
            """;

    private Venta map(ResultSet rs) throws SQLException {
        Videojuego v = new Videojuego(
                rs.getLong("videojuego_id"),
                rs.getString("videojuego_nombre"),
                rs.getDouble("precio"),
                rs.getInt("unidadesDisponibles"),
                rs.getInt("nivelReposicion"),
                rs.getInt("suspendido")
        );
        return new Venta(
                rs.getLong("id"),
                rs.getDate("fecha").toLocalDate(),
                v,
                rs.getInt("cantidad"),
                rs.getDouble("descuento"),
                rs.getDouble("total")
        );
    }

    @Override
    public long registrarVenta(Venta venta) throws SQLException {
        String updateStock = """
                UPDATE videojuegos
                SET unidadesDisponibles = unidadesDisponibles - ?
                WHERE id = ? AND unidadesDisponibles >= ? AND suspendido = 1
                """;
        String insertVenta = """
                INSERT INTO ventas(fecha, videojuego_id, cantidad, descuento, total)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection cn = ConexionBD.obtenerConexion()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement(updateStock)) {
                    ps.setInt(1, venta.getCantidad());
                    ps.setLong(2, venta.getVideojuego().getId());
                    ps.setInt(3, venta.getCantidad());
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("No se pudo descontar el stock.");
                    }
                }

                long id;
                try (PreparedStatement ps = cn.prepareStatement(insertVenta, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setDate(1, Date.valueOf(venta.getFecha()));
                    ps.setLong(2, venta.getVideojuego().getId());
                    ps.setInt(3, venta.getCantidad());
                    ps.setDouble(4, venta.getDescuento());
                    ps.setDouble(5, venta.getTotal());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se obtuvo el ID de la venta.");
                        id = keys.getLong(1);
                    }
                }

                cn.commit();
                venta.setId(id);
                return id;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    @Override
    public List<Venta> listarPorIdVideojuego(long idVideojuego) throws SQLException {
        List<Venta> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(
                     SELECT + " WHERE ve.videojuego_id=? ORDER BY ve.fecha, ve.id")) {
            ps.setLong(1, idVideojuego);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Venta> listarVentas() throws SQLException {
        List<Venta> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SELECT + " ORDER BY ve.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    @Override
    public List<Venta> listarVentasDelMes() throws SQLException {
        List<Venta> lista = new ArrayList<>();
        String sql = SELECT + """
                WHERE YEAR(ve.fecha)=YEAR(CURRENT_DATE())
                  AND MONTH(ve.fecha)=MONTH(CURRENT_DATE())
                ORDER BY ve.fecha, ve.id
                """;
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    @Override
    public Venta obtenerPorId(long id) throws SQLException {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SELECT + " WHERE ve.id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }
}
