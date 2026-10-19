import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface Experiencia {
    id: number;
    nombre: string;
    destino: string;
    descripcion: string;
    precio: number;
    capacidadDiaria: number;
}
export interface Disponibilidad {
    experienciaId: number;
    fecha: string;
    capacidad: number;
    ocupados: number;
    disponibles: number;
}
export type EstadoReserva = 'BLOQUEADA' | 'CONFIRMADA' | 'CANCELADA' | 'EXPIRADA';
export interface Reserva {
    id: string;
    experienciaId: number;
    experiencia: string;
    destino: string;
    fecha: string;
    personas: number;
    turista: string;
    correo: string;
    estado: EstadoReserva;
    expiraEn: string;
    total: number;
    valoracion?: {
        puntuacion: number;
        comentario: string;
    };
}
export interface Comprobante {
    numero: string;
    experiencia: string;
    destino: string;
    fecha: string;
    personas: number;
    turista: string;
    total: number;
    estado: string;
    mensaje: string;
}
@Injectable({ providedIn: 'root' })
export class ApiService {
    private http = inject(HttpClient);
    experiencias(): Observable<Experiencia[]> { return this.http.get<Experiencia[]>('/api/experiencias'); }
    disponibilidad(id: number, fecha: string): Observable<Disponibilidad> { return this.http.get<Disponibilidad>('/api/reservas/disponibilidad', { params: new HttpParams().set('experienciaId', id).set('fecha', fecha) }); }
    bloquear(datos: {
        experienciaId: number;
        fecha: string;
        personas: number;
        turista: string;
        correo: string;
    }, clave: string): Observable<Reserva> {
        return this.http.post<Reserva>('/api/reservas/bloqueos', datos, { headers: new HttpHeaders({ 'Idempotency-Key': clave }) });
    }
    pagar(id: string, aprobado: boolean): Observable<Reserva> { return this.http.post<Reserva>(`/api/reservas/${id}/pago-simulado`, { aprobado }); }
    cancelar(id: string): Observable<Reserva> { return this.http.post<Reserva>(`/api/reservas/${id}/cancelacion`, {}); }
    reservas(): Observable<Reserva[]> { return this.http.get<Reserva[]>('/api/reservas'); }
    comprobante(id: string): Observable<Comprobante> { return this.http.get<Comprobante>(`/api/reservas/${id}/comprobante`); }
    valorar(id: string, puntuacion: number, comentario: string): Observable<Reserva> { return this.http.post<Reserva>(`/api/reservas/${id}/valoracion`, { puntuacion, comentario }); }
    crearExperiencia(datos: {
        nombre: string;
        destino: string;
        descripcion: string;
        precio: number;
        capacidadDiaria: number;
    }): Observable<Experiencia> { return this.http.post<Experiencia>('/api/experiencias', datos); }
}
