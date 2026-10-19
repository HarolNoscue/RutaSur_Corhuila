# Guion del PDF final (completar después de ejecutar y probar)

1. Portada, objetivo y alcance de la v1. Referencia al diagnóstico H0/H1.
2. Arquitectura: MVC dominante, capas y cliente-servidor; diagrama de componentes y tabla de dependencias.
3. Modelo de datos: `Experiencia`, `Reserva`, `Valoracion`, estados y bloqueo de fila.
4. CU-01: secuencia de bloqueo y escenarios 201 / 409, clave de idempotencia y expiración.
5. Implementación: captura de catálogo, bloqueo, pago simulado, comprobante y panel operador.
6. Pruebas: anexar salida real de `mvn test`, `npm run build`, ensayo de dos solicitudes concurrentes.
7. Calidad y límites: consistencia demostrada por pruebas; disponibilidad 99,5 % y P95 <3 s son metas aún no medidas.
8. Cómo ejecutar: Java/Node/Maven, comandos backend/frontend, H2 y PostgreSQL opcional.
9. Enlace público al **nuevo repositorio personal** (añadir URL verdadera después de subirlo).
10. Reflexión crítica de mantenibilidad, escalabilidad y eficiencia.

No usar afirmaciones de éxito de pruebas ni capturas simuladas hasta ejecutar y verificar el software.
