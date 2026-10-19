package com.rutasur.controller;

import com.rutasur.dto.ApiDtos.*;
import com.rutasur.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController @RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service=service;
    }

    @GetMapping("/disponibilidad") public DisponibilidadResponse disponibilidad(@RequestParam Long experienciaId, @RequestParam LocalDate fecha) {
        return service.disponibilidad(experienciaId, fecha);
    }
    @PostMapping("/bloqueos") public ResponseEntity<ReservaResponse> bloquear(@RequestHeader("Idempotency-Key") String clave, @Valid @RequestBody BloqueoRequest p) {
        ReservaResponse r=service.bloquear(p, clave);
        return ResponseEntity.status(r.estado()==com.rutasur.model.EstadoReserva.BLOQUEADA?HttpStatus.CREATED:HttpStatus.OK).body(r);
    }
    @PostMapping("/{id}/pago-simulado") public ReservaResponse pagar(@PathVariable UUID id, @RequestBody PagoSimuladoRequest p) {
        return service.pagar(id, p.aprobado());
    }
    @PostMapping("/{id}/cancelacion") public ReservaResponse cancelar(@PathVariable UUID id) {
        return service.cancelar(id);
    }
    @GetMapping public List<ReservaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}/comprobante") public ComprobanteResponse comprobante(@PathVariable UUID id) {
        return service.comprobante(id);
    }
    @PostMapping("/{id}/valoracion") public ReservaResponse valorar(@PathVariable UUID id, @Valid @RequestBody ValoracionRequest p) {
        return service.valorar(id, p);
    }
}
