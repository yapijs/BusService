package io.busservice.taxrate;

import java.math.BigDecimal;

public interface Vat {
    BigDecimal getVat();

    BigDecimal getVatMultiplier(BigDecimal amount);
}
