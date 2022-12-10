package io.busservice.ticketprice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PricesDraft {
    private List<String> ticketPrices;
    private String totalPrice;

    public PricesDraft(List<String> ticketPrices, String totalPrice) {
        this.ticketPrices = ticketPrices;
        this.totalPrice = totalPrice;
    }
}
