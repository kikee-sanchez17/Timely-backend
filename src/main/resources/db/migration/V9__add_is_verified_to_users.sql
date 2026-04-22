UPDATE users SET name = 'owner' WHERE email = 'owner@timelydemo.com';
UPDATE users SET surname = 'demo' WHERE email = 'owner@timelydemo.com';
UPDATE users SET name = 'customer' WHERE email = 'client@timelydemo.com';
UPDATE users SET surname = 'demo' WHERE email = 'client@timelydemo.com';

-- Add is_verified column
ALTER TABLE users
    ADD COLUMN is_verified BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE users SET is_verified = TRUE WHERE email = 'owner@timelydemo.com';
UPDATE users SET is_verified = TRUE WHERE email = 'client@timelydemo.com';

UPDATE users SET password_hash = '$2a$10$rBTOnE87Yjbcdqv/mthyi.S3lRgWjrncAZ2N5gKgsXU57pkU/wj3e' WHERE email = 'owner@timelydemo.com';
UPDATE users SET password_hash = '$2a$10$rBTOnE87Yjbcdqv/mthyi.S3lRgWjrncAZ2N5gKgsXU57pkU/wj3e' WHERE email = 'client@timelydemo.com';

-- Make name and surname not null
ALTER TABLE users
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN surname SET NOT NULL;

-- Insert admin user
INSERT INTO users (email, password_hash, name, surname, created_at, is_active, is_verified)
VALUES (
           'enricmontoya@gmail.com',
           '$2a$10$rBTOnE87Yjbcdqv/mthyi.S3lRgWjrncAZ2N5gKgsXU57pkU/wj3e', -- bcrypt hash
           'Enric',
           'Sanchez',
           NOW(),
           TRUE,
           TRUE
       );

-- Assign admin role (assuming role_id 1 = ROLE_ADMIN)
INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT user_id FROM users WHERE email = 'enricmontoya@gmail.com'),
           (SELECT role_id FROM roles WHERE code = 'ADMIN')
       );