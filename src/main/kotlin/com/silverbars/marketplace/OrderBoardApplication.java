package com.silverbars.marketplace;


import com.silverbars.marketplace.controller.OrderController;
import io.javalin.Javalin;

public class OrderBoardApplication {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.post("/order/register", OrderController.registerOrder);
        app.post("/order/cancel", OrderController.cancelOrder);
        app.get("/order/live-orders", OrderController.liveOrderBoard);
    }
}
