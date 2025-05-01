package com.example.mapaanimalesperdidos.Reportes;

public class Reporte {
    public String nombre;
    public String descripcion;
    public String ubicacion;
    public String fotoUrl;
    public double latitud;
    public double longitud;
    public String id;

    public Reporte() {
        // Constructor vacío requerido por Firebase
    }

    public Reporte(String nombre, String descripcion, String ubicacion, String fotoUrl, double latitud, double longitud, String id) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.fotoUrl = fotoUrl;
        this.latitud = latitud;
        this.longitud = longitud;
        this.id = id;
    }

    @Override
    public String toString() {
        return nombre + "\n" + descripcion + "\nUbicación: " + ubicacion;
    }
}
