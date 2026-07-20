package com.macro.mall.portal.payment;

import com.macro.mall.model.OmsOrder;

public interface PaymentGateway {
    PaymentChannel channel();
    PaymentCheckout createCheckout(OmsOrder order);
}
