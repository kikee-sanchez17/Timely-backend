package dev.esanchez.timely.backend.entity;

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED_BY_CUSTOMER,
    CANCELLED_BY_BUSINESS,
    COMPLETED,
    NO_SHOW
}