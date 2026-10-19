# RutaSur v1 — Prototipo funcional personal

Plataforma académica de reservas turísticas para San Agustín, Isnos y el Desierto
de la Tatacoa. Proyecto organizado como **dos aplicaciones independientes en un
único repositorio** para que el docente pueda clonar y ejecutar sin duplicar
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

## Requisitos del profesor

- Java 17 o 21; Maven 3.9+.
- Node.js 20+; npm 10+.
- **No se necesita instalar Db2, PostgreSQL ni Docker** para la opción H2.
- Acceso a internet para instalar dependencias la primera vez.

## Inicio rápido (dos terminales)

Abre **PowerShell** en la carpeta raíz del repositorio.

**Terminal 1: API (déjala abierta)**

```powershell
cd backend
mvn spring-boot:run
```

**Terminal 2: Angular (déjala abierta)**

```powershell
cd frontend
npm install
npm start
```

Abre **http://localhost:4200**. API en **http://localhost:8080/api/experiencias**.
El catálogo inicial tendrá tres experiencias cuando se use una base nueva.

## Demostración en clase (5 minutos)

1. Abrir el catálogo de experiencias y seleccionar **Astronomía en la Tatacoa**.
2. Consultar cupos disponibles para una fecha futura.
3. Bloquear dos cupos con nombre/correo de prueba, verificar el descuento del inventario.
4. Simular un pago aprobado; verificar reserva **CONFIRMADA** y comprobante imprimible.
5. Intentar bloquear más cupos que los disponibles; debe responder **409**.
6. Mostrar el panel **Operador · demo**, registrar una experiencia y consultar el catálogo.

## Estado funcional del prototipo

- Catálogo, consulta de disponibilidad y gestión de experiencias.
- Bloqueos de 10 minutos con expiración e idempotencia.
- Pago **simulado**, cancelación y comprobantes.
- Valoraciones únicamente después de una visita confirmada.
- Panel operador **sin autenticación**: solo demostración local.
- Control del aforo mediante bloqueo pesimista y transacciones de base de datos.

## Pruebas

```powershell
cd backend
mvn test
```

Y desde `frontend/`:

```powershell
npm run build
```

Los resultados de pruebas y métricas deben adjuntarse al PDF cuando se hayan
ejecutado; **este paquete reorganizado aún debe compilarse en tu equipo**.
La meta de 99,5 % de disponibilidad y P95 menor a 3 segundos corresponde al
diagnóstico arquitectónico, no a resultados que ya podamos certificar.

## Importante: conservar tus experiencias y reservas previas

**Detén el backend anterior antes de copiar los datos**.

Copia el archivo del proyecto antiguo
`apps/api/rutasur.mv.db` al nuevo `database/data/rutasur.mv.db`.
Para los pasos detallados consulta [database/README.md](database/README.md).

Si no copias este archivo, H2 iniciará con una base nueva (no es un error).

## Publicar en un único GitHub personal

Crea un repositorio nuevo, vacío y sin README. Desde la raíz de este proyecto:

```powershell
git init
git add .
git commit -m "feat: RutaSur v1 organizado por proyectos"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/TU_REPOSITORIO.git
git push -u origin main
```

Sustituye TU_USUARIO y TU_REPOSITORIO. **No** publiques las bases de datos,
archivos `.env`, credenciales privadas ni correos reales de las reservas.

## Qué cambia y qué se conserva

Cambios: carpetas separadas `frontend`, `backend`, `database`, formato legible
para Java, TypeScript, HTML, CSS, JSON y el `pom.xml`, y ruta del archivo H2.

Se conserva: reglas de reserva, servicios y controladores, endpoints, datos de
inicio, estilos y plantillas Angular, respuestas JSON, bloqueo transaccional,
expiración e idempotencia. Sigue siendo una sola API, **no microservicios**.

> Prototipo académico sin pagos reales ni autenticación. No exponer endpoints
> de administración a internet.
