# Acerca del proyecto

### Looma-Core

Este es el proyecto backend de Looma, para funcionar por completo debe de usarse en conjunto con `Looma-Front`.



# Configurarcion de entorno

## Requisitos previos

- Java 21
- PostgreSQL

## Configuracion de PostgreSQL

1. Instalar PostgreSQL si no esta instalado.
2. Crear una base de datos llamada `looma_dev`.
3. Crear un usuario y contraseña para la base de datos o usar el ya existente con postgres.
  - revisar el archivo .env para conocer que variables de entorno se deben configurar.

### Acerca de la base de datos

- Necesita ser inicializada la base de datos para levantar el proyecto.

## Detalles acerca de los perfiles

- `development`: Perfil por defecto. Usado para desarrollo local.
- `production`: Usado para despliegue en produccion.

Deben usar siempre el perfil `development` para desarrollo local.
