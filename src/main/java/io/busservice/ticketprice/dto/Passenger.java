package io.busservice.ticketprice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Passenger {

    @NotNull
    private PassengerType passengerType;
    private int luggageCount;

    public Passenger(PassengerType passengerType, int luggageCount) {
        this.passengerType = passengerType;
        this.luggageCount = luggageCount;
    }
}
