package com.silverbars.marketplace.repository;

import com.silverbars.marketplace.constants.OrderType;
import com.silverbars.marketplace.dto.ConsolidatedOrder;
import com.silverbars.marketplace.dto.LiveOrder;
import com.silverbars.marketplace.dto.MarketOrder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/*
  Only accessor methods are created to ensure reference is not altered
 */
@Getter
@Slf4j
public class OrderRepository {

    private static OrderRepository orderRepository;

    // Singleton pattern to ensure the orderTracker is central
    private OrderRepository() {
    }

    public static OrderRepository repositoryInstance() {
        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }
        return orderRepository;
    }

    /**
     * Instance is declared final for immutability
     * ConcurrentHashMap will ensure Data integrity in a multi threaded environment
     */
    private final Map<String, ConsolidatedOrder> orderTracker = new ConcurrentHashMap<>();

    /**
     * This function receives order and updates to the orderTracker
     * Duplicate check is not in scope for this requirement
     * orderTracker Key format: orderType + pricePerKg
     * - This is adopted to keep logic simple and helps with reduced iteration in deletion over relevant list
     *
     * @param marketOrder
     */
    public void createOrUpdateOrder(MarketOrder marketOrder) {
        String orderKey = marketOrder.getOrderType() + Integer.toString(marketOrder.getPricePerKg());
        orderTracker.computeIfAbsent(orderKey, key -> new ConsolidatedOrder()).registerOrder(marketOrder);
    }

    public void cancelOrder(MarketOrder cancellationOrder) {
        // orderTracker Key format: orderType + pricePerKg
        String orderKey = cancellationOrder.getOrderType() + Integer.toString(cancellationOrder.getPricePerKg());
        orderTracker.computeIfPresent(orderKey, (key, consolidatedOrder) -> {
            boolean removalStatus = consolidatedOrder.cancelOrder(cancellationOrder);
            log.info("Cancellation Order found and removed : {} ", removalStatus);
            return consolidatedOrder;
        });
    }

    /**
     * o(1) for totalCount value. Since the Live dash board gets used heavily
     * that's the need for the total to be retrieved efficiently
     */
    public List<LiveOrder> fetchAllLiveOrders() {
        List<LiveOrder> liveOrders = new ArrayList<>();

        for (String orderTrackerKey : orderTracker.keySet()) {
            Optional<OrderType> orderType = OrderType.extractOrderType(orderTrackerKey);
            String pricePerKg = orderTrackerKey.replaceFirst(orderType.get().name(), "");
            ConsolidatedOrder consolidatedOrder = orderTracker.get(orderTrackerKey);
            liveOrders.add(LiveOrder.builder()
                    .orderType(orderType.get())
                    .orderQty((consolidatedOrder.getTotalOrderQty().doubleValue()) / 1000)
                    .pricePerKg(Integer.valueOf(pricePerKg))
                    .build());
        }

        Comparator<LiveOrder> liveOrderComparator = (o1, o2) -> {
            if (o1.getOrderType().equals(OrderType.SELL) && o2.getOrderType().equals(OrderType.SELL)) {
                return o1.getPricePerKg().compareTo(o2.getPricePerKg());
            } else if (o1.getOrderType().equals(OrderType.BUY) && o2.getOrderType().equals(OrderType.BUY)) {
                return o2.getPricePerKg().compareTo(o1.getPricePerKg());
            }
            return 0;
        };

        Collections.sort(liveOrders, Comparator.comparing(LiveOrder::getOrderType)
                .thenComparing(liveOrderComparator));
        return liveOrders;
    }

}
