package com.rutasur.service;

import com.rutasur.model.*;
import com.rutasur.repository.*;
import com.rutasur.dto.ApiDtos.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.*;
import jakarta.persistence.EntityManager;
import java.util.*;

@Service
public class ReservaService {
    private static final Duration DURACION_BLOQUEO = Duration.ofMinutes(10);

    private final ExperienciaRepository experiencias;
    private final ExperienciaService catalogo;
    private final ReservaRepository reservas;
    private final Clock reloj;
    private final EntityManager em;
    public ReservaService(ExperienciaRepository experiencias, ReservaRepository reservas, ExperienciaService catalogo, Clock reloj, EntityManager em) {
        this.experiencias=experiencias;
        this.reservas=reservas;
        this.catalogo=catalogo;
        this.reloj=reloj;
        this.em=em;
    }
    private Instant ahora() {
        return Instant.now(reloj);
    }
    private LocalDate hoy() {
        return LocalDate.now(reloj);
    }
    private ResponseStatusException error(HttpStatus code, String mensaje) {
        return new ResponseStatusException(code, mensaje);
    }
    private Experiencia experienciaBloqueada(Long id) {
        return experiencias.bloquearFila(id).orElseThrow(()->error(HttpStatus.NOT_FOUND, "Experiencia inexistente"));
    }
    private long ocupados(Long id, LocalDate fecha, Instant instante) {
        return reservas.cuposOcupados(id, fecha, instante, EstadoReserva.CONFIRMADA, EstadoReserva.BLOQUEADA);
    }
    @Transactional(readOnly=true)
    public DisponibilidadResponse disponibilidad(Long id, LocalDate fecha) {
        Experiencia e=catalogo.buscar(id);
        long usados=ocupados(id, fecha, ahora());
        return new DisponibilidadResponse(id, fecha, e.getCapacidadDiaria(), (int)usados, PoliticaCupos.disponibles(e.getCapacidadDiaria(), usados));
    }
    @Transactional
    public ReservaResponse bloquear(BloqueoRequest solicitud, String clave) {
        if(clave==null||clave.isBlank()||clave.length()>100)throw error(HttpStatus.BAD_REQUEST, "Envíe Idempotency-Key (1-100 caracteres)");
        if(solicitud.fecha().isBefore(hoy()))throw error(HttpStatus.BAD_REQUEST, "La fecha debe ser hoy o posterior");
        Reserva repetida=reservas.findByClaveIdempotencia(clave).orElse(null);
        if(repetida!=null)return mismaSolicitud(repetida, solicitud);
        Experiencia e=experienciaBloqueada(solicitud.experienciaId());
        repetida=reservas.findByClaveIdempotencia(clave).orElse(null);
        if(repetida!=null)return mismaSolicitud(repetida, solicitud);
        long usados=ocupados(e.getId(), solicitud.fecha(), ahora());
        if(!PoliticaCupos.permiteBloqueo(e.getCapacidadDiaria(), usados, solicitud.personas()))throw error(HttpStatus.CONFLICT, "No hay cupos suficientes");
        Instant inicio=ahora();
        Reserva r=new Reserva(e, solicitud.fecha(), solicitud.personas(), solicitud.turista().trim(), solicitud.correo().trim(), clave, inicio, inicio.plus(DURACION_BLOQUEO));
        return ReservaResponse.desde(reservas.saveAndFlush(r));
    }
    private ReservaResponse mismaSolicitud(Reserva r, BloqueoRequest p) {
        if(!Objects.equals(r.getExperiencia().getId(), p.experienciaId()) || !r.getFecha().equals(p.fecha()) || r.getPersonas()!=p.personas()    || !r.getTurista().equals(p.turista().trim()) || !r.getCorreo().equals(p.correo().trim()))    throw error(HttpStatus.CONFLICT, "Clave de idempotencia reutilizada para una solicitud distinta");
        return ReservaResponse.desde(r);
    }
    @Transactional
    public ReservaResponse pagar(UUID id, boolean aprobado) {
        Reserva preliminar=reservas.findById(id).orElseThrow(()->error(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        experienciaBloqueada(preliminar.getExperiencia().getId());
        Reserva r=reservas.findById(id).orElseThrow(()->error(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        em.refresh(r);
        if(r.getEstado()==EstadoReserva.CONFIRMADA)return ReservaResponse.desde(r);
        if(r.getEstado()!=EstadoReserva.BLOQUEADA)throw error(HttpStatus.CONFLICT, "La reserva no admite confirmación");
        if(!r.getExpiraEn().isAfter(ahora())) {
            r.setEstado(EstadoReserva.EXPIRADA);
            return ReservaResponse.desde(r);
        }
        r.setEstado(aprobado?EstadoReserva.CONFIRMADA:EstadoReserva.CANCELADA);
        return ReservaResponse.desde(r);
    }
    @Transactional
    public ReservaResponse cancelar(UUID id) {
        Reserva preliminar=reservas.findById(id).orElseThrow(()->error(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        experienciaBloqueada(preliminar.getExperiencia().getId());
        Reserva r=reservas.findById(id).orElseThrow(()->error(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        em.refresh(r);
        if(r.getEstado()==EstadoReserva.BLOQUEADA || r.getEstado()==EstadoReserva.CONFIRMADA)r.setEstado(EstadoReserva.CANCELADA);
        return ReservaResponse.desde(r);
    }
    @Transactional(readOnly=true)
    public List<ReservaResponse> listar() {
        return reservas.findAllByOrderByCreadaEnDesc().stream().map(ReservaResponse::desde).toList();
    }
    @Transactional(readOnly=true)
    public ComprobanteResponse comprobante(UUID id) {
        Reserva r=reservas.findById(id).orElseThrow(()->error(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        if(r.getEstado()!=EstadoReserva.CONFIRMADA)throw error(HttpStatus.CONFLICT, "Solo las reservas confirmadas tienen comprobante");
        return new ComprobanteResponse("RS-"+r.getId().toString().substring(0, 8).toUpperCase(), r.getExperiencia().getNombre(),      r.getExperiencia().getDestino(), r.getFecha(), r.getPersonas(), r.getTurista(), r.getTotal(), "CONFIRMADA", "Pago simulado: sin transacción bancaria real");
    }
    @Transactional
    public ReservaResponse valorar(UUID id, ValoracionRequest p) {
        Reserva r=reservas.findById(id).orElseThrow(()->error(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        if(r.getEstado()!=EstadoReserva.CONFIRMADA || !r.getFecha().isBefore(hoy()))throw error(HttpStatus.CONFLICT, "Solo se valoran experiencias confirmadas que ya ocurrieron");
        if(r.getValoracion()!=null)throw error(HttpStatus.CONFLICT, "La experiencia ya fue valorada");
        r.setValoracion(new Valoracion(p.puntuacion(), p.comentario().trim(), ahora()));
        return ReservaResponse.desde(r);
    }
    @Scheduled(fixedDelay=60000)
    @Transactional
    public void cerrarBloqueosVencidos() {
        reservas.marcarExpiradas(ahora(), EstadoReserva.EXPIRADA, EstadoReserva.BLOQUEADA);
    }
}
