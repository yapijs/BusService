package io.busservice.taxrate;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TaxRateServiceServiceDefaultImpl implements TaxRateService {

    @Override
    public BigDecimal getVat() {
        return new BigDecimal("0.21");
    }
}
