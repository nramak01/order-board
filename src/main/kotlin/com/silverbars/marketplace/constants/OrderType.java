package com.silverbars.marketplace.constants;


import java.util.Arrays;
import java.util.Optional;

public enum OrderType {
    BUY, SELL;

    public static Optional<OrderType> extractOrderType(String value) {

        return Arrays.stream(OrderType.values()).filter(orderType -> value.startsWith(orderType.toString())).findFirst();
    }
}
