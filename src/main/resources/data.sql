-- USERS
INSERT INTO users (id, email, password, first_name, last_name, phone_number)
VALUES
    (1, 'jan.kowalski@gmail.com', 'haslo1', 'Jan', 'Kowalski', '500111222'),
    (2, 'anna.nowak@gmail.com', 'haslo2', 'Anna', 'Nowak', '500222333'),
    (3, 'piotr.zielinski@gmail.com', 'haslo3', 'Piotr', 'Zielinski', '500333444'),
    (4, 'maria.wojcik@gmail.com', 'haslo4', 'Maria', 'Wojcik', '500444555'),
    (5, 'tomasz.mazur@gmail.com', 'haslo5', 'Tomasz', 'Mazur', '500555666'),
    (6, 'katarzyna.krawczyk@gmail.com', 'haslo6', 'Katarzyna', 'Krawczyk', '500666777'),
    (7, 'michal.kaczmarek@gmail.com', 'haslo7', 'Michal', 'Kaczmarek', '500777888'),
    (8, 'agnieszka.adamczyk@gmail.com', 'haslo8', 'Agnieszka', 'Adamczyk', '500888999'),
    (9, 'pawel.dudek@gmail.com', 'haslo9', 'Pawel', 'Dudek', '500999000'),
    (10, 'karolina.zajac@gmail.com', 'haslo10', 'Karolina', 'Zajac', '500101010'),
    (11, 'adam.wisniewski@medico.pl', 'haslo11', 'Adam', 'Wisniewski', '600111222'),
    (12, 'ewa.kaminska@zdrowie.pl', 'haslo12', 'Ewa', 'Kaminska', '600222333'),
    (13, 'marek.lewandowski@med.pl', 'haslo13', 'Marek', 'Lewandowski', '600333444'),
    (14, 'zofia.wozniak@zdrowie.pl', 'haslo14', 'Zofia', 'Wozniak', '600444555'),
    (15, 'krzysztof.szymanski@med.pl', 'haslo15', 'Krzysztof', 'Szymanski', '600555666'),
    (16, 'joanna.dabrowska@medico.pl', 'haslo16', 'Joanna', 'Dabrowska', '600666777'),
    (17, 'lukasz.kozlowski@medico.pl', 'haslo17', 'Lukasz', 'Kozlowski', '600777888'),
    (18, 'magdalena.jankowska@zdrowie.pl', 'haslo18', 'Magdalena', 'Jankowska', '600888999'),
    (19, 'marcin.wojciechowski@med.pl', 'haslo19', 'Marcin', 'Wojciechowski', '600999000'),
    (20, 'elzbieta.kwiatkowska@medico.pl', 'haslo20', 'Elzbieta', 'Kwiatkowska', '600101010');

-------------------------------------------------------

-- PATIENTS (20 rekordow)
INSERT INTO patient (id, id_card_no, birthday, user_id)
VALUES
    (1, 'ABC123456', '1998-05-20', 1),
    (2, 'XYZ987654', '2001-11-10', 2),
    (3, 'DEF456789', '1985-03-15', 3),
    (4, 'GHI321654', '1992-07-04', 4),
    (5, 'JKL789123', '1978-12-01', 5),
    (6, 'MNO654987', '2003-01-22', 6),
    (7, 'PQR147258', '1995-09-30', 7),
    (8, 'STU258369', '1980-04-18', 8),
    (9, 'VWX369147', '1967-08-12', 9),
    (10, 'YZA951753', '1999-10-05', 10),
    (11, 'BCD852963', '1990-02-28', 1),
    (12, 'EFG741852', '1988-06-14', 2),
    (13, 'HIJ963852', '1975-11-25', 3),
    (14, 'KLM159357', '2000-03-08', 4),
    (15, 'NOP357951', '1982-05-17', 5),
    (16, 'QRS486259', '1994-09-02', 6),
    (17, 'TUV753159', '2002-12-19', 7),
    (18, 'WXY159487', '1970-07-11', 8),
    (19, 'ZAB357159', '1996-04-23', 9),
    (20, 'CDE951357', '1989-01-31', 10);

-------------------------------------------------------

-- ADDRESSES (20 rekordow)
INSERT INTO address (id, city, street, house_number, postal_code)
VALUES
    (1, 'Warszawa', 'Marszalkowska', '10', '00-001'),
    (2, 'Krakow', 'Dluga', '25', '30-001'),
    (3, 'Wroclaw', 'Swidnicka', '5', '50-001'),
    (4, 'Poznan', 'Polwiejska', '12', '60-001'),
    (5, 'Gdansk', 'Dlugi Targ', '8', '80-001'),
    (6, 'Lodz', 'Piotrkowska', '102', '90-001'),
    (7, 'Katowice', 'Stawowa', '3', '40-001'),
    (8, 'Szczecin', 'Wojska Polskiego', '45', '70-001'),
    (9, 'Bydgoszcz', 'Gdanska', '18', '85-001'),
    (10, 'Lublin', 'Krakowskie Przedmiescie', '20', '20-001'),
    (11, 'Bialystok', 'Lipowa', '7', '15-001'),
    (12, 'Gdynia', 'Swietojanska', '33', '81-001'),
    (13, 'Rzeszow', '3 Maja', '14', '35-001'),
    (14, 'Torun', 'Szeroka', '9', '87-001'),
    (15, 'Kielce', 'Sienkiewicza', '50', '25-001'),
    (16, 'Gliwice', 'Zwyciestwa', '22', '44-100'),
    (17, 'Olsztyn', 'Prosta', '2', '10-001'),
    (18, 'Bielsko-Biala', '1 Maja', '11', '43-300'),
    (19, 'Opole', 'Krakowska', '15', '45-001'),
    (20, 'Zielona Gora', 'Kupiecka', '6', '65-001');

