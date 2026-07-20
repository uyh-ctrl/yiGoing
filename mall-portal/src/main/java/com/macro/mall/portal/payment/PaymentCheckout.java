package com.macro.mall.portal.payment;

/** Channel-neutral checkout payload returned to the web client. */
public record PaymentCheckout(PaymentChannel channel, String orderSn, String payload) { }
