package io.busservice.ticketprice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PassengerType {
    ADULT,
    CHILD,
    PENSIONER;

    @JsonCreator
    public static PassengerType getPassengerType(String value) {
        for (PassengerType enumValue : PassengerType.values()) {
            if (enumValue.toString().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Incorrect enum parameter:" + value);
    }
}
