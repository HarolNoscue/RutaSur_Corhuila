package com.rutasur.dto;

import com.rutasur.model.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

public final class ApiDtos {

    private ApiDtos() {
    }
    public record CrearExperiencia(@NotBlank String nombre, @NotBlank String destino, @NotBlank String descripcion,      @NotNull @DecimalMin("0.01") BigDecimal precio, @Min(1) int capacidadDiaria) {
    }
    public record BloqueoRequest(@NotNull Long experienciaId, @NotNull LocalDate fecha, @Min(1) int personas,      @NotBlank String turista, @NotBlank @Email String correo) {
    }
    public record PagoSimuladoRequest(boolean aprobado) {
    }
    public record ValoracionRequest(@Min(1) @Max(5) int puntuacion, @NotBlank String comentario) {
    }
    public record DisponibilidadResponse(Long experienciaId, LocalDate fecha, int capacidad, int ocupados, int disponibles) {
    }
    public record ReservaResponse(UUID id, Long experienciaId, String experiencia, String destino, LocalDate fecha, int personas,       String turista, String correo, EstadoReserva estado, Instant expiraEn, BigDecimal total, Valoracion valoracion) {
        public static ReservaResponse desde(Reserva r) {
            return new ReservaResponse(r.getId(), r.getExperiencia().getId(), r.getExperiencia().getNombre(),        r.getExperiencia().getDestino(), r.getFecha(), r.getPersonas(), r.getTurista(), r.getCorreo(), r.getEstado(), r.getExpiraEn(), r.getTotal(), r.getValoracion());
        }
    }
    public record ComprobanteResponse(String numero, String experiencia, String destino, LocalDate fecha, int personas,       String turista, BigDecimal total, String estado, String mensaje) {
    }
}
