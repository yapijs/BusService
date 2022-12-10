package io.busservice.ticketprice;

import io.busservice.baseprice.BasePriceService;
import io.busservice.ticketprice.dto.*;
import io.busservice.taxrate.TaxRateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final BasePriceService basePriceService;
    private final TaxRateService taxRateService;
    private static final String CURRENCY = "EUR";

    public TicketService(BasePriceService basePriceService, TaxRateService taxRateService) {
        this.basePriceService = basePriceService;
        this.taxRateService = taxRateService;
    }

    public PricesDraft calculateTicketPrices(PriceRequest priceRequest) {
        BigDecimal basePrice = basePriceService.getBasePrice(priceRequest.getFrom(), priceRequest.getTo());

        List<Ticket> listOfTickets = new ArrayList<>();
        
        for (Passenger passenger : priceRequest.getPassengers()) {
            List<Ticket> personCosts = calculatePersonPrice(passenger, basePrice);
            listOfTickets.addAll(personCosts);
        }

        BigDecimal totalPriceAllTickets = listOfTickets.stream()
                .map(Ticket::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<String> ticketDetails = listOfTickets.stream()
                .map(Ticket::getPriceDescription).toList();


        return new PricesDraft(ticketDetails, currencyFormat(totalPriceAllTickets));
    }

    private List<Ticket> calculatePersonPrice(Passenger passenger, BigDecimal basePrice ) {
        List<Ticket> results = new ArrayList<>();
        BigDecimal ticketCost = getPersonPrice(passenger, basePrice);
        String ticketText = getPersonText(passenger, basePrice, ticketCost);
        results.add(new Ticket(ticketText, ticketCost));
        return results;
    }

    private BigDecimal getPersonPrice(Passenger passenger, BigDecimal basePrice) {
        return  passenger.getPassengerType().getPriceMultiplier()
                .multiply(basePrice)
                .multiply(taxRateService.getVat().add(BigDecimal.ONE));
    }

    private String getPersonText(Passenger passenger, BigDecimal basePrice, BigDecimal totalPrice) {
        StringBuilder formattedText = new StringBuilder(capitalize(passenger.getPassengerType().toString()));
        formattedText.append(" (");
        formattedText.append(currencyFormat(basePrice));
        if (!passenger.getPassengerType().equals(PassengerType.ADULT)) {
            formattedText.append(" x ");
            formattedText.append(percentageFormat(passenger.getPassengerType().getPriceMultiplier()));
        }
        formattedText.append(" + ");
        formattedText.append(percentageFormat(taxRateService.getVat()));
        formattedText.append(") = ");
        formattedText.append(currencyFormat(totalPrice));
        return formattedText.toString();
    }

    private String currencyFormat(BigDecimal n) {
        return n.setScale(2, RoundingMode.HALF_UP) + " " + CURRENCY;
    }

    private String percentageFormat(BigDecimal n) {
        return NumberFormat.getPercentInstance().format(n);
    }

    private String capitalize (String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
