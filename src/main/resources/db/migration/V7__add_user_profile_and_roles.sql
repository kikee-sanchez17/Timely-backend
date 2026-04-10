-- =========================================
-- Add profile fields to users
-- =========================================

ALTER TABLE users
    ADD COLUMN name TEXT,
    ADD COLUMN surname TEXT,
    ADD COLUMN phone_number TEXT,
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

-- =========================================
-- Roles
-- =========================================

CREATE TABLE roles (
                       role_id BIGSERIAL PRIMARY KEY,
                       code TEXT NOT NULL UNIQUE
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                            role_id BIGINT NOT NULL REFERENCES roles(role_id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

-- =========================================
-- Seed roles
-- =========================================

INSERT INTO roles (code) VALUES
                             ('CUSTOMER'),
                             ('BUSINESS_OWNER'),
                             ('ADMIN');

-- =========================================
-- Assign CUSTOMER role to all existing users
-- =========================================

INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u
         JOIN roles r ON r.code = 'CUSTOMER';

-- =========================================
-- Assign BUSINESS_OWNER role to users
-- that already own a business
-- =========================================

INSERT INTO user_roles (user_id, role_id)
SELECT DISTINCT b.user_id, r.role_id
FROM businesses b
         JOIN roles r ON r.code = 'BUSINESS_OWNER'
    ON CONFLICT DO NOTHING;