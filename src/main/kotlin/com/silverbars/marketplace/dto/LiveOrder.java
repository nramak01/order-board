package com.silverbars.marketplace.dto;

import com.silverbars.marketplace.constants.OrderType;
import lombok.*;

import java.io.Serializable;
import java.util.Comparator;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class LiveOrder implements Serializable, Comparator<LiveOrder> {

    private Double orderQty;
    private Integer pricePerKg;
    private OrderType orderType;

    @Override
    public int compare(LiveOrder o1, LiveOrder o2) {
        return 0;
    }
}
