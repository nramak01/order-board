package com.silverbars.marketplace.repository;

import com.silverbars.marketplace.constants.OrderType;
import com.silverbars.marketplace.dto.ConsolidatedOrder;
import com.silverbars.marketplace.dto.LiveOrder;
import com.silverbars.marketplace.dto.MarketOrder;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * Integration Test cases were not provided due to scarcity of Time
 */
public class OrderRepositoryTest {

    private OrderRepository orderRepository;

    @Before
    public void setUp() {
        orderRepository = OrderRepository.repositoryInstance();
    }

    @Test
    public void repositoryInstance() {
        OrderRepository testInstance1 = OrderRepository.repositoryInstance();
        OrderRepository testInstance2 = OrderRepository.repositoryInstance();

        assertEquals(testInstance1, testInstance2);
    }

    @Test
    public void createAndCancelRepeatOrder() {

        MarketOrder order1 = MarketOrder.builder().userId("user1").orderType(OrderType.SELL).pricePerKg(306).orderQty(3.5).build();
        orderRepository.createOrUpdateOrder(order1);
        MarketOrder order2 = MarketOrder.builder().userId("user1").orderType(OrderType.SELL).pricePerKg(306).orderQty(3.5).build();
        orderRepository.createOrUpdateOrder(order2);

        assertTrue(orderRepository.getOrderTracker().containsKey(OrderType.SELL+ Integer.toString(306)));

        ConsolidatedOrder consolidatedOrder = orderRepository.getOrderTracker().get(OrderType.SELL+ Integer.toString(306));
        assertEquals(7000 ,consolidatedOrder.getTotalOrderQty().intValue());
        assertEquals(Arrays.asList(order1,order2), consolidatedOrder.getOrderBasedOnPrice());

        orderRepository.cancelOrder(order2);
        assertEquals(3500 ,consolidatedOrder.getTotalOrderQty().intValue());
        assertEquals(Arrays.asList(order1), consolidatedOrder.getOrderBasedOnPrice());

        orderRepository.cancelOrder(order1);
        assertEquals(0 ,consolidatedOrder.getTotalOrderQty().intValue());
        assertEquals(Collections.EMPTY_LIST, consolidatedOrder.getOrderBasedOnPrice());
    }

    @Test
    public void createAndCancelOrdersFromDifferentCustomers() {

        MarketOrder order1 = createOrUpdateOrder("user1", OrderType.SELL,306,3.5);
        MarketOrder order2 = createOrUpdateOrder("user2", OrderType.SELL,310,1.2);
        MarketOrder order3 = createOrUpdateOrder("user3", OrderType.SELL,307,1.5);
        MarketOrder order4 = createOrUpdateOrder("user4", OrderType.SELL,306,2.0);

        assertTrue(orderRepository.getOrderTracker().keySet().size()==3);
        assertTrue(orderRepository.getOrderTracker().containsKey(OrderType.SELL+ Integer.toString(306)));
        assertTrue(orderRepository.getOrderTracker().containsKey(OrderType.SELL+ Integer.toString(307)));
        assertTrue(orderRepository.getOrderTracker().containsKey(OrderType.SELL+ Integer.toString(310)));

        ConsolidatedOrder consolidatedOrderFor306 = orderRepository.getOrderTracker().get(OrderType.SELL+ Integer.toString(306));
        assertEquals(5500 ,consolidatedOrderFor306.getTotalOrderQty().intValue());
        assertEquals(Arrays.asList(order1, order4), consolidatedOrderFor306.getOrderBasedOnPrice());

        orderRepository.cancelOrder(order1);
        assertEquals(2000 ,consolidatedOrderFor306.getTotalOrderQty().intValue());
        assertEquals(Arrays.asList(order4), consolidatedOrderFor306.getOrderBasedOnPrice());

        orderRepository.cancelOrder(order4);
        assertEquals(0 ,consolidatedOrderFor306.getTotalOrderQty().intValue());
        assertEquals(Collections.EMPTY_LIST, consolidatedOrderFor306.getOrderBasedOnPrice());


        ConsolidatedOrder consolidatedOrderFor310 = orderRepository.getOrderTracker().get(OrderType.SELL+ Integer.toString(310));
        assertEquals(1200 ,consolidatedOrderFor310.getTotalOrderQty().intValue());
        assertEquals(Arrays.asList(order2), consolidatedOrderFor310.getOrderBasedOnPrice());

        orderRepository.cancelOrder(order2);
        assertEquals(0 ,consolidatedOrderFor310.getTotalOrderQty().intValue());
        assertEquals(Collections.EMPTY_LIST, consolidatedOrderFor310.getOrderBasedOnPrice());


        ConsolidatedOrder consolidatedOrderFor307 = orderRepository.getOrderTracker().get(OrderType.SELL+ Integer.toString(307));
        assertEquals(1500 ,consolidatedOrderFor307.getTotalOrderQty().intValue());
        assertEquals(Arrays.asList(order3), consolidatedOrderFor307.getOrderBasedOnPrice());

        orderRepository.cancelOrder(order3);
        assertEquals(0 ,consolidatedOrderFor307.getTotalOrderQty().intValue());
        assertEquals(Collections.EMPTY_LIST, consolidatedOrderFor307.getOrderBasedOnPrice());
    }

    @Test
    public void fetchAllLiveOrders() {

        MarketOrder order1 = createOrUpdateOrder("user1", OrderType.SELL,306,3.5);
        MarketOrder order2 = createOrUpdateOrder("user2", OrderType.SELL,310,1.2);
        MarketOrder order3 = createOrUpdateOrder("user3", OrderType.SELL,307,1.5);
        MarketOrder order4 = createOrUpdateOrder("user4", OrderType.SELL,306,2.0);
        MarketOrder order5 = createOrUpdateOrder("user1", OrderType.BUY,306,3.5);
        MarketOrder order6 = createOrUpdateOrder("user2", OrderType.BUY,310,1.2);
        MarketOrder order7 = createOrUpdateOrder("user3", OrderType.BUY,307,1.5);
        MarketOrder order8 = createOrUpdateOrder("user4", OrderType.BUY,306,2.0);

        List<LiveOrder> liveOrders = orderRepository.fetchAllLiveOrders();
        //SELL orders the orders with lowest prices are displayed first. Opposite is true for the BUY orders.
        assertEquals(createLiveOrder(1.2 , 310, OrderType.BUY), liveOrders.get(0));
        assertEquals(createLiveOrder(1.5 , 307, OrderType.BUY), liveOrders.get(1));
        assertEquals(createLiveOrder(5.5 , 306, OrderType.BUY), liveOrders.get(2));
        assertEquals(createLiveOrder(5.5 , 306, OrderType.SELL), liveOrders.get(3));
        assertEquals(createLiveOrder(1.5 , 307, OrderType.SELL), liveOrders.get(4));
        assertEquals(createLiveOrder(1.2 , 310, OrderType.SELL), liveOrders.get(5));
    }

    private MarketOrder createOrUpdateOrder(String userId, OrderType orderType, Integer pricePerKg, Double orderQty){
        MarketOrder order = MarketOrder.builder().userId(userId).orderType(orderType).pricePerKg(pricePerKg).orderQty(orderQty).build();
        orderRepository.createOrUpdateOrder(order);
        return order;
    }

    private LiveOrder createLiveOrder(Double orderQty, Integer pricePerKg, OrderType orderType){
        return   LiveOrder.builder()
                .orderType(orderType)
                .orderQty((orderQty))
                .pricePerKg(pricePerKg)
                .build();
    }
}