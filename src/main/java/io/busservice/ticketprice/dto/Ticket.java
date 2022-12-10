package io.busservice.ticketprice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Ticket {
    private String priceDescription;
    private BigDecimal total;

    public Ticket(String priceDescription, BigDecimal total) {
        this.priceDescription = priceDescription;
        this.total = total;
    }
}
