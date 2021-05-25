package com.company.jtatest;

import com.company.jtatest.entity.customers.Customer;
import com.company.jtatest.entity.orders.Order;
import io.jmix.core.Metadata;
import io.jmix.core.Stores;
import io.jmix.core.UnsafeDataManager;
import io.jmix.data.StoreAwareLocator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
public class GlobalTxTest {
    @Autowired
    StoreAwareLocator locator;
    @Autowired
    Metadata metadata;
    @Autowired
    UnsafeDataManager dataManager;

    @Test
    void successfulCommitTest() {
        AtomicLong customerId = new AtomicLong();
        AtomicLong orderId = new AtomicLong();
        mainTransaction().executeWithoutResult(tsM -> {
            Customer customer = createCustomer();
            customerId.set(customer.getId());
            ordersTransaction().executeWithoutResult(tsO -> {
                orderId.set(createOrder(customer).getId());
            });
        });

        Customer loadedCustomer = dataManager.load(Customer.class).id(customerId.get()).one();
        Order loadedOrder = dataManager.load(Order.class).id(orderId.get()).one();

        assertThat(loadedCustomer.getId()).isEqualTo(customerId.get());
        assertThat(loadedOrder.getId()).isEqualTo(orderId.get());
    }

    @Test
    void rollbackTest() {
        AtomicLong customerId = new AtomicLong();
        AtomicLong orderId = new AtomicLong();
        try {
            mainTransaction().executeWithoutResult(tsM -> {
                Customer customer = createCustomer();
                customerId.set(customer.getId());
                ordersTransaction().executeWithoutResult(tsO -> {
                    orderId.set(createOrder(customer).getId());
                });
                throw new RuntimeException("Transaction rollback exception");
            });
        } catch (RuntimeException e) {
            // Catch thrown exception
        }

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() ->
                dataManager.load(Customer.class).id(customerId.get()).one()
        );
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() ->
                dataManager.load(Order.class).id(orderId.get()).one()
        );
    }

    TransactionTemplate mainTransaction() {
        TransactionTemplate template = new TransactionTemplate(locator.getTransactionManager(Stores.MAIN));
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return template;
    }

    TransactionTemplate ordersTransaction() {
        TransactionTemplate template = new TransactionTemplate(locator.getTransactionManager("orders"));
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return template;
    }

    Customer createCustomer() {
        Customer customer = metadata.create(Customer.class);

        customer.setName("customer-" + RandomStringUtils.randomAlphabetic(5));
        customer.setEmail("---");

        locator.getEntityManager(Stores.MAIN).persist(customer);
        return customer;
    }

    Order createOrder(Customer customer) {
        Order order = metadata.create(Order.class);

        order.setNumber("order-" + RandomStringUtils.randomAlphabetic(5));
        order.setCustomer(customer);

        locator.getEntityManager("orders").persist(order);
        return order;
    }

    @AfterEach
    void cleanup() {
        locator.getJdbcTemplate(Stores.MAIN).update("delete from SAMPLE_CUSTOMER");
        locator.getJdbcTemplate("orders").update("delete from SAMPLE_ORDER");
    }
}
