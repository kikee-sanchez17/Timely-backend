# Timely Backend Project Context

Project name: Timely

Timely is a booking system currently under development. It focuses on reliable availability calculation while accounting for edge cases such as business schedules, employee schedules, exception intervals, overlapping bookings, cancellations and rescheduling.

Tech stack:
- Java 21
- Spring Boot
- PostgreSQL
- Flyway
- JPA/Hibernate
- JWT authentication
- Docker
- Angular frontend

Architecture goals:
- Clean backend architecture
- Clear module separation
- Maintainable service layer
- Explicit domain logic
- Strong validation
- Robust edge-case handling
- Practical testing strategy

Important domain concepts:
- Business schedules define default availability
- Employee schedules may override or refine availability
- Exception intervals can open or close specific time ranges
- Bookings block availability
- Cancelled bookings should free slots
- Availability must respect timezone logic
- Race conditions must be considered when two users book the same slot

Coding preferences for this project:
- Prefer clear service methods over clever abstractions
- Keep booking logic understandable
- Avoid hiding business rules too deeply
- Use DTOs for API boundaries
- Use repositories only for persistence access
- Prefer tests for business-critical logic
- Mention possible edge cases when modifying booking logic
