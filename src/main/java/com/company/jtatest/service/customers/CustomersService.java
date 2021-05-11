package com.company.jtatest.service.customers;

import com.company.jtatest.entity.customers.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Service
public class CustomersService {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional("transactionManager")
    public Customer updateCustomerById(Long customerId) {
        Customer customer = entityManager.find(Customer.class, customerId);
        customer.setLastOrder(new Date());
        return entityManager.merge(customer);
    }

}
