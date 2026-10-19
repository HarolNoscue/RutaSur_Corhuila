# ADR-001 — MVC complementado por capas y cliente-servidor

**Estado:** propuesta implementada como estructura base.

## Contexto
RutaSur necesita consultas de catálogo, bloqueos temporales, reservas y confirmación de pagos simulados.
El criterio crítico es mantener el inventario de cupos consistente bajo solicitudes simultáneas.

## Decisión
- Vista Angular en cliente; controladores Spring Boot REST.
- Servicios de negocio sin dependencia a controladores; repositorios JPA para persistencia.
- Un único almacén relacional como fuente de verdad; sin inventario autoritativo en caché.
- Bloqueo pesimista de fila de experiencia para serializar solicitudes concurrentes del mismo inventario.

## Alternativas descartadas
- Pipes and Filters: el flujo central es transaccional, no pipeline.
- P2P: no garantiza fácilmente un inventario centralizado.
- Microservicios: coste de coordinación injustificado para una v1 de alcance reducido.

## Consecuencias
+ Componentes fácilmente testeables y evolución incremental.
+ Fuente única de verdad para reservas.
- Contención de reservas de fechas diferentes de una misma experiencia (mejorar con filas de salida por fecha).
- Interfaz y servicios inicialmente sin autenticar: aceptable solo en una demo local.
