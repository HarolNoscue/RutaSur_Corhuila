package com.rutasur.model;

import jakarta.persistence.*;
import java.time.Instant;

@Embeddable
public class Valoracion {
    @Column(name="calificacion") private Integer puntuacion;
    @Column(name="comentario", length=500) private String comentario;
    @Column(name="valorada_en") private Instant creadaEn;
    protected Valoracion() {
    }
    public Valoracion(Integer puntuacion, String comentario, Instant creadaEn) {
        this.puntuacion=puntuacion;
        this.comentario=comentario;
        this.creadaEn=creadaEn;
    }
    public Integer getPuntuacion() {
        return puntuacion;
    }
    public String getComentario() {
        return comentario;
    }
    public Instant getCreadaEn() {
        return creadaEn;
    }
}
