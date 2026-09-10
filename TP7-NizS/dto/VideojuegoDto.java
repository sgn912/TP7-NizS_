package resol.NizS.dto;

public class VideojuegoDto {
    private Long id;
    private String nombre;
    private double precio;
    private boolean necesitaReposicion;

    public VideojuegoDto() {}

    public VideojuegoDto(Long id, String nombre, double precio, boolean necesitaReposicion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.necesitaReposicion = necesitaReposicion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public boolean isNecesitaReposicion() { return necesitaReposicion; }
    public void setNecesitaReposicion(boolean necesitaReposicion) { this.necesitaReposicion = necesitaReposicion; }

    @Override
    public String toString() {
        return "ID: " + id + " | Nombre: " + nombre +
                " | Precio: $" + String.format("%.2f", precio) +
                " | Necesita reposición: " + (necesitaReposicion ? "Sí" : "No");
    }
}
