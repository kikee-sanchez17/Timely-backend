CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY ,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL
);

CREATE TABLE categories (
    category_id BIGSERIAL PRIMARY KEY,
    code TEXT NOT NULL UNIQUE,
    display_name TEXT NOT NULL
);

CREATE TABLE countries (
    code CHAR(2) PRIMARY KEY,
    name_en TEXT NOT NULL,
    flag_emoji TEXT
);

CREATE TABLE country_timezones (
    country_code CHAR(2) NOT NULL REFERENCES countries(code),
    timezone_id TEXT NOT NULL,
    PRIMARY KEY (country_code, timezone_id)
);

CREATE TABLE businesses (
    business_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    name TEXT NOT NULL,
    info TEXT,
    category_id BIGINT NOT NULL REFERENCES categories(category_id),
    country_code CHAR(2) NOT NULL,
    city TEXT NOT NULL,
    timezone_id TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (country_code, timezone_id)
        REFERENCES country_timezones(country_code, timezone_id)
);

CREATE TABLE employees (
    employee_id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(business_id) ,
    name TEXT NOT NULL,
    surname TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()

);

CREATE TABLE business_schedule_exceptions (
    business_schedule_exception_id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(business_id),
    date DATE NOT NULL,
    reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (business_id, date)

);
