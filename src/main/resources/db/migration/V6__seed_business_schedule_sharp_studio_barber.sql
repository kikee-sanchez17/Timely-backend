INSERT INTO business_schedule (business_id, day_of_week, start_time, end_time)
VALUES
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        1, '10:00:00', '14:00:00'
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        1, '16:00:00', '20:00:00'
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        2, '10:00:00', '14:00:00'
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        2, '16:00:00', '20:00:00'
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        3, '10:00:00', '14:00:00'
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        3, '16:00:00', '20:00:00'
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        4, '10:00:00', '14:00:00'
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        4, '16:00:00', '20:00:00'
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        5, '10:00:00', '14:00:00'
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        5, '16:00:00', '20:00:00'
    ),
    (
        (SELECT business_id FROM businesses WHERE name = 'Sharp Studio Barber'),
        6, '10:00:00', '14:00:00'
    );