package com.rutasur.model;

import jakarta.persistence.*;
import java.time.*;
import java.util.UUID;
import java.math.BigDecimal;

@Entity @Table(name="reservas", indexes= {
    @Index(name="idx_reserva_inventario", columnList="experiencia_id,fecha,estado")
}
, uniqueConstraints= {
    @UniqueConstraint(name="uq_reserva_clave", columnNames="clave_idempotencia")
}
) public class Reserva {
    @Id @Column(nullable=false) private UUID id;
    @Version private long version;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="experiencia_id", nullable=false) private Experiencia experiencia;
    @Column(nullable=false) private LocalDate fecha;
    @Column(nullable=false) private int personas;
    @Column(nullable=false, length=110) private String turista;
    @Column(nullable=false, length=150) private String correo;
    @Column(name="clave_idempotencia", nullable=false, length=100) private String claveIdempotencia;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16) private EstadoReserva estado;
    @Column(nullable=false) private Instant creadaEn;
    @Column(nullable=false) private Instant expiraEn;
    @Column(nullable=false, precision=14, scale=2) private BigDecimal total;
    @Embedded private Valoracion valoracion;
    protected Reserva() {
    }
    public Reserva(Experiencia experiencia, LocalDate fecha, int personas, String turista, String correo, String clave, Instant ahora, Instant expiracion) {
        this.id=UUID.randomUUID();
        this.experiencia=experiencia;
        this.fecha=fecha;
        this.personas=personas;
        this.turista=turista;
        this.correo=correo;
        this.claveIdempotencia=clave;
        this.creadaEn=ahora;
        this.expiraEn=expiracion;
        this.estado=EstadoReserva.BLOQUEADA;
        this.total=experiencia.getPrecio().multiply(BigDecimal.valueOf(personas));
    }
    public UUID getId() {
        return id;
    }
    public Experiencia getExperiencia() {
        return experiencia;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public int getPersonas() {
        return personas;
    }
    public String getTurista() {
        return turista;
    }
    public String getCorreo() {
        return correo;
    }
    public String getClaveIdempotencia() {
        return claveIdempotencia;
    }
    public EstadoReserva getEstado() {
        return estado;
    }
    public Instant getCreadaEn() {
        return creadaEn;
    }
    public Instant getExpiraEn() {
        return expiraEn;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public Valoracion getValoracion() {
        return valoracion;
    }
    public void setEstado(EstadoReserva estado) {
        this.estado=estado;
    }
    public void setValoracion(Valoracion valoracion) {
        this.valoracion=valoracion;
    }
}
