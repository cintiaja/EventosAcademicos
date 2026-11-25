package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa un evento académico (conferencia, taller, seminario o concurso).
 * Cada evento guarda su propio CONJUNTO (Set) de participantes.
 */
public class Evento {

    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String clave;
    private String nombre;
    private LocalDate fecha;
    private String responsable;
    private String lugar;
    // Set: no permite elementos repetidos, así se evitan participantes duplicados
    private final Set<String> participantes = new HashSet<>();

    public Evento(String clave, String nombre, LocalDate fecha, String responsable, String lugar) {
        this.clave = clave;
        this.nombre = nombre;
        this.fecha = fecha;
        this.responsable = responsable;
        this.lugar = lugar;
    }

    public String getClave() { return clave; }
    public String getNombre() { return nombre; }
    public LocalDate getFecha() { return fecha; }
    public String getResponsable() { return responsable; }
    public String getLugar() { return lugar; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    /** Regresa true si se agregó; false si ya estaba inscrito (el Set lo rechaza). */
    public boolean agregarParticipante(String nombreParticipante) {
        return participantes.add(nombreParticipante);
    }

    public boolean tieneParticipante(String nombreParticipante) {
        return participantes.contains(nombreParticipante);
    }

    /** Regresa una copia para que nadie modifique el conjunto original desde afuera. */
    public Set<String> getParticipantes() {
        return new HashSet<>(participantes);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-34s | %s | Resp: %-16s | Lugar: %-22s | Participantes: %d",
                clave, nombre, fecha.format(FORMATO_FECHA), responsable, lugar, participantes.size());
    }
}
