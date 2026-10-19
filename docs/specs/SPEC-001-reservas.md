# SPEC-001 · RutaSur v1 — Reserva de cupos

## Fuente del alcance
Documento entregado «Reto y alcance delimitado 2.docx», H0/H1, componentes, secuencia y reglas.
Este archivo es la traducción a criterios de aceptación del diagnóstico académico.

## Capacidades previstas en v1
- Consultar experiencias: San Agustín, Isnos y Tatacoa.
- Consultar disponibilidad por experiencia y fecha.
- Crear un bloqueo temporal de cupos con expiración e idempotencia.
- Confirmar la reserva con pago **simulado** y emitir un comprobante imprimible.
- Registrar valoraciones solamente después de la fecha de una reserva confirmada.
- Panel básico de operador para consultar reservas y crear experiencias.

## Caso principal CU-01
1. El turista elige experiencia, fecha y personas desde `VistaReservas`.
2. `ReservaController` delega en `ReservaService`.
3. `ReservaService` busca la clave idempotente.
4. Se adquiere `PESSIMISTIC_WRITE` sobre la fila de `Experiencia` en la misma transacción.
5. Se recalculan cupos: confirmadas + bloqueos no vencidos para esa experiencia y fecha.
6. Si alcanza el aforo, se persiste el bloqueo con identificador único y vencimiento.
7. Si no alcanza, HTTP 409 sin crear la reserva.
8. Un pago simulado aprobado antes del vencimiento confirma la retención; pago fallido cancela.
9. Al vencer, deja de ocupar cupos inmediatamente por criterio de consulta; proceso programado actualiza el estado almacenado.

## Regla calculable
`ocupados = sum(personas CONFIRMADA) + sum(personas BLOQUEADA con expiraEn > ahora)`.
`disponibles = max(0, capacidadDiaria - ocupados)`.

## Supuestos académicos de implementación
- Cada experiencia tiene capacidad **diaria** constante. No se administran horarios o salidas múltiples todavía.
- El bloqueo dura **10 minutos** (parámetro demostrativo; el diagnóstico no fija su duración).
- H2 persistente para ejecución rápida; PostgreSQL opcional para prueba con base relacional compartida.
- Usuarios sin autenticación y pagos ficticios: **prototipo**, no desplegar públicamente.
- El bloqueo pesimista de la fila de Experiencia serializa fechas distintas de esa misma experiencia: solución correcta para v1 pequeña, posible cuello de botella a escalar.

## Criterios de aceptación
- A1: Dos intentos concurrentes de retener el último cupo no generan sobreventa.
- A2: Misma clave y mismos datos no insertan reserva adicional.
- A3: Misma clave con diferentes datos genera conflicto.
- A4: Pago aprobado dentro del plazo marca CONFIRMADA.
- A5: Pago rechazado marca CANCELADA y devuelve cupos.
- A6: Un bloqueo vencido deja de ocupar cupos; pago posterior no confirma.
- A7: Solo reserva confirmada genera comprobante y solo visita pasada acepta valoración.
- A8: Controladores no importan repositorios ni incorporan reglas de disponibilidad.

## Metas de calidad del diagnóstico (NO medidas)
- Consistencia: cero sobreventas bajo concurrencia.
- Disponibilidad: 99,5 % mensual (requiere despliegue y observabilidad, NO comprobable en demo local).
- Rendimiento: respuesta de reserva menor a 3 segundos P95 (requiere prueba de carga).
- Escalabilidad adicional del H1: 10 veces tráfico y catálogo P95 < 2,5 s (fuera de prueba local).
