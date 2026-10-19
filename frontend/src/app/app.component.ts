import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiService, Comprobante, Disponibilidad, Experiencia, Reserva } from './api.service';
@Component({ selector: 'app-root', standalone: true, imports: [CommonModule, FormsModule], templateUrl: './app.component.html' })
export class AppComponent implements OnInit {
    api = inject(ApiService);
    pestana: 'catalogo' | 'mis-reservas' | 'operador' = 'catalogo';
    experiencias: Experiencia[] = [];
    reservas: Reserva[] = [];
    ocupacion: Disponibilidad | null = null;
    actual: Reserva | null = null;
    comprobante: Comprobante | null = null;
    cargando = false;
    mensaje = '';
    error = '';
    formulario = { experienciaId: 1, fecha: new Date().toISOString().slice(0, 10), personas: 1, turista: '', correo: '' };
    valoracion = { puntuacion: 5, comentario: '' };
    nueva = { nombre: '', destino: '', descripcion: '', precio: 50000, capacidadDiaria: 8 };
    ngOnInit() { this.cargarExperiencias(); this.cargarReservas(); }
    irAReserva() { document.getElementById('reserva-form')?.scrollIntoView({ behavior: 'smooth' }); }
    imprimir() { window.print(); }
    dinero(n: number) { return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(n); }
    fechaFormateada(f: string) { return f; }
    esPasada(f: string) { return f < new Date().toISOString().slice(0, 10); }
    private msgError(e: unknown) { const r = e as HttpErrorResponse; this.error = r.error?.error || r.message || 'No fue posible completar la solicitud'; this.cargando = false; }
    private inicio() { this.mensaje = ''; this.error = ''; this.cargando = true; }
    cargarExperiencias() { this.api.experiencias().subscribe({ next: e => { this.experiencias = e; if (e.length && !e.some(x => x.id === this.formulario.experienciaId))
            this.formulario.experienciaId = e[0].id; }, error: e => this.msgError(e) }); }
    cargarReservas() { this.api.reservas().subscribe({ next: r => this.reservas = r, error: e => this.msgError(e) }); }
    consultarDisponibilidad() { this.inicio(); this.api.disponibilidad(this.formulario.experienciaId, this.formulario.fecha).subscribe({ next: d => { this.ocupacion = d; this.cargando = false; }, error: e => this.msgError(e) }); }
    bloquear() { this.inicio(); this.comprobante = null; const clave = crypto.randomUUID(); this.api.bloquear({ ...this.formulario }, clave).subscribe({ next: r => { this.actual = r; this.mensaje = 'Cupos bloqueados temporalmente; confirma el pago simulado antes del vencimiento.'; this.cargando = false; this.cargarReservas(); this.consultarDisponibilidad(); }, error: e => this.msgError(e) }); }
    pagar(aprobado: boolean) { if (!this.actual)
        return; this.inicio(); this.api.pagar(this.actual.id, aprobado).subscribe({ next: r => { this.actual = r; this.mensaje = r.estado === 'CONFIRMADA' ? '¡Reserva confirmada! Puedes generar el comprobante.' : `Estado de la reserva: ${r.estado}`; this.cargando = false; this.cargarReservas(); this.consultarDisponibilidad(); }, error: e => this.msgError(e) }); }
    cancelar(r: Reserva) { this.inicio(); this.api.cancelar(r.id).subscribe({ next: respuesta => { this.mensaje = 'Reserva actualizada a ' + respuesta.estado; this.cargando = false; this.actual = respuesta; this.cargarReservas(); }, error: e => this.msgError(e) }); }
    generarComprobante(r: Reserva) { this.inicio(); this.api.comprobante(r.id).subscribe({ next: c => { this.comprobante = c; this.cargando = false; }, error: e => this.msgError(e) }); }
    valorar(r: Reserva) { this.inicio(); this.api.valorar(r.id, this.valoracion.puntuacion, this.valoracion.comentario).subscribe({ next: () => { this.cargando = false; this.mensaje = 'Valoración registrada'; this.cargarReservas(); }, error: e => this.msgError(e) }); }
    crearExperiencia() { this.inicio(); this.api.crearExperiencia({ ...this.nueva }).subscribe({ next: () => { this.cargando = false; this.mensaje = 'Experiencia agregada'; this.cargarExperiencias(); this.nueva = { nombre: '', destino: '', descripcion: '', precio: 50000, capacidadDiaria: 8 }; }, error: e => this.msgError(e) }); }
}
