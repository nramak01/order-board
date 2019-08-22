package com.silverbars.marketplace.dto;

import com.silverbars.marketplace.constants.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class MarketOrder implements Serializable {

    @NotNull
    private String userId;
    @NotNull
    private Double orderQty;
    @NotNull
    private Integer pricePerKg;
    @NotNull
    private OrderType orderType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketOrder)) return false;
        MarketOrder order = (MarketOrder) o;
        return getUserId().equals(order.getUserId()) &&
                getOrderQty().equals(order.getOrderQty()) &&
                getPricePerKg().equals(order.getPricePerKg()) &&
                getOrderType() == order.getOrderType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getOrderQty(), getPricePerKg(), getOrderType());
    }
}
