package resol.NizS.mapper;

import resol.NizS.dto.VideojuegoDto;
import resol.NizS.modelo.Videojuego;

public final class VideojuegoMapper {
    private VideojuegoMapper() {}

    public static VideojuegoDto toDto(Videojuego v) {
        if (v == null) return null;
        return new VideojuegoDto(
                v.getId(),
                v.getNombre(),
                v.getPrecio(),
                v.necesitaReposicion()
        );
    }
}
