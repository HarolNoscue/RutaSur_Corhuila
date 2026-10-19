# Backend — RutaSur

Proyecto **Spring Boot 3.4 / Java 17** independiente, organizado en controladores,
servicios, repositorios, modelos y DTOs.

## Ejecutar desde `backend/`

```powershell
mvn spring-boot:run
```

API: http://localhost:8080/api/experiencias

## Pruebas

```powershell
mvn test
```

La base predeterminada **H2** se guarda fuera del backend, en
`../database/data/rutasur.mv.db`. Esa carpeta existe en el repositorio
mediante un `.gitkeep`, pero no se publican archivos con datos.

Para probar PostgreSQL, consulta `../database/README.md`.
