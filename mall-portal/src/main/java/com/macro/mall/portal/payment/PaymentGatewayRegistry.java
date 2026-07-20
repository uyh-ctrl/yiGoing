package com.macro.mall.portal.payment;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Keeps provider selection out of order controllers and services. */
@Component
public class PaymentGatewayRegistry {
    private final Map<PaymentChannel, PaymentGateway> gateways = new EnumMap<>(PaymentChannel.class);

    public PaymentGatewayRegistry(List<PaymentGateway> gateways) {
        gateways.forEach(gateway -> this.gateways.put(gateway.channel(), gateway));
    }

    public PaymentGateway get(PaymentChannel channel) {
        PaymentGateway gateway = gateways.get(channel);
        if (gateway == null) throw new IllegalArgumentException("Payment channel is not enabled: " + channel);
        return gateway;
    }
}
