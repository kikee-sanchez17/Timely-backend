ALTER TABLE bookings
DROP CONSTRAINT IF EXISTS bookings_status_check;

ALTER TABLE bookings
ALTER COLUMN status TYPE TEXT
USING status::text;

ALTER TABLE bookings
    ALTER COLUMN status SET DEFAULT 'PENDING';

ALTER TABLE bookings
    ADD CONSTRAINT bookings_status_check
        CHECK (status IN (
                          'PENDING',
                          'CONFIRMED',
                          'CANCELLED_BY_CUSTOMER',
                          'CANCELLED_BY_BUSINESS',
                          'COMPLETED',
                          'NO_SHOW'
            ));

DROP TYPE IF EXISTS booking_status;