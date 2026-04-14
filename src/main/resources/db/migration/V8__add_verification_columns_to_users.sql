ALTER TABLE users
    ADD COLUMN verification_code TEXT,
ADD COLUMN verification_expiration TIMESTAMPTZ;