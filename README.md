# RutaSur v1 — Prototipo funcional personal

Plataforma académica de reservas turísticas para San Agustín, Isnos y el Desierto
de la Tatacoa. Proyecto organizado como **dos aplicaciones independientes en un
único repositorio** para que se pueda clonar y ejecutar sin duplicar
repositorios ni instalar un motor de base de datos.

## Arquitectura

- **MVC (patrón dominante):** Angular presenta las vistas; Spring Boot contiene
  controladores REST y el modelo de negocio.
- **Capas:** `controller` → `service` → `repository` → base de datos.
- **Cliente-servidor:** Angular consume la API REST mediante HTTP.
- **Persistencia:** **H2** relacional en archivo local; PostgreSQL opcional.
- La retención de cupos se valida transaccionalmente desde el backend,
  no desde Angular ni desde datos en caché.

## Estructura

```text
RutaSur-v1/
├── frontend/                    Angular 19 (proyecto independiente)
│   ├── src/app/
│   ├── package.json
│   ├── angular.json
│   └── README.md
├── backend/                     Spring Boot 3.4 / Maven (proyecto independiente)
│   ├── src/main/java/com/rutasur/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   ├── dto/
│   │   └── config/
│   ├── src/test/
│   ├── pom.xml
│   └── README.md
├── database/
│   ├── data/                    Archivos locales H2 (no incluidos en Git)
│   ├── compose.yml              PostgreSQL opcional
│   └── README.md
├── docs/                        Especificaciones, arquitectura y reglas
├── scripts/                     Prueba de política de cupos
└── README.md
```
Para ejecutar abrir terminal en intellij y ejecutar 

back 
cd backend
mvn spring-boot:run


front
cd frontend
npm install - por si al clonar no descarga node_modules
npm start
   
al final ejecutar http://localhost:4200/ en el navegador  para ingresar al front  y listo 
