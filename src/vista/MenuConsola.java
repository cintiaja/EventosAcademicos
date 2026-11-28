package vista;

import controlador.GestorEventos;
import modelo.ArbolEventos.Criterio;
import modelo.Evento;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Menús de consola. Pide datos, muestra resultados y avisa los errores;
 * la lógica está en GestorEventos.
 */
public class MenuConsola {

    private final GestorEventos gestor = new GestorEventos();
    private final Scanner scanner = new Scanner(System.in);

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n===== GESTIÓN DE EVENTOS ACADÉMICOS =====");
            System.out.println("1. Administración de eventos");
            System.out.println("2. Administración de participantes");
            System.out.println("3. Calendario de eventos");
            System.out.println("0. Salir");
            opcion = leerEntero("Elige una opción: ", 0, 4);
            switch (opcion) {
                case 1: menuEventos(); break;
                case 2: menuParticipantes(); break;
                case 3: menuCalendario(); break;
                case 0: System.out.println("¡Hasta pronto!"); break;
            }
        } while (opcion != 0);
    }

    private void menuEventos() {//seccion 1
        System.out.println("\n--- ADMINISTRACIÓN DE EVENTOS ---");
        System.out.println("1. Registrar evento\n2. Consultar evento por clave\n3. Actualizar evento");
        System.out.println("4. Eliminar evento\n5. Mostrar todos los eventos\n0. Regresar");
        int opcion = leerEntero("Elige una opción: ", 0, 5);
        try {
            switch (opcion) {
                case 1:
                    Evento nuevo = gestor.registrarEvento(pedir("Clave: "), pedir("Nombre: "),
                            pedir("Fecha (dd/MM/aaaa): "), pedir("Responsable: "), pedir("Lugar: "));
                    System.out.println("Evento registrado:\n" + nuevo);
                    break;
                case 2:
                    System.out.println(gestor.consultarEvento(pedir("Clave del evento: ")));
                    break;
                case 3:
                    String clave = pedir("Clave del evento a actualizar: ");
                    System.out.println("Evento actual:\n" + gestor.consultarEvento(clave));
                    System.out.println("(Deja vacío lo que NO quieras cambiar)");
                    Evento actualizado = gestor.actualizarEvento(clave, pedir("Nuevo nombre: "),
                            pedir("Nueva fecha (dd/MM/aaaa): "), pedir("Nuevo responsable: "), pedir("Nuevo lugar: "));
                    System.out.println("Evento actualizado:\n" + actualizado);
                    break;
                case 4:
                    gestor.eliminarEvento(pedir("Clave del evento a eliminar: "));
                    System.out.println("Evento eliminado.");
                    break;
                case 5:
                    mostrarLista("TODOS LOS EVENTOS", gestor.obtenerTodos());
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void menuParticipantes() {//seccion 2
        System.out.println("\n--- ADMINISTRACIÓN DE PARTICIPANTES ---");
        System.out.println("1. Registrar participante\n2. Consultar si está inscrito");
        System.out.println("3. Mostrar listado de participantes\n0. Regresar");
        int opcion = leerEntero("Elige una opción: ", 0, 3);
        try {
            switch (opcion) {
                case 1:
                    String claveRegistro = pedir("Clave del evento: ");
                    if (gestor.registrarParticipante(claveRegistro, pedir("Nombre del participante: "))) {
                        System.out.println("Participante registrado.");
                    } else {
                        System.out.println("Ese participante ya estaba inscrito. No se registró de nuevo.");
                    }
                    break;
                case 2:
                    String claveConsulta = pedir("Clave del evento: ");
                    boolean inscrito = gestor.estaInscrito(claveConsulta, pedir("Nombre del participante: "));
                    System.out.println(inscrito ? "Sí, el participante ya está inscrito."
                                                : "No, el participante NO está inscrito.");
                    break;
                case 3:
                    Set<String> participantes = gestor.listarParticipantes(pedir("Clave del evento: "));
                    if (participantes.isEmpty()) {
                        System.out.println("Este evento todavía no tiene participantes.");
                    } else {
                        System.out.println("Participantes (" + participantes.size() + "):");
                        for (String nombre : participantes) {
                            System.out.println(" - " + nombre);
                        }
                    }
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void menuCalendario() {//seccion 3
        System.out.println("\n--- CALENDARIO DE EVENTOS ---");
        System.out.println("1. Registrar evento\n2. Mostrar eventos ordenados\n3. Buscar un evento");
        System.out.println("4. Recorrer el árbol\n0. Regresar");
        int opcion = leerEntero("Elige una opción: ", 0, 4);
        try {
            switch (opcion) {
                case 1:
                    Evento nuevo = gestor.registrarEvento(pedir("Clave: "), pedir("Nombre: "),
                            pedir("Fecha (dd/MM/aaaa): "), pedir("Responsable: "), pedir("Lugar: "));
                    System.out.println("Evento registrado:\n" + nuevo);
                    break;
                case 2:
                    Criterio criterio = elegirCriterio();
                    mostrarLista("EVENTOS ORDENADOS POR " + criterio, gestor.eventosOrdenados(criterio));
                    break;
                case 3:
                    Criterio criterioBusqueda = elegirCriterio();
                    String texto = criterioBusqueda == Criterio.NOMBRE
                            ? pedir("Nombre exacto del evento: ") : pedir("Fecha (dd/MM/aaaa): ");
                    List<Evento> encontrados = gestor.buscarEnCalendario(criterioBusqueda, texto);
                    if (encontrados.isEmpty()) {
                        System.out.println("No se encontró ningún evento con ese dato.");
                    } else {
                        mostrarLista("EVENTOS ENCONTRADOS", encontrados);
                    }
                    break;
                case 4:
                    Criterio criterioArbol = elegirCriterio();
                    System.out.println("Recorrido:\n1. Inorden (ordenado)\n2. Preorden\n3. Postorden");
                    int tipo = leerEntero("Elige el recorrido: ", 1, 3);
                    mostrarLista("RECORRIDO DEL ÁRBOL POR " + criterioArbol,
                            gestor.recorrerArbol(criterioArbol, tipo));
                    System.out.println("Altura del árbol: " + gestor.alturaArbol(criterioArbol));
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Criterio elegirCriterio() {
        System.out.println("Criterio:\n1. Nombre\n2. Fecha");
        return leerEntero("Elige el criterio: ", 1, 2) == 1 ? Criterio.NOMBRE : Criterio.FECHA;
    }

    private void mostrarLista(String titulo, List<Evento> eventos) {
        System.out.println("\n--- " + titulo + " ---");
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos registrados.");
            return;
        }
        for (Evento evento : eventos) {
            System.out.println(evento);
        }
    }

    private String pedir(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    private int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            try {
                int numero = Integer.parseInt(scanner.nextLine().trim());
                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
            } catch (NumberFormatException e) {

            }
            System.out.println("Entrada inválida. Escribe un número entre " + minimo + " y " + maximo + ".");
        }
    }
}
