package io.busservice.taxrate;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TaxRateService implements Vat{

    @Override
    public BigDecimal getVat() {
        return new BigDecimal("0.21");
    }

    @Override
    public BigDecimal getVatMultiplier(BigDecimal amount) {
        return getVat().add(BigDecimal.ONE);
    }
}
