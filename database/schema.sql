CREATE DATABASE IF NOT EXISTS finanzas_db;
USE finanzas_db;

INSERT INTO roles (nombre) VALUES ('ADMIN'), ('USUARIO');

-- password real: "1234" -- este hash de ejemplo NO sirve, se genera solo
-- al usar POST /api/usuarios/registro (que aplica BCrypt automaticamente)
INSERT INTO categorias (nombre, tipo, usuario_id) VALUES
('Salario', 'INGRESO', 1),
('Comida', 'GASTO', 1),
('Transporte', 'GASTO', 1),
('Ocio', 'GASTO', 1);

INSERT INTO presupuestos (categoria_id, usuario_id, monto_limite, mes) VALUES
(2, 1, 200.00, '2026-09'),
(3, 1, 80.00, '2026-09'),
(4, 1, 60.00, '2026-09');
