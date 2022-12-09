package io.busservice.ticketprice.dto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PassengerTypeConverter implements Converter<String, PassengerType> {

    @Override
    public PassengerType convert(String value) {
        return PassengerType.valueOf(value.toUpperCase());
    }
}
