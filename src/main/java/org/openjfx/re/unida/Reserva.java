package org.openjfx.re.unida;

/**
 * Modelo de datos para una reservación de sala.
 * Diseñado con getters estándar para enlazarse directamente con TableView y TableColumn.
 *
 * @author rub3n
 */
public class Reserva {

    private String fecha;
    private String hora;
    private String duracion;
    private String sala;
    private String anfitrion;
    private String empresa;
    private String servicios;
    private String requerimientos;
    private String estado;

    public Reserva(String fecha, String hora, String duracion, String sala,
                   String anfitrion, String empresa, String servicios,
                   String requerimientos, String estado) {
        this.fecha = fecha;
        this.hora = hora;
        this.duracion = duracion;
        this.sala = sala;
        this.anfitrion = anfitrion;
        this.empresa = empresa;
        this.servicios = servicios;
        this.requerimientos = requerimientos;
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public String getAnfitrion() {
        return anfitrion;
    }

    public void setAnfitrion(String anfitrion) {
        this.anfitrion = anfitrion;
    }

    public String getHost() {
        return anfitrion;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getServicios() {
        return servicios;
    }

    public void setServicios(String servicios) {
        this.servicios = servicios;
    }

    public String getRequerimientos() {
        return requerimientos;
    }

    public void setRequerimientos(String requerimientos) {
        this.requerimientos = requerimientos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
