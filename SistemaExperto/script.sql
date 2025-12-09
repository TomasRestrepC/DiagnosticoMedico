DROP DATABASE IF EXISTS DiagnosticoMedico;
CREATE DATABASE DiagnosticoMedico;
USE DiagnosticoMedico;

-- 1. Tabla de Enfermedades
CREATE TABLE enfermedades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    categoria VARCHAR(50),
    recomendacion TEXT
);

-- 2. Tabla de Síntomas por Enfermedad
CREATE TABLE sintomas_enfermedad (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enfermedad_id INT,
    sintoma VARCHAR(50),
    FOREIGN KEY (enfermedad_id) REFERENCES enfermedades(id)
);

-- 3. Tabla Historial de Pacientes
CREATE TABLE historial_pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_paciente VARCHAR(100),
    edad INT,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    enfermedad_diagnosticada VARCHAR(50),
    categoria VARCHAR(50)
);

-- 1. Gripe (Viral)
INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES 
('Gripe', 'viral', 'Descansar, hidratar, consultar médico');
SET @id = LAST_INSERT_ID();
INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES 
(@id, 'fiebre'), (@id, 'tos'), (@id, 'dolor_cabeza'), (@id, 'dolor_muscular');

-- 2. Resfriado (Viral)
INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES 
('Resfriado', 'viral', 'Descansar, hidratar');
SET @id = LAST_INSERT_ID();
INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES 
(@id, 'tos'), (@id, 'estornudos'), (@id, 'dolor_garganta');

-- 3. Diabetes (Crónica)
INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES 
('Diabetes', 'crónica', 'Controlar dieta, consultar especialista');
SET @id = LAST_INSERT_ID();
INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES 
(@id, 'sed'), (@id, 'cansancio'), (@id, 'perdida_peso');

-- 4. COVID-19 (Viral)
INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES 
('COVID-19', 'viral', 'Aislamiento, consultar médico');
SET @id = LAST_INSERT_ID();
INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES 
(@id, 'fiebre'), (@id, 'tos'), (@id, 'cansancio'), (@id, 'perdida_gusto_olfato');

-- 5. Varicela (Viral)
INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES 
('Varicela', 'viral', 'Descansar, evitar rascar lesiones');
SET @id = LAST_INSERT_ID();
INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES 
(@id, 'fiebre'), (@id, 'erupcion'), (@id, 'picazon');

-- 6. Migraña (Crónica)
INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES 
('Migraña', 'crónica', 'Descansar, evitar luz intensa');
SET @id = LAST_INSERT_ID();
INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES 
(@id, 'dolor_cabeza'), (@id, 'nausea'), (@id, 'sensibilidad_luz');

-- 7. Alergia (Alergia)
INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES 
('Alergia', 'alergia', 'Evitar alérgenos, antihistamínicos');
SET @id = LAST_INSERT_ID();
INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES 
(@id, 'estornudos'), (@id, 'picazon'), (@id, 'ojos_lagrimosos');

-- 8. Hipotiroidismo (Crónica)
INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES 
('Hipotiroidismo', 'crónica', 'Control médico y medicación');
SET @id = LAST_INSERT_ID();
INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES 
(@id, 'cansancio'), (@id, 'aumento_peso'), (@id, 'piel_seca');

-- 9. Gastroenteritis (Viral)
INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES 
('Gastroenteritis', 'viral', 'Hidratación, dieta ligera');
SET @id = LAST_INSERT_ID();
INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES 
(@id, 'vomito'), (@id, 'diarrea'), (@id, 'dolor_abdominal'), (@id, 'fiebre');

-- 10. Faringitis (Viral/Bacteriana)
INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES 
('Faringitis', 'viral_bacteriana', 'Consultar médico');
SET @id = LAST_INSERT_ID();
INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES 
(@id, 'dolor_garganta'), (@id, 'fiebre'), (@id, 'tos');

SELECT nombre_paciente, enfermedad_diagnosticada, categoria FROM historial_pacientes ORDER BY fecha DESC LIMIT 1;