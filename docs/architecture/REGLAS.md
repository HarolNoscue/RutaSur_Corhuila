# Reglas verificables de RutaSur v1

| # | Regla | Comprobación |
|---|---|---|
|1|Un controlador no importa repositorios|ArquitecturaTest/ArchUnit|
|2|ReservaService centraliza cupos/bloqueo/confirmación|Revisión de clases; tests de servicio|
|3|Un bloqueo nunca excede el aforo|ReservaServiceTest.rechazaCuandoNoHayCupos|
|4|La retención usa operación atómica y bloqueo de fila|ReservaServiceTest.concurrenciaPermiteUnaSolaRetencion|
|5|Los bloqueos caducan y dejan de ocupar inventario|Consulta JPQL por `expiraEn > ahora` y scheduled task; test de expiración pendiente|
|6|Misma clave no crea dos retenciones|ReservaServiceTest.idempotenciaNoDuplicaBloqueos|
|7|Pago simulado aprobado y bloqueo vigente para confirmar|ReservaService.pagar; test de vencimiento pendiente|
|8|No se decide inventario solo desde caché|Consulta JPQL a base relacional; revisión de implementación|

**Pruebas automatizadas pendientes:** vencimiento con reloj inyectado, idempotencia concurrente cruzada y comparación PostgreSQL/H2.
