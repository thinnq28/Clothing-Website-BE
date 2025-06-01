package com.datn.shop_app.constant;

public interface Constant {
    interface Order {
        String PAID = "PAID";
        String UNPAID = "UNPAID";
        String REFUNDED = "REFUNDED";
    }

    interface OrderStatus {
        String CANCELLED = "cancelled";
        String DELIVERED = "delivered";
        String SHIPPED = "shipped";
        String PROCESSING = "processing";
        String PENDING = "pending";
    }
}
