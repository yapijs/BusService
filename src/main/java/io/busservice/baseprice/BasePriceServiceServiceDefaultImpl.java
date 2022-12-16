package io.busservice.baseprice;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BasePriceServiceServiceDefaultImpl implements BasePriceService {

    @Override
    public BigDecimal getBasePrice(String from, String to) {
        BigDecimal calculatedBasePrice;
        if (from.equalsIgnoreCase("riga") && to.equalsIgnoreCase("vilnius")) {
            calculatedBasePrice = new BigDecimal(10);
        } else {
            calculatedBasePrice = new BigDecimal(15);
        }
        return calculatedBasePrice;
    }
}
