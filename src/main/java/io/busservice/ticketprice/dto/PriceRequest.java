package io.busservice.ticketprice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PriceRequest {
    @NotBlank
    private String from;
    @NotBlank
    private String to;

    @Valid
    private Passenger[] passengers;
}
