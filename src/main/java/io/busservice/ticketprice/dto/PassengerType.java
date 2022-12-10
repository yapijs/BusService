package io.busservice.ticketprice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigDecimal;

public enum PassengerType {
    ADULT(BigDecimal.ONE),
    CHILD(BigDecimal.valueOf(0.5D)),
    PENSIONER(BigDecimal.valueOf(0.75D));

    private final BigDecimal priceMultiplier;

    PassengerType(BigDecimal priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public BigDecimal getPriceMultiplier() {
        return priceMultiplier;
    }

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
