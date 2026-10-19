package com.rutasur.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="experiencias")

public class Experiencia {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=120) private String nombre;
    @Column(nullable=false, length=80) private String destino;
    @Column(nullable=false, length=1000) private String descripcion;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal precio;
    @Column(nullable=false) private int capacidadDiaria;
    protected Experiencia() {
    }
    public Experiencia(String nombre, String destino, String descripcion, BigDecimal precio, int capacidadDiaria) {
        this.nombre=nombre;
        this.destino=destino;
        this.descripcion=descripcion;
        this.precio=precio;
        this.capacidadDiaria=capacidadDiaria;
    }
    public Long getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public String getDestino() {
        return destino;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public int getCapacidadDiaria() {
        return capacidadDiaria;
    }
}
