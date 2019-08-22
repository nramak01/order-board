package com.silverbars.marketplace.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


/*
    If the inserts/updates are more frequent then ReentrantLock can be used to ensure synchronisation
    as below Concurrency list orderBasedOnPrice performs slower than ArrayList due to overhead of creating copy of the
    list due to frequent inserts
 */
@Slf4j
@Getter
public class ConsolidatedOrder implements Serializable {

    private AtomicInteger totalOrderQty = new AtomicInteger(0);

    private CopyOnWriteArrayList<MarketOrder> orderBasedOnPrice = new CopyOnWriteArrayList<>();

    public void registerOrder(MarketOrder order) {
        totalOrderQty.addAndGet((int) (order.getOrderQty() * 1000));
        orderBasedOnPrice.add(order);
    }

    public boolean cancelOrder(MarketOrder order) {
        // The below check ensures we are cancelling an order that had been placed earlier
        // This is the reason for keeping track of the List of orders
        boolean removedOrder = orderBasedOnPrice.remove(order);
        totalOrderQty.addAndGet((removedOrder) ? (int) (-order.getOrderQty() * 1000) : 0);
        return removedOrder;
    }
}
