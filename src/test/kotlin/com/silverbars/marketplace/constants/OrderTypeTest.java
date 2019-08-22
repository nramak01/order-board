package com.silverbars.marketplace.constants;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrderTypeTest {

    @Test
    public void extractOrderType() {
        assertEquals(OrderType.extractOrderType("SELL306").get(), OrderType.SELL);
        assertEquals(OrderType.extractOrderType("BUY310").get(), OrderType.BUY);
    }
}