INSERT INTO countries (code, name_en, flag_emoji) VALUES
    ('ES', 'Spain', '🇪🇸'),
    ('FR', 'France', '🇫🇷'),
    ('US', 'United States', '🇺🇸'),
    ('GB', 'United Kingdom', '🇬🇧'),
    ('DE', 'Germany', '🇩🇪');


INSERT INTO country_timezones (country_code, timezone_id) VALUES
-- España
('ES', 'Europe/Madrid'),
('ES', 'Atlantic/Canary'),

-- Francia
('FR', 'Europe/Paris'),

-- Alemania
('DE', 'Europe/Berlin'),

-- Reino Unido
('GB', 'Europe/London'),

-- USA (ejemplo parcial)
('US', 'America/New_York'),
('US', 'America/Chicago'),
('US', 'America/Denver'),
('US', 'America/Los_Angeles');