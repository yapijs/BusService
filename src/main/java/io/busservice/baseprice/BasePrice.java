package io.busservice.baseprice;

import java.math.BigDecimal;

public interface BasePrice {
    BigDecimal getBasePrice(String from, String to);
}
