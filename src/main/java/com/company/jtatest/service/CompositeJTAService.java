package com.company.jtatest.service;

import com.company.jtatest.entity.customers.Customer;
import com.company.jtatest.entity.orders.Order;
import com.company.jtatest.service.customers.CustomersService;
import com.company.jtatest.service.orders.OrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompositeJTAService {

    private final Logger logger = LoggerFactory.getLogger(CompositeJTAService.class);

    final CustomersService customersService;

    final OrdersService ordersService;

    public CompositeJTAService(CustomersService customersService, OrdersService ordersService) {
        this.customersService = customersService;
        this.ordersService = ordersService;
    }

    @Transactional("transactionManager")
    public void updateCustomerAndOrder(Order order) {
        Customer customer = customersService.updateCustomerById(order.getCustomerId());
        Order updatedOrder = ordersService.saveOrder(order);
        logger.info(customer+" "+order);
        if (updatedOrder.getNumber().contains("8")) {
            throw new RuntimeException("Test Exception");
        }
    }

}
