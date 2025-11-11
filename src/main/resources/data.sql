-- =============================================================================
-- Script de inicialización de datos para Looma Core
-- Base de datos: PostgreSQL
-- =============================================================================

-- -----------------------------------------------------------------------------
-- CATEGORÍAS LITERARIAS
-- -----------------------------------------------------------------------------
INSERT INTO category (name) VALUES
('Ficción'),
('No Ficción'),
('Fantasía'),
('Ciencia Ficción'),
('Romance'),
('Misterio'),
('Thriller'),
('Terror'),
('Aventura'),
('Histórica'),
('Biografía'),
('Poesía'),
('Drama'),
('Comedia'),
('Distopía'),
('Realismo Mágico'),
('Literatura Infantil'),
('Literatura Juvenil'),
('Ensayo'),
('Autoayuda');

-- -----------------------------------------------------------------------------
-- FORMATOS DE LIBRO
-- -----------------------------------------------------------------------------
INSERT INTO format (name) VALUES
('Novela'),
('Cuento'),
('Novela Corta'),
('Microrrelato'),
('Relato'),
('Novela Gráfica'),
('Antología');

-- -----------------------------------------------------------------------------
-- IDIOMAS (Lenguas latinas + Inglés)
-- -----------------------------------------------------------------------------
INSERT INTO language (code, name) VALUES
('es', 'Español'),
('en', 'Inglés'),
('pt', 'Portugués'),
('fr', 'Francés'),
('it', 'Italiano'),
('ro', 'Rumano'),
('ca', 'Catalán'),
('gl', 'Gallego');

-- -----------------------------------------------------------------------------
-- USUARIO DE PRUEBA
-- -----------------------------------------------------------------------------
INSERT INTO "user" (name, surname, username, email, password, photo, money) VALUES
('Juan', 'Pérez', 'jperez', 'juan.perez@example.com', '1234', 'none', 100.00);

-- -----------------------------------------------------------------------------
-- FIN DEL SCRIPT
-- -----------------------------------------------------------------------------

