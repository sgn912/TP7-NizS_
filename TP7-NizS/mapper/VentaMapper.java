package resol.NizS.mapper;

import resol.NizS.dto.VentaDto;
import resol.NizS.modelo.Venta;

public final class VentaMapper {
    private VentaMapper() {}

    public static VentaDto toDto(Venta v) {
        if (v == null) return null;
        return new VentaDto(
                v.getId(),
                v.getFecha(),
                v.getVideojuego() == null ? "" : v.getVideojuego().getNombre(),
                v.getCantidad(),
                v.getDescuento(),
                v.getTotal()
        );
    }
}
