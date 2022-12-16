package io.busservice.ticketprice;

import io.busservice.baseprice.BasePrice;
import io.busservice.taxrate.TaxRate;
import io.busservice.ticketprice.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final BasePrice basePriceService;
    private final TaxRate taxRateService;
    private static final BigDecimal LUGGAGE_COST_RATIO = new BigDecimal("0.3");
    private static final String CURRENCY = "EUR";

    public TicketService(BasePrice basePriceService, TaxRate taxRateService) {
        this.basePriceService = basePriceService;
        this.taxRateService = taxRateService;
    }

    public PricesDraft calculateTicketPrices(PriceRequest priceRequest) {
        validateRequest(priceRequest);

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

    private void validateRequest(PriceRequest priceRequest) {
        if (priceRequest.getTo().equalsIgnoreCase(priceRequest.getFrom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong start or end location");
        }
    }

    private List<Ticket> calculatePersonPrice(Passenger passenger, BigDecimal basePrice) {
        List<Ticket> results = new ArrayList<>();
        BigDecimal vat = taxRateService.getVat();

        BigDecimal ticketCost = getPersonPrice(passenger.getPassengerType(), basePrice, vat);
        String ticketText = getPersonText(passenger, basePrice, ticketCost, vat);

        BigDecimal luggageCost = getLuggagePrice(passenger.getLuggageCount(), basePrice, vat);
        String luggageText = getLuggageText(passenger.getLuggageCount(), basePrice, luggageCost, vat);

        results.add(new Ticket(ticketText, ticketCost));
        results.add(new Ticket(luggageText, luggageCost));
        return results;
    }

    private BigDecimal getLuggagePrice(int luggageCount, BigDecimal basePrice, BigDecimal vat) {
        return BigDecimal.valueOf(luggageCount)
                .multiply(basePrice)
                .multiply(LUGGAGE_COST_RATIO)
                .multiply(getVatMultiplier(vat));
    }

    private BigDecimal getPersonPrice(PassengerType passengerType, BigDecimal basePrice, BigDecimal vat) {
        return passengerType.getPriceMultiplier()
                .multiply(basePrice)
                .multiply(getVatMultiplier(vat));
    }

    private String getPersonText(Passenger passenger, BigDecimal basePrice, BigDecimal totalPrice, BigDecimal vat) {
        StringBuilder formattedText = new StringBuilder();
        formattedText.append(capitalize(passenger.getPassengerType().toString()));
        formattedText.append(" (");
        formattedText.append(currencyFormat(basePrice));
        if (!passenger.getPassengerType().equals(PassengerType.ADULT)) {
            formattedText.append(" x ");
            formattedText.append(percentageFormat(passenger.getPassengerType().getPriceMultiplier()));
        }
        formattedText.append(appendFormattedVatAndCostText(vat, totalPrice));
        return formattedText.toString();
    }

    private String getLuggageText(int luggageCount, BigDecimal basePrice, BigDecimal luggageCost, BigDecimal vat) {
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

            formattedText.append(appendFormattedVatAndCostText(vat, luggageCost));
        }
        return formattedText.toString();
    }

    private String currencyFormat(BigDecimal n) {
        return n.setScale(2, RoundingMode.HALF_UP) + " " + CURRENCY;
    }

    private String percentageFormat(BigDecimal n) {
        return NumberFormat.getPercentInstance().format(n);
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    private BigDecimal getVatMultiplier(BigDecimal vat) {
        return vat.add(BigDecimal.ONE);
    }

    private StringBuilder appendFormattedVatAndCostText(BigDecimal vat, BigDecimal ticketPrice) {
        StringBuilder textToAdd = new StringBuilder();
        textToAdd.append(" + ");
        textToAdd.append(percentageFormat(vat));
        textToAdd.append(") = ");
        textToAdd.append(currencyFormat(ticketPrice));
        return textToAdd;
    }
}
