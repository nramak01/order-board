package com.silverbars.marketplace.controller;


import com.silverbars.marketplace.dto.LiveOrder;
import com.silverbars.marketplace.dto.MarketOrder;
import com.silverbars.marketplace.repository.OrderRepository;
import io.javalin.http.Handler;

import java.util.List;

public class OrderController {

    static OrderRepository repositoryInstance = OrderRepository.repositoryInstance();


    public static Handler registerOrder = ctx -> {
        MarketOrder orderForRegistration = ctx.bodyAsClass(MarketOrder.class);
        repositoryInstance.createOrUpdateOrder(orderForRegistration);
        ctx.json("Order Registered Successfully!");
    };

    public static Handler cancelOrder = ctx -> {
        MarketOrder orderForRegistration = ctx.bodyAsClass(MarketOrder.class);
        repositoryInstance.cancelOrder(orderForRegistration);
        ctx.json("Order Removed Successfully!");
    };

    public static Handler liveOrderBoard = ctx -> {
        List<LiveOrder> liveOrders = repositoryInstance.fetchAllLiveOrders();
        ctx.json(liveOrders);
    };

}
