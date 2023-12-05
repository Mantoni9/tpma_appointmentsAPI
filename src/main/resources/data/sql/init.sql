-- init.sql

-- Creates the 'appointment' table if it does not exist
CREATE TABLE IF NOT EXISTS appointment (
    id BIGSERIAL PRIMARY KEY,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    patient_name VARCHAR(255),
    doctor_name VARCHAR(255),
    appointment_type VARCHAR(255),
    location VARCHAR(255)
    );

-- Inserts two appointments into the 'appointment' table
INSERT INTO appointment (start_time, end_time, patient_name, doctor_name, appointment_type, location)
VALUES
    ('2023-12-05 09:00:00', '2023-12-05 10:00:00', 'Max Mustermann', 'Dr. Schmidt', 'General Checkup', 'Room 101'),
    ('2023-12-06 11:00:00', '2023-12-06 12:00:00', 'Erika Musterfrau', 'Dr. Müller', 'Annual Checkup', 'Room 102');
