package com.example.ejerciciorepasoexamenfirebasecompra.modelos;

import java.text.NumberFormat;

public class Producto {
    private String nombre;
    private int cantidad;
    private float precio;
    private float total;

    private static final NumberFormat numberFormat;

    static {
        numberFormat = NumberFormat.getCurrencyInstance();
    }

    public Producto() {
    }

    public Producto(String nombre, int cantidad, float precio) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.total = this.cantidad * this.precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public void updateTotal(){
        this.total = this.cantidad * this.precio;
    }

    public String getPrecioMoneda(){
        return numberFormat.format(this.precio);
    }
}
