# Migración de MySQL a PostgreSQL (Neon) y conexión de la app

Este repositorio ya trae un esquema PostgreSQL listo en `src/main/resources/DB/create_postgres.sql` que coincide con el código (DAOs y modelos Java). Aquí tienes los pasos para empezar de cero, migrar datos desde MySQL si lo necesitas, crear la base en Neon y conectar la app.

## 1) Crear la base en Neon

1. Crea una cuenta y proyecto en https://neon.tech
2. Crea una base de datos (por ejemplo `semaforo`) y un usuario con contraseña.
3. En la pestaña "Connection Details" copia el host del Pooler (termina en `-pooler`), el puerto (5432), la base, el usuario y la contraseña. Neon requiere SSL.
4. Abre pgAdmin 4 y añade un nuevo servidor con esos datos (host del pooler, usuario/contraseña). En "SSL" usa `Require` o `Verify-CA` según tus preferencias.
5. En pgAdmin, conéctate a tu DB y ejecuta el script `src/main/resources/DB/create_postgres.sql` en el Query Tool para crear el esquema.

## 2) Opciones para migrar datos desde MySQL

Si quieres llevar tus datos de MySQL a Neon, la forma más sencilla es usar `pgloader`.

- Requisitos: Docker Desktop en Windows o WSL.
- Comando (ajusta credenciales y nombres):

```powershell
# Con Docker
docker run --rm dimitri/pgloader:latest \
  pgloader \
  mysql://MYSQL_USER:MYSQL_PASS@MYSQL_HOST:3306/MY_DB \
  postgresql://PG_USER:PG_PASS@NEON_POOLER_HOST:5432/SEMAFORO_DB?sslmode=require
```

pgloader mapea tipos de MySQL a PostgreSQL automáticamente. Revisa las columnas booleanas (tinyint(1) -> boolean), fechas, y textos largos. Si ves errores, puedes:
- Limpiar datos inválidos en origen (por ejemplo, fechas 0000-00-00 no válidas en PostgreSQL).
- Cargar primero el esquema (este repo) y luego solo los datos (`--data-only`).

Alternativas sin Docker:
- Instalar pgloader nativo (más complejo en Windows), o
- Exportar CSVs desde MySQL y luego importar a cada tabla con pgAdmin (Import/Export).

## 3) Configurar la app para usar tu Neon

La clase `ConexaoBD` ahora lee variables de entorno; si no están, usa valores por defecto (solo para desarrollo). Define estas variables en tu sistema o en tu entorno de ejecución:

- `DB_HOST` = host del pooler de Neon (ej: `ep-xxxx-pooler.us-east-2.aws.neon.tech`)
- `DB_PORT` = `5432`
- `DB_NAME` = nombre de la base (ej: `semaforo`)
- `DB_USER` = usuario de Neon
- `DB_PASSWORD` = contraseña
- `DB_SSLMODE` = `require` (recomendado)

En PowerShell (temporal para la sesión actual):

```powershell
$env:DB_HOST = "tu-host-pooler.neon.tech"
$env:DB_PORT = "5432"
$env:DB_NAME = "semaforo"
$env:DB_USER = "usuario"
$env:DB_PASSWORD = "contraseña"
$env:DB_SSLMODE = "require"
```

Luego ejecuta la app:

```powershell
mvn javafx:run
```

## 4) Notas de compatibilidad MySQL -> PostgreSQL

- AUTO_INCREMENT -> `generated always as identity` en PostgreSQL (usado en este esquema).
- boolean: `tinyint(1)` se mapea a `boolean`.
- blobs: usa `bytea` (para `image_path`).
- fechas: usa `date` (el código Java usa `LocalDate`).
- collations/charsets: PostgreSQL usa UTF-8 por defecto; evita datos con codificaciones mixtas.
- nombres en minúsculas: en PostgreSQL, no uses comillas en Identifiers para evitar conflictos. Este esquema está en minúsculas.

## 5) Verificación rápida

- Desde pgAdmin, ejecuta `select 1;` para verificar conexión.
- Inserta un registro de prueba en `clientes` y `veiculos` para validar claves foráneas.
- Inicia la app y prueba crear/editar vehículos y ventas.

Si necesitas que generemos un script de migración específico desde tu esquema MySQL (DDL), compárteme tu `CREATE TABLE ...` de MySQL y lo adapto a PostgreSQL paso a paso.
