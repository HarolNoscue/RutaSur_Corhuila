package com.rutasur.repository;

import com.rutasur.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

    Optional<Reserva> findByClaveIdempotencia(String claveIdempotencia);

    @Query("select coalesce(sum(r.personas), 0) from Reserva r where r.experiencia.id=:id and r.fecha=:fecha and (r.estado=:confirmada or (r.estado=:bloqueada and r.expiraEn>:ahora))")
    Long cuposOcupados(@Param("id")Long id, @Param("fecha")LocalDate fecha, @Param("ahora")Instant ahora,     @Param("confirmada")EstadoReserva confirmada, @Param("bloqueada")EstadoReserva bloqueada);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("update Reserva r set r.estado=:expirada where r.estado=:bloqueada and r.expiraEn<=:ahora")
    int marcarExpiradas(@Param("ahora")Instant ahora, @Param("expirada")EstadoReserva expirada, @Param("bloqueada")EstadoReserva bloqueada);
    List<Reserva> findAllByOrderByCreadaEnDesc();
}
