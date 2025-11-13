-- =============================================================================
-- Script de inicialización de datos para Looma Core
-- Base de datos: PostgreSQL
-- Este script está escrito de forma idempotente: si ya existen los registros
-- no se volverán a insertar en ejecuciones posteriores.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- CATEGORÍAS LITERARIAS (idempotente)
-- -----------------------------------------------------------------------------
INSERT INTO category (name)
SELECT 'Ficción'      WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Ficción');
INSERT INTO category (name)
SELECT 'No Ficción'   WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'No Ficción');
INSERT INTO category (name)
SELECT 'Fantasía'     WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Fantasía');
INSERT INTO category (name)
SELECT 'Ciencia Ficción' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Ciencia Ficción');
INSERT INTO category (name)
SELECT 'Romance'      WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Romance');
INSERT INTO category (name)
SELECT 'Misterio'     WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Misterio');
INSERT INTO category (name)
SELECT 'Thriller'     WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Thriller');
INSERT INTO category (name)
SELECT 'Terror'       WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Terror');
INSERT INTO category (name)
SELECT 'Aventura'     WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Aventura');
INSERT INTO category (name)
SELECT 'Histórica'    WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Histórica');
INSERT INTO category (name)
SELECT 'Drama'        WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Drama');
INSERT INTO category (name)
SELECT 'Comedia'      WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Comedia');
INSERT INTO category (name)
SELECT 'Distopía'     WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Distopía');
INSERT INTO category (name)
SELECT 'Realismo Mágico' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Realismo Mágico');
INSERT INTO category (name)
SELECT 'Literatura Infantil' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Literatura Infantil');
INSERT INTO category (name)
SELECT 'Literatura Juvenil' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Literatura Juvenil');
INSERT INTO category (name)
SELECT 'Autoayuda'    WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Autoayuda');

-- -----------------------------------------------------------------------------
-- FORMATOS DE LIBRO (idempotente)
-- -----------------------------------------------------------------------------
INSERT INTO format (name)
SELECT 'Novela'       WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Novela');
INSERT INTO format (name)
SELECT 'Cuento'       WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Cuento');
INSERT INTO format (name)
SELECT 'Novela Corta' WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Novela Corta');
INSERT INTO format (name)
SELECT 'Microrrelato' WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Microrrelato');
INSERT INTO format (name)
SELECT 'Relato'       WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Relato');
INSERT INTO format (name)
SELECT 'Novela Gráfica' WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Novela Gráfica');
INSERT INTO format (name)
SELECT 'Antología'    WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Antología');
INSERT INTO format (name)
SELECT 'Ensayo'       WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Ensayo');
INSERT INTO format (name)
SELECT 'Biografía'    WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Biografía');
INSERT INTO format (name)
SELECT 'Autobiografía' WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Autobiografía');
INSERT INTO format (name)
SELECT 'Poesía'       WHERE NOT EXISTS (SELECT 1 FROM format WHERE name = 'Poesía');


-- -----------------------------------------------------------------------------
-- IDIOMAS (Lenguas latinas + Inglés) (idempotente)
-- Se asume que la columna "code" es el identificador único del idioma.
-- -----------------------------------------------------------------------------
INSERT INTO language (code, name)
SELECT 'es', 'Español'    WHERE NOT EXISTS (SELECT 1 FROM language WHERE code = 'es');
INSERT INTO language (code, name)
SELECT 'en', 'Inglés'     WHERE NOT EXISTS (SELECT 1 FROM language WHERE code = 'en');
INSERT INTO language (code, name)
SELECT 'pt', 'Portugués'  WHERE NOT EXISTS (SELECT 1 FROM language WHERE code = 'pt');
INSERT INTO language (code, name)
SELECT 'fr', 'Francés'    WHERE NOT EXISTS (SELECT 1 FROM language WHERE code = 'fr');
INSERT INTO language (code, name)
SELECT 'it', 'Italiano'   WHERE NOT EXISTS (SELECT 1 FROM language WHERE code = 'it');
INSERT INTO language (code, name)
SELECT 'ro', 'Rumano'     WHERE NOT EXISTS (SELECT 1 FROM language WHERE code = 'ro');
INSERT INTO language (code, name)
SELECT 'ca', 'Catalán'    WHERE NOT EXISTS (SELECT 1 FROM language WHERE code = 'ca');
INSERT INTO language (code, name)
SELECT 'gl', 'Gallego'    WHERE NOT EXISTS (SELECT 1 FROM language WHERE code = 'gl');

-- -----------------------------------------------------------------------------
-- USUARIO DE PRUEBA (idempotente)
-- Se asume que username y/o email son únicos; se evita duplicar por username.
-- La tabla se llama "user" (entre comillas) tal como se usó en el proyecto.
-- -----------------------------------------------------------------------------
INSERT INTO "user" (name, surname, username, email, password, photo, money)
SELECT 'Juan', 'Pérez', 'jperez', 'juanperez@gmail.com', 'Password1234', 'none', 100.00
WHERE NOT EXISTS (SELECT 1 FROM "user" WHERE username = 'jperez' OR email = 'juanperez@gmail.com');

-- -----------------------------------------------------------------------------
-- FIN DEL SCRIPT
-- -----------------------------------------------------------------------------

