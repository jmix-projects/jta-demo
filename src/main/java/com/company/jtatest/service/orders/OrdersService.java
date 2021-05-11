package com.company.jtatest.service.orders;

import com.company.jtatest.entity.orders.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Service
public class OrdersService {

    @PersistenceContext(unitName = "orders")
    EntityManager entityManager;

    @Transactional("ordersTransactionManager")
    public Order saveOrder(Order order) {
        order.setOrderDate(new Date());
        order.setNumber((Integer.parseInt(order.getNumber()) + 1) + "");
        return entityManager.merge(order);
    }

}
