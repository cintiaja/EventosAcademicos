package controlador;

import modelo.ArbolEventos;
import modelo.ArbolEventos.Criterio;
import modelo.Evento;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lógica del sistema. Usa tres estructuras, cada una para lo que mejor sirve:
 *  - Diccionario (Map): eventos por clave (sección 1).
 *  - Conjunto (Set): participantes sin repetir, dentro de cada Evento (sección 2).
 *  - Árbol (BST): eventos ordenados por nombre y por fecha (sección 3).
 */
public class GestorEventos {

    // LinkedHashMap es un Map que además recuerda el orden en que se registraron
    private final Map<String, Evento> eventos = new LinkedHashMap<>();
    private final ArbolEventos arbolPorNombre = new ArbolEventos(Criterio.NOMBRE);
    private final ArbolEventos arbolPorFecha = new ArbolEventos(Criterio.FECHA);

    private static final DateTimeFormatter FORMATO_ENTRADA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    // ---------- Sección 1: Diccionario (Map) ----------

    public Evento registrarEvento(String clave, String nombre, String fechaTexto,
                                  String responsable, String lugar) {
        String claveNormalizada = normalizarClave(clave);
        if (eventos.containsKey(claveNormalizada)) {
            throw new IllegalArgumentException("Ya existe un evento con la clave " + claveNormalizada + ".");
        }
        validarTexto(nombre, "El nombre");
        validarTexto(responsable, "El responsable");
        validarTexto(lugar, "El lugar");
        Evento nuevo = new Evento(claveNormalizada, nombre.trim(), convertirFecha(fechaTexto),
                responsable.trim(), lugar.trim());
        eventos.put(claveNormalizada, nuevo);   // put: guarda el evento usando su clave
        arbolPorNombre.insertar(nuevo);         // y también entra a los dos árboles
        arbolPorFecha.insertar(nuevo);
        return nuevo;
    }

    public Evento consultarEvento(String clave) {
        Evento evento = eventos.get(normalizarClave(clave)); // get: búsqueda directa por clave
        if (evento == null) {
            throw new IllegalArgumentException("No existe un evento con la clave " + normalizarClave(clave) + ".");
        }
        return evento;
    }

    /** Actualiza solo los campos que no vengan vacíos; los vacíos se quedan como estaban. */
    public Evento actualizarEvento(String clave, String nombre, String fechaTexto,
                                   String responsable, String lugar) {
        Evento evento = consultarEvento(clave);
        if (!nombre.trim().isEmpty()) evento.setNombre(nombre.trim());
        if (!fechaTexto.trim().isEmpty()) evento.setFecha(convertirFecha(fechaTexto));
        if (!responsable.trim().isEmpty()) evento.setResponsable(responsable.trim());
        if (!lugar.trim().isEmpty()) evento.setLugar(lugar.trim());
        reconstruirArboles(); // el nombre o la fecha pudieron cambiar, así que se reordenan los árboles
        return evento;
    }

    public void eliminarEvento(String clave) {
        consultarEvento(clave); // lanza error si no existe
        eventos.remove(normalizarClave(clave));
        reconstruirArboles();
    }

    public List<Evento> obtenerTodos() {
        return new ArrayList<>(eventos.values());
    }

    public int cantidadEventos() {
        return eventos.size();
    }

    // ---------- Sección 2: Conjunto (Set) ----------

    /** Regresa true si se registró; false si ese participante ya estaba inscrito. */
    public boolean registrarParticipante(String claveEvento, String nombre) {
        validarTexto(nombre, "El nombre del participante");
        return consultarEvento(claveEvento).agregarParticipante(normalizarNombre(nombre));
    }

    public boolean estaInscrito(String claveEvento, String nombre) {
        validarTexto(nombre, "El nombre del participante");
        return consultarEvento(claveEvento).tieneParticipante(normalizarNombre(nombre));
    }

    public Set<String> listarParticipantes(String claveEvento) {
        return consultarEvento(claveEvento).getParticipantes();
    }

    // ---------- Sección 3: Árbol binario de búsqueda ----------

    private ArbolEventos arbolDe(Criterio criterio) {
        return criterio == Criterio.NOMBRE ? arbolPorNombre : arbolPorFecha;
    }

    public List<Evento> eventosOrdenados(Criterio criterio) {
        return arbolDe(criterio).inorden();
    }

    /** Busca en el árbol por nombre exacto o por fecha (dd/MM/aaaa). */
    public List<Evento> buscarEnCalendario(Criterio criterio, String texto) {
        if (criterio == Criterio.NOMBRE) {
            validarTexto(texto, "El nombre a buscar");
            return arbolDe(criterio).buscar(texto.trim());
        }
        return arbolDe(criterio).buscar(convertirFecha(texto));
    }

    /** tipo: 1 = inorden, 2 = preorden, 3 = postorden. */
    public List<Evento> recorrerArbol(Criterio criterio, int tipo) {
        ArbolEventos arbol = arbolDe(criterio);
        switch (tipo) {
            case 1: return arbol.inorden();
            case 2: return arbol.preorden();
            default: return arbol.postorden();
        }
    }

    public int alturaArbol(Criterio criterio) {
        return arbolDe(criterio).altura();
    }

    // ---------- Datos de ejemplo ----------

    public void cargarDatosEjemplo() {
        String[][] datos = {
            {"EV01", "Conferencia de Inteligencia Artificial", "12/10/2026", "Dra. Laura Méndez", "Auditorio Principal"},
            {"EV02", "Taller de Python", "05/10/2026", "Ing. Carlos Ruiz", "Laboratorio 3"},
            {"EV03", "Seminario de Ciberseguridad", "20/11/2026", "Mtro. Jorge Salas", "Sala de Usos Múltiples"},
            {"EV04", "Concurso de Programación", "03/12/2026", "Mtra. Ana Torres", "Centro de Cómputo"},
            {"EV05", "Taller de Robótica", "05/10/2026", "Ing. Diego Soto", "Lab. de Electrónica"},
            {"EV06", "Conferencia de Emprendimiento", "18/09/2026", "Lic. Sofía Luna", "Aula Magna"}
        };
        for (String[] fila : datos) {
            if (!eventos.containsKey(fila[0])) {
                registrarEvento(fila[0], fila[1], fila[2], fila[3], fila[4]);
            }
        }
        registrarParticipante("EV01", "Ana Torres");
        registrarParticipante("EV01", "Luis Pérez");
        registrarParticipante("EV01", "María López");
        registrarParticipante("EV02", "Pedro Ramírez");
    }

    // ---------- Apoyo ----------

    private void reconstruirArboles() {
        arbolPorNombre.limpiar();
        arbolPorFecha.limpiar();
        for (Evento evento : eventos.values()) {
            arbolPorNombre.insertar(evento);
            arbolPorFecha.insertar(evento);
        }
    }

    private String normalizarClave(String clave) {
        validarTexto(clave, "La clave");
        return clave.trim().toUpperCase();
    }

    /** "  ana   torres " -> "Ana Torres", así "ana torres" y "Ana Torres" cuentan como el mismo. */
    private String normalizarNombre(String texto) {
        String[] palabras = texto.trim().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            resultado.append(Character.toUpperCase(palabra.charAt(0)))
                     .append(palabra.substring(1).toLowerCase())
                     .append(' ');
        }
        return resultado.toString().trim();
    }

    private LocalDate convertirFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim(), FORMATO_ENTRADA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha inválida. Usa el formato dd/MM/aaaa, por ejemplo 15/12/2026.");
        }
    }

    private void validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
    }
}
