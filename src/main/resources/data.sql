-- USERS
INSERT INTO users (id, email, password, first_name, last_name, phone_number)
VALUES
    (1, 'jan.kowalski@gmail.com', 'pass123', 'Jan', 'Kowalski', '500111222'),
    (2, 'anna.nowak@gmail.com', 'pass123', 'Anna', 'Nowak', '500222333'),
    (3, 'adam.lekarz@gmail.com', 'pass123', 'Adam', 'Wiśniewski', '500333444'),
    (4, 'ewa.lekarz@gmail.com', 'pass123', 'Ewa', 'Kamińska', '500444555');

-------------------------------------------------------

-- PATIENTS
INSERT INTO patient (id, id_card_no, birthday, user_id)
VALUES
    (1, 'ABC123456', '1998-05-20', 1),
    (2, 'XYZ987654', '2001-11-10', 2);

-------------------------------------------------------

-- ADDRESSES
INSERT INTO address (id, city, street, house_number, postal_code)
VALUES
    (1, 'Warszawa', 'Marszałkowska', '10', '00-001'),
    (2, 'Kraków', 'Długa', '25', '30-001');

-------------------------------------------------------

-- CLINICS
INSERT INTO clinic (id, name, address_id)
VALUES
    (1, 'Medico', 1),
    (2, 'Zdrowie+', 2);

-------------------------------------------------------

-- DOCTORS
INSERT INTO doctor (id, specialization, user_id)
VALUES
    (1, 'Kardiolog', 3),
    (2, 'Dermatolog', 4);

-------------------------------------------------------

-- DOCTOR_CLINIC
INSERT INTO doctor_clinic (doctor_id, clinic_id)
VALUES
    (1,1),
    (1,2),
    (2,2);

-------------------------------------------------------

-- VISITS
INSERT INTO visit (id, visit_date, doctor_id, patient_id)
VALUES
    (1, '2026-08-20 10:00:00', 1, 1),
    (2, '2026-08-20 11:00:00', 1, NULL),
    (3, '2026-08-21 09:00:00', 2, 2),
    (4, '2026-08-21 10:00:00', 2, NULL);