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
}
