# Almacenamiento de RutaSur

## Predeterminado: H2 en un archivo local

La versión académica usa **H2**, no IBM Db2. Es una base de datos relacional integrada
con Spring Boot y no requiere un servidor separado ni Docker.

Al arrancar el backend desde la carpeta `backend/`, la URL JDBC definida en
`backend/src/main/resources/application.yml` crea/abre el archivo:

`database/data/rutasur.mv.db`

Los datos se conservan entre reinicios. Esta carpeta de datos no se publica en GitHub
para no incluir reservas y correos personales en el repositorio.

## Conservar los datos de la versión anterior

1. **Apaga Spring Boot** antes de tocar archivos H2.
2. En el proyecto anterior copia `apps/api/rutasur.mv.db`.
3. Pégalo en este proyecto como `database/data/rutasur.mv.db`.
4. Después inicia el nuevo backend desde `backend/`.

Si abres la v1 anterior desde otro directorio al ejecutar Maven, busca el archivo
`rutasur.mv.db` en su directorio de trabajo real. No renombres una base que esté abierta.

Si no copias el archivo, se creará una base nueva con las tres experiencias iniciales.

## Alternativa opcional: PostgreSQL

PostgreSQL es útil para probar un motor cliente-servidor con transacciones,
pero no se requiere para la demostración académica:

```powershell
cd database
docker compose up -d db
cd ..\backend
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Esta alternativa guarda los datos en un volumen administrado por Docker,
no dentro de `data/`. Las credenciales del `compose.yml` son de desarrollo local.
