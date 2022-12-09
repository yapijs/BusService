package io.busservice.ticketprice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Passenger {

    private PassengerType passengerType;
    private int luggageCount;

    public Passenger(PassengerType passengerType, int luggageCount) {
        this.passengerType = passengerType;
        this.luggageCount = luggageCount;
    }
}