-------------------------------------------------------

-- CLINICS (20 rekordow)
INSERT INTO clinic (id, name, address_id)
VALUES
    (1, 'Medico Central', 1),
    (2, 'Zdrowie Plus', 2),
    (3, 'Silesia Med', 3),
    (4, 'Wielkopolska Clinic', 4),
    (5, 'Nadmorskie Centrum Medyczne', 5),
    (6, 'Atlas Med', 6),
    (7, 'Silesia Heart', 7),
    (8, 'Pomerania Clinic', 8),
    (9, 'Kujawy Med', 9),
    (10, 'Lubelskie Centrum Zdrowia', 10),
    (11, 'Podlaskie Med', 11),
    (12, 'Baltic Health', 12),
    (13, 'Podkarpackie Centrum Medyczne', 13),
    (14, 'Torunskie Zdrowie', 14),
    (15, 'Swietokrzyskie Med', 15),
    (16, 'Gliwice Care', 16),
    (17, 'Warmia Med', 17),
    (18, 'Beskid Med', 18),
    (19, 'Opole Clinique', 19),
    (20, 'Lubuskie Centrum Zdrowia', 20);

-------------------------------------------------------

-- DOCTORS (20 rekordow)
INSERT INTO doctor (id, specialization, user_id)
VALUES
    (1, 'Kardiolog', 11),
    (2, 'Dermatolog', 12),
    (3, 'Pediatra', 13),
    (4, 'Neurolog', 14),
    (5, 'Okulista', 15),
    (6, 'Ortopeda', 16),
    (7, 'Laryngolog', 17),
    (8, 'Ginekolog', 18),
    (9, 'Endokrynolog', 19),
    (10, 'Psychiatra', 20),
    (11, 'Chirurg', 11),
    (12, 'Internista', 12),
    (13, 'Urolog', 13),
    (14, 'Gastroenterolog', 14),
    (15, 'Onkolog', 15),
    (16, 'Reumatolog', 16),
    (17, 'Pulmonolog', 17),
    (18, 'Alergolog', 18),
    (19, 'Nefrolog', 19),
    (20, 'Diabetolog', 20);

-------------------------------------------------------

-- DOCTOR_CLINIC (20 rekordow)
INSERT INTO doctor_clinic (doctor_id, clinic_id)
VALUES
    (1, 1), (2, 2), (3, 3), (4, 4),
    (5, 5), (6, 6), (7, 7), (8, 8),
    (9, 9), (10, 10), (11, 11), (12, 12),
    (13, 13), (14, 14), (15, 15), (16, 16),
    (17, 17), (18, 18), (19, 19), (20, 20);

-------------------------------------------------------

-- VISITS (20 rekordow)
INSERT INTO visit (id, start_date, finish_date, doctor_id, patient_id)
VALUES
    (1, '2026-08-20 10:00:00', '2026-08-20 10:30:00', 1, 1),
    (2, '2026-08-20 11:00:00', '2026-08-20 11:30:00', 2, NULL),
    (3, '2026-08-21 09:00:00', '2026-08-21 09:30:00', 3, 2),
    (4, '2026-08-21 10:00:00', '2026-08-21 10:30:00', 4, NULL),
    (5, '2026-08-22 08:00:00', '2026-08-22 08:30:00', 5, 3),
    (6, '2026-08-22 09:00:00', '2026-08-22 09:30:00', 6, 4),
    (7, '2026-08-22 10:00:00', '2026-08-22 10:30:00', 7, 5),
    (8, '2026-08-23 11:00:00', '2026-08-23 11:30:00', 8, 6),
    (9, '2026-08-23 12:00:00', '2026-08-23 12:30:00', 9, NULL),
    (10, '2026-08-24 09:15:00', '2026-08-24 09:45:00', 10, 7),
    (11, '2026-08-24 10:00:00', '2026-08-24 10:30:00', 11, 8),
    (12, '2026-08-25 13:00:00', '2026-08-25 13:30:00', 12, 9),
    (13, '2026-08-25 14:00:00', '2026-08-25 14:30:00', 13, 10),
    (14, '2026-08-26 08:00:00', '2026-08-26 08:30:00', 14, 11),
    (15, '2026-08-26 09:30:00', '2026-08-26 10:00:00', 15, NULL),
    (16, '2026-08-27 10:00:00', '2026-08-27 10:30:00', 16, 12),
    (17, '2026-08-27 11:00:00', '2026-08-27 11:30:00', 17, 13),
    (18, '2026-08-28 12:00:00', '2026-08-28 12:30:00', 18, 14),
    (19, '2026-08-28 13:00:00', '2026-08-28 13:30:00', 19, 15),
    (20, '2026-08-29 15:00:00', '2026-08-29 15:30:00', 20, 16);