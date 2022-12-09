package io.busservice.ticketprice;

import io.busservice.ticketprice.dto.PriceRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api")
public class TicketController {

    TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/ticket-prices")
    public String calculatePrices(@RequestBody @Valid PriceRequest priceRequest) {
        return ticketService.calculateTicketPrice(priceRequest);

    }

}
