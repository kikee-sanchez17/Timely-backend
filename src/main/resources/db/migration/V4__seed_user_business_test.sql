-- =========================================================
-- USERS
-- =========================================================

INSERT INTO users (email, password_hash)
VALUES
    ('owner@timelydemo.com', '$2a$10$ownerDemoHash123456789'),
    ('client@timelydemo.com', '$2a$10$clientDemoHash123456789');

-- =========================================================
-- BUSINESS
-- =========================================================

INSERT INTO businesses (
    user_id,
    name,
    info,
    category_id,
    country_code,
    city,
    timezone_id,
    is_active
)
VALUES (
           (SELECT user_id FROM users WHERE email = 'owner@timelydemo.com'),
           'Sharp Studio Barber',
           'Modern barber shop specialized in haircuts, beard grooming and premium styling.',
           (SELECT category_id FROM categories WHERE code = 'BARBER'),
           'ES',
           'Barcelona',
           'Europe/Madrid',
           TRUE
       );

-- =========================================================
-- EMPLOYEES
-- =========================================================

INSERT INTO employees (
    business_id,
    name,
    surname,
    is_active
)
VALUES
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        'Marc',
        'Soler',
        TRUE
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        'David',
        'Romero',
        TRUE
    );

-- =========================================================
-- SERVICES
-- =========================================================

INSERT INTO services (
    name,
    business_id,
    description,
    is_active
)
VALUES
    (
        'Haircuts',
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        'Haircut services for different styles and preferences.',
        TRUE
    ),
    (
        'Beard',
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        'Beard trimming, shaping and full grooming services.',
        TRUE
    );

-- =========================================================
-- SUBSERVICES
-- =========================================================

INSERT INTO subservices (
    name,
    description,
    price,
    duration_minutes,
    service_id,
    is_active
)
VALUES
    (
        'Classic Haircut',
        'Traditional haircut with clipper and scissors.',
        15.00,
        30,
        (
            SELECT service_id
            FROM services
            WHERE name = 'Haircuts'
              AND business_id = (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber')
        ),
        TRUE
    ),
    (
        'Skin Fade',
        'Fade haircut with more detailed blending work.',
        18.00,
        45,
        (
            SELECT service_id
            FROM services
            WHERE name = 'Haircuts'
              AND business_id = (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber')
        ),
        TRUE
    ),
    (
        'Beard Trim',
        'Quick beard trim and clean up.',
        10.00,
        20,
        (
            SELECT service_id
            FROM services
            WHERE name = 'Beard'
              AND business_id = (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber')
        ),
        TRUE
    ),
    (
        'Full Beard Grooming',
        'Beard shaping, contouring and hot towel finish.',
        16.00,
        30,
        (
            SELECT service_id
            FROM services
            WHERE name = 'Beard'
              AND business_id = (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber')
        ),
        TRUE
    );

-- =========================================================
-- EMPLOYEE_SUBSERVICES
-- Marc hace todos los servicios
-- David hace haircut clásico y beard trim
-- =========================================================

-- Marc Soler
INSERT INTO employee_subservices (subservice_id, employee_id)
VALUES
    (
        (
            SELECT s.subservice_id
            FROM subservices s
                     JOIN services sv ON sv.service_id = s.service_id
                     JOIN businesses b ON b.business_id = sv.business_id
            WHERE s.name = 'Classic Haircut'
              AND b.name = 'Sharp Studio Barber'
        ),
        (
            SELECT employee_id
            FROM employees
            WHERE name = 'Marc'
              AND surname = 'Soler'
              AND business_id = (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber')
        )
    ),
    (
        (
            SELECT s.subservice_id
            FROM subservices s
                     JOIN services sv ON sv.service_id = s.service_id
                     JOIN businesses b ON b.business_id = sv.business_id
            WHERE s.name = 'Skin Fade'
              AND b.name = 'Sharp Studio Barber'
        ),
        (
            SELECT employee_id
            FROM employees
            WHERE name = 'Marc'
              AND surname = 'Soler'
              AND business_id = (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber')
        )
    ),
    (
        (
            SELECT s.subservice_id
            FROM subservices s
                     JOIN services sv ON sv.service_id = s.service_id
                     JOIN businesses b ON b.business_id = sv.business_id
            WHERE s.name = 'Beard Trim'
              AND b.name = 'Sharp Studio Barber'
        ),
        (
            SELECT employee_id
            FROM employees
            WHERE name = 'Marc'
              AND surname = 'Soler'
              AND business_id = (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber')
        )
    ),
    (
        (
            SELECT s.subservice_id
            FROM subservices s
                     JOIN services sv ON sv.service_id = s.service_id
                     JOIN businesses b ON b.business_id = sv.business_id
            WHERE s.name = 'Full Beard Grooming'
              AND b.name = 'Sharp Studio Barber'
        ),
        (
            SELECT employee_id
            FROM employees
            WHERE name = 'Marc'
              AND surname = 'Soler'
              AND business_id = (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber')
        )
    );

-- David Romero
INSERT INTO employee_subservices (subservice_id, employee_id)
VALUES
    (
        (
            SELECT s.subservice_id
            FROM subservices s
                     JOIN services sv ON sv.service_id = s.service_id
                     JOIN businesses b ON b.business_id = sv.business_id
            WHERE s.name = 'Classic Haircut'
              AND b.name = 'Sharp Studio Barber'
        ),
        (
            SELECT employee_id
            FROM employees
            WHERE name = 'David'
              AND surname = 'Romero'
              AND business_id = (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber')
        )
    ),
    (
        (
            SELECT s.subservice_id
            FROM subservices s
                     JOIN services sv ON sv.service_id = s.service_id
                     JOIN businesses b ON b.business_id = sv.business_id
            WHERE s.name = 'Beard Trim'
              AND b.name = 'Sharp Studio Barber'
        ),
        (
            SELECT employee_id
            FROM employees
            WHERE name = 'David'
              AND surname = 'Romero'
              AND business_id = (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber')
        )
    );