-- Konto administratora (login: admin, haslo: admin123)
INSERT INTO users (username, password, email, role, enabled, created_at)
VALUES ('admin',
        '$2b$10$/xAmsuy0etNHfPEoCOIAVeucgWZMEiFrdOgY18G8q5Y0aSysfWqqm',
        'admin@bookstore.local',
        'ADMIN',
        TRUE,
        now());
