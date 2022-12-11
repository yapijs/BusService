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

    private static final BigDecimal LUGGAGE_COST_RATIO = new BigDecimal("0.3");
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

        BigDecimal ticketCost = getPersonPrice(passenger.getPassengerType(), basePrice);
        String ticketText = getPersonText(passenger, basePrice, ticketCost);

        BigDecimal luggageCost = getLuggagePrice(passenger.getLuggageCount(), basePrice);
        String LuggageText = getLuggageText(passenger.getLuggageCount(), basePrice, luggageCost);

        results.add(new Ticket(ticketText, ticketCost));
        results.add(new Ticket(LuggageText, luggageCost));
        return results;
    }

    private BigDecimal getLuggagePrice(int luggageCount, BigDecimal basePrice) {
        return BigDecimal.valueOf(luggageCount)
                .multiply(basePrice)
                .multiply(LUGGAGE_COST_RATIO)
                .multiply(getVatMultiplier());
    }

    private BigDecimal getPersonPrice(PassengerType passengerType, BigDecimal basePrice) {
        return  passengerType.getPriceMultiplier()
                .multiply(basePrice)
                .multiply(getVatMultiplier());
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

    private String getLuggageText(int luggageCount, BigDecimal basePrice, BigDecimal luggageCost) {
        StringBuilder formattedText = new StringBuilder();
        if (luggageCount == 0) {
            formattedText.append("No bags");
        } else {
            if (luggageCount == 1) {
                formattedText.append("1 bag");
                formattedText.append(" (");
            } else {
                formattedText.append(luggageCount).append(" bags");
                formattedText.append(" (").append(luggageCount).append(" x ");
            }
            formattedText.append(currencyFormat(basePrice));
            formattedText.append(" x ");
            formattedText.append(percentageFormat(LUGGAGE_COST_RATIO));
            formattedText.append(" + ");
            formattedText.append(percentageFormat(taxRateService.getVat()));
            formattedText.append(") = ");
            formattedText.append(currencyFormat(luggageCost));
        }
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

    private BigDecimal getVatMultiplier() {
        return taxRateService.getVat().add(BigDecimal.ONE);
    }
}
