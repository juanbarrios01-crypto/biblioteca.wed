# Sistema de Gestión de Biblioteca

Este es un backend para un Sistema de Gestión de Biblioteca, basado en Node.js, Express y Prisma. Su estructura arquitectónica ha sido adaptada de un repositorio de referencia (Clean Architecture / MVC adaptado a Node.js).

## Tecnologías Utilizadas

*   **Node.js & Express**: Framework para el servidor web.
*   **Prisma ORM**: ORM para la interacción con la base de datos.
*   **TypeScript**: Tipado estático para JavaScript.
*   **JWT & Bcrypt**: Autenticación y seguridad.

## Estructura del Proyecto

*   `src/infrastructure/`: Controladores, rutas, DTOs y configuración de Prisma (Adaptación de la capa de infraestructura/presentación).
*   `src/services/`: Lógica de negocio para Usuarios, Libros y Préstamos.
*   `src/security/`: Middlewares para autenticación y autorización (RBAC).
*   `prisma/schema.prisma`: Entidades de la base de datos (Dominio).

## Base de Datos (Configuración)

El proyecto utiliza Prisma, lo que facilita el cambio entre distintas bases de datos. Por defecto, está configurado para usar **SQLite** para que sea fácil de probar sin configuración adicional.

### Cambiar de Base de Datos (MySQL o PostgreSQL)

1.  Abre el archivo `prisma/schema.prisma`.
2.  Cambia el `provider` en el bloque `datasource`:
    *   Para PostgreSQL: `provider = "postgresql"`
    *   Para MySQL: `provider = "mysql"`
3.  Actualiza la variable `DATABASE_URL` en tu archivo `.env` con la cadena de conexión correspondiente. Ejemplo para MySQL: `mysql://usuario:contraseña@localhost:3306/biblioteca`.

## Instrucciones de Despliegue

### 1. Clonar el repositorio y configurar variables de entorno

Clona el repositorio, y asegúrate de crear un archivo `.env` en la raíz del proyecto basándote en el `.env.example` (o usa el que se generó):

```env
DATABASE_URL="file:./dev.db"
JWT_SECRET="supersecreto123"
PORT=3000
```

### 2. Instalar dependencias

```bash
npm install
```

### 3. Ejecutar las migraciones de Prisma

Esto creará las tablas en la base de datos según el esquema:

```bash
npx prisma migrate dev --name init
```

### 4. Compilar e iniciar el proyecto

Para desarrollo (con recarga automática):
```bash
npx ts-node-dev src/index.ts
```

Para producción:
```bash
npx tsc
node dist/index.js
```

## Usuarios y Roles

Existen dos roles en el sistema: `ADMIN` y `STUDENT`.
*   **Admin**: Tiene acceso a todo (crear libros, ver todos los préstamos, aceptar devoluciones).
*   **Student**: Solo puede ver los libros, pedir préstamos y ver su historial personal.
