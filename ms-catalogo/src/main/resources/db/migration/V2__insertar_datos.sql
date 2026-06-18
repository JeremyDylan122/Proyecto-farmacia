-- 1. TIPOS DE RECETA
INSERT INTO tipo_receta (nombre, descripcion) VALUES 
('Venta Libre', 'Medicamentos que no requieren receta médica para su compra.'),
('Receta Simple', 'Medicamentos que requieren presentar receta, pero esta se devuelve al cliente.'),
('Receta Retenida', 'Medicamentos donde la receta debe quedar archivada en la farmacia (ej. antibióticos).'),
('Cheque o Receta Magistral', 'Medicamentos con control estricto (ej. estupefacientes o psicotrópicos).');

-- 2. CATEGORÍAS
INSERT INTO categoria (nombre) VALUES  
('Dermatocosmética'),          -- ID 1
('Medicamentos'),              -- ID 2
('Infantil y Maternidad'),     -- ID 3
('Belleza'),                   -- ID 4
('Vitaminas y Suplementos'),   -- ID 5
('Higiene y Cuidado Personal'),-- ID 6
('Cuidado Adulto'),            -- ID 7
('Bienestar Sexual');          -- ID 8

-- 3. PRODUCTOS
INSERT INTO producto (sku, nombre, precio, laboratorio, descripcion, id_tipo_receta, id_categoria) VALUES 
-- Dermatocosmética (Cat 1)
(780001, 'Protector Solar Anthelios XL', 24990.00, 'La Roche-Posay', 'Protector solar FPS 50+', 1, 1),
(780002, 'Sérum Hyalu B5', 32500.00, 'La Roche-Posay', 'Reparador de arrugas', 1, 1),

-- Medicamentos (Cat 2)
(780004, 'Ibuprofeno 600mg', 2800.00, 'Mintlab', 'Analgésico venta libre', 1, 2),
(780017, 'Losartán Potásico 50mg', 3200.00, 'Lab Chile', 'Receta Simple: Hipertensión', 2, 2),
(780018, 'Ciprofloxacino 500mg', 5900.00, 'Mintlab', 'Receta Retenida: Antibiótico', 3, 2),
(780019, 'Fentanilo Parche 25mcg', 45000.00, 'Janssen', 'Receta Cheque: Dolor crónico', 4, 2),

-- Infantil (Cat 3)
(780005, 'Pañales Premium Care G', 18990.00, 'Pampers', 'Pack 60 unidades', 1, 3),
(780006, 'Leche Nido Etapa 1+', 11500.00, 'Nestlé', 'Tarro 800g', 1, 3),

-- Belleza (Cat 4)
(780007, 'Labial Matte Red', 8900.00, 'Vichy', 'Larga duración', 1, 4),
(780008, 'Esmalte Fortalecedor', 5400.00, 'Mavala', 'Para uñas quebradizas', 1, 4),

-- Vitaminas (Cat 5)
(780009, 'Centrum Adulto', 15990.00, 'Pfizer', 'Multivitamínico', 1, 5),
(780010, 'Magnesio 200mg', 9900.00, 'Nature Made', 'Suplemento muscular', 1, 5),

-- Higiene (Cat 6)
(780011, 'Jabón Antibacterial', 3200.00, 'Protex', 'Protección gérmenes', 1, 6),
(780012, 'Pasta Dental Total 12', 2990.00, 'Colgate', 'Salud bucal', 1, 6),

-- Cuidado Adulto (Cat 7)
(780013, 'Pañal Adulto Plenitud G', 12400.00, 'Kimberly-Clark', 'Máxima absorción', 1, 7),
(780014, 'Crema Antiescaras 100g', 7800.00, 'Simond’s', 'Protección piel', 1, 7),

-- Bienestar Sexual (Cat 8)
(780015, 'Condones Sensitive 3u', 3500.00, 'Durex', 'Ultra finos', 1, 8),
(780016, 'Gel Lubricante KY 50g', 6900.00, 'KY', 'Base agua', 1, 8);