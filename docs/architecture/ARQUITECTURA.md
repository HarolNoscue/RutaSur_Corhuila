# Arquitectura RutaSur v1

Patrón dominante académico: **MVC** (Vista Angular, Controladores REST Spring y Modelo/lógica de negocio).
Complementos: arquitectura **en capas** y distribución **cliente-servidor**.

```mermaid
flowchart TD
 V[VistaReservas - Angular] --> EC[ExperienciaController]
 V --> RC[ReservaController]
 EC --> ES[ExperienciaService]
 RC --> RS[ReservaService]
 RS --> ES
 ES --> ER[ExperienciaRepository]
 RS --> ER
 RS --> RR[ReservaRepository]
 ER --> DB[(Base de Datos Relacional)]
 RR --> DB
```

`ReservaService` es el dueño de la regla de disponibilidad; la fila `Experiencia` bloqueada mediante
`@Lock(PESSIMISTIC_WRITE)` opera como serialización entre instancias para evitar reservas simultáneas
que superen el aforo. `ReservaRepository` suma exclusivamente confirmadas y retenidas vigentes.

**Limitación consciente:** gran concurrencia entre diferentes fechas de una misma experiencia comparte un mismo bloqueo;
para evolucionar, mover la sincronización a inventario por `experiencia_id + fecha`.

## Secuencia CU-01

```mermaid
sequenceDiagram
 actor T as Turista
 participant V as VistaReservas
 participant C as ReservaController
 participant S as ReservaService
 participant E as ExperienciaRepository
 participant R as ReservaRepository
 participant DB as BaseDatosRelacional
 T->>V: Escoge experiencia, fecha, personas
 V->>C: POST bloqueo + Idempotency-Key
 C->>S: bloquear(solicitud)
 S->>R: buscarPorClave(clave)
 R->>DB: consulta clave
 DB-->>R: existente o vacío
 R-->>S: resultado
 alt solicitud repetida
   S-->>C: resultado anterior
   C-->>V: reserva existente
 else solicitud nueva
   S->>E: bloquearFila(experiencia)
   E->>DB: SELECT FOR UPDATE
   DB-->>E: fila exclusiva
   E-->>S: experiencia
   S->>R: cuposOcupados(fecha)
   R->>DB: consulta confirmadas + bloqueadas vigentes
   DB-->>R: ocupación
   R-->>S: cupos
   Note over S,DB: Regla: ocupados + pedidos <= capacidad
   alt cupos disponibles
     S->>R: guardarBloqueo(expiraEn)
     R->>DB: INSERT transaccional
     DB-->>R: reserva retenida
     R-->>S: identificador y expiración
     S-->>C: creada
     C-->>V: HTTP 201
   else cupos insuficientes
     S-->>C: sin disponibilidad
     C-->>V: HTTP 409
   end
 end
```

El diagrama de secuencia es lógico; los mensajes `INSERT/SELECT FOR UPDATE` son operaciones de persistencia internas a los repositorios.
