package io.busservice.ticketprice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PriceRequest {
    @NotBlank
    private String from;
    @NotBlank
    private String to;

    private Passenger[] passengers;

    public PriceRequest(String from, String to, Passenger[] passengers) {
        this.from = from;
        this.to = to;
        this.passengers = passengers;
    }
}
