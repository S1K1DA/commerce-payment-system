package com.spartaifive.commercepayment.domain.payment.dto;

public sealed interface ConfirmPaymentResponse
        permits ConfirmPaymentSuccessResponse, ConfirmPaymentFailResponse {
}
