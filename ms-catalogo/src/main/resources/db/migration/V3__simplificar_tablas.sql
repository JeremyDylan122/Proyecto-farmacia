UPDATE tipo_receta 
SET nombre = 'Cheque' 
WHERE nombre = 'Cheque o Receta Magistral'; --Se cambia a un nombre mas simple.

ALTER TABLE tipo_receta 
DROP COLUMN descripcion; -- Se borra descripcion al ser un dato redundante.

--Aqui simplificamos los nombres de las categorias para que sean mas simples y standar.
UPDATE categoria SET nombre = 'dermatocosmetica' WHERE nombre = 'Dermatocosmética';
UPDATE categoria SET nombre = 'medicamentos' WHERE nombre = 'Medicamentos';
UPDATE categoria SET nombre = 'infantil-y-maternidad' WHERE nombre = 'Infantil y Maternidad';
UPDATE categoria SET nombre = 'belleza' WHERE nombre = 'Belleza';
UPDATE categoria SET nombre = 'vitaminas-y-suplementos' WHERE nombre = 'Vitaminas y Suplementos';
UPDATE categoria SET nombre = 'higiene-y-personal' WHERE nombre = 'Higiene y Cuidado Personal';
UPDATE categoria SET nombre = 'cuidado-adulto' WHERE nombre = 'Cuidado Adulto';
UPDATE categoria SET nombre = 'bienestar-sexual' WHERE nombre = 'Bienestar Sexual';
