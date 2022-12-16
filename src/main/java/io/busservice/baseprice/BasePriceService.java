package io.busservice.baseprice;

import java.math.BigDecimal;

public interface BasePriceService {
    BigDecimal getBasePrice(String from, String to);
}
