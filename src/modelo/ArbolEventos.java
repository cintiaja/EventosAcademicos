package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArbolEventos {

    public enum Criterio { NOMBRE, FECHA }
    private static class Nodo {
        Evento dato;
        Nodo izquierdo;
        Nodo derecho;

        Nodo(Evento dato) {
            this.dato = dato;
        }
    }

    private Nodo raiz;
    private final Criterio criterio;
    private int tamanio;

    public ArbolEventos(Criterio criterio) {
        this.criterio = criterio;
    }

    public Criterio getCriterio() { return criterio; }
    public int size() { return tamanio; }
    public boolean estaVacio() { return raiz == null; }


    public void limpiar() {
        raiz = null;
        tamanio = 0;
    }


    public void insertar(Evento evento) {
        raiz = insertar(raiz, evento);
        tamanio++;
    }

    private Nodo insertar(Nodo actual, Evento evento) {
        if (actual == null) {
            return new Nodo(evento);          // encontró un hueco: aquí va el nuevo
        }
        if (compararEventos(evento, actual.dato) < 0) {
            actual.izquierdo = insertar(actual.izquierdo, evento);
        } else {
            actual.derecho = insertar(actual.derecho, evento);
        }
        return actual;
    }

    public List<Evento> buscar(Object clave) {
        List<Evento> encontrados = new ArrayList<>();
        buscar(raiz, clave, encontrados);
        return encontrados;
    }

    private void buscar(Nodo actual, Object clave, List<Evento> encontrados) {
        if (actual == null) {
            return;
        }
        int resultado = compararConClave(clave, actual.dato);
        if (resultado == 0) {
            encontrados.add(actual.dato);
            buscar(actual.derecho, clave, encontrados); // los iguales pueden estar a la derecha
        } else if (resultado < 0) {
            buscar(actual.izquierdo, clave, encontrados);
        } else {
            buscar(actual.derecho, clave, encontrados);
        }
    }

    public List<Evento> inorden() {
        List<Evento> lista = new ArrayList<>();
        inorden(raiz, lista);
        return lista;
    }

    private void inorden(Nodo nodo, List<Evento> lista) {
        if (nodo == null) return;
        inorden(nodo.izquierdo, lista);
        lista.add(nodo.dato);
        inorden(nodo.derecho, lista);
    }

    public List<Evento> preorden() {
        List<Evento> lista = new ArrayList<>();
        preorden(raiz, lista);
        return lista;
    }

    private void preorden(Nodo nodo, List<Evento> lista) {
        if (nodo == null) return;
        lista.add(nodo.dato);
        preorden(nodo.izquierdo, lista);
        preorden(nodo.derecho, lista);
    }


    public List<Evento> postorden() {
        List<Evento> lista = new ArrayList<>();
        postorden(raiz, lista);
        return lista;
    }

    private void postorden(Nodo nodo, List<Evento> lista) {
        if (nodo == null) return;
        postorden(nodo.izquierdo, lista);
        postorden(nodo.derecho, lista);
        lista.add(nodo.dato);
    }

    public int altura() {
        return altura(raiz);
    }

    private int altura(Nodo nodo) {
        if (nodo == null) return 0;
        return 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
    }

    private int compararEventos(Evento a, Evento b) {
        if (criterio == Criterio.NOMBRE) {
            return a.getNombre().compareToIgnoreCase(b.getNombre());
        }
        return a.getFecha().compareTo(b.getFecha());
    }

    private int compararConClave(Object clave, Evento evento) {
        if (criterio == Criterio.NOMBRE) {
            return ((String) clave).compareToIgnoreCase(evento.getNombre());
        }
        return ((LocalDate) clave).compareTo(evento.getFecha());
    }
}
