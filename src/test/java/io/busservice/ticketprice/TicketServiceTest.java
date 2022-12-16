package io.busservice.ticketprice;

import io.busservice.baseprice.BasePriceService;
import io.busservice.taxrate.TaxRateService;
import io.busservice.ticketprice.dto.Passenger;
import io.busservice.ticketprice.dto.PassengerType;
import io.busservice.ticketprice.dto.PriceRequest;
import io.busservice.ticketprice.dto.PricesDraft;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    BasePriceService basePriceService;

    @Mock
    TaxRateService taxRateService;

    @InjectMocks
    TicketService ticketService;

    Passenger createAdultPassenger() {
        return new Passenger(PassengerType.ADULT, 2);
    }

    Passenger createChildPassenger() {
        return new Passenger(PassengerType.CHILD, 1);
    }

    PriceRequest createPriceRequest() {
        Passenger passengerAdult = createAdultPassenger();
        Passenger passengerChild = createChildPassenger();
        Passenger[] passengers = new Passenger[] { passengerAdult, passengerChild };
        return new PriceRequest("Riga", "Vilnius", passengers);
    }

    PricesDraft createPricesDraft() {
        String totalPrice = "29.04 EUR";
        List<String> allTickets = new ArrayList<>();
        allTickets.add("Adult (10.00 EUR + 21%) = 12.10 EUR");
        allTickets.add("2 bags (2 x 10.00 EUR x 30% + 21%) = 7.26 EUR");
        allTickets.add("Child (10.00 EUR x 50% + 21%) = 6.05 EUR");
        allTickets.add("1 bag (10.00 EUR x 30% + 21%) = 3.63 EUR");
        return new PricesDraft(allTickets, totalPrice);
    }

    @Test
    public void shouldCalculatePricesCorrectly() {
        PricesDraft pricesDraft = createPricesDraft();

        Mockito.doReturn(new BigDecimal(10))
                .when(basePriceService)
                .getBasePrice("Riga", "Vilnius");
        Mockito.doReturn(new BigDecimal("0.21"))
                .when(taxRateService)
                .getVat();

        PricesDraft result = ticketService.calculateTicketPrices(createPriceRequest());
        Assertions.assertEquals(result.getTotalPrice(), pricesDraft.getTotalPrice());
        Assertions.assertEquals(result.getTicketPrices(), pricesDraft.getTicketPrices());
    }

    @Test
    public void shouldThrowErrorWhenSameStartDestination() {
        PriceRequest priceRequest = new PriceRequest("Riga", "Riga", new Passenger[0]);

        ResponseStatusException thrown = Assertions.assertThrows(ResponseStatusException.class,
                () -> ticketService.calculateTicketPrices(priceRequest));

        Assertions.assertEquals("Wrong start or end location", thrown.getReason());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }
}
