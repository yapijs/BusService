package io.busservice.ticketprice;

import io.busservice.ticketprice.dto.PriceRequest;
import io.busservice.ticketprice.dto.PricesDraft;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/ticket-prices")
    public PricesDraft calculatePrices(@RequestBody @Valid PriceRequest priceRequest) {
        return ticketService.calculateTicketPrices(priceRequest);

    }

}
