package com.company.jtatest.screen.order;

import io.jmix.ui.screen.*;
import com.company.jtatest.entity.orders.Order;

@UiController("sample_Order.edit")
@UiDescriptor("order-edit.xml")
@EditedEntityContainer("orderDc")
public class OrderEdit extends StandardEditor<Order> {
}