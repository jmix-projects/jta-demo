package com.company.jtatest.screen.customer;

import io.jmix.ui.screen.*;
import com.company.jtatest.entity.customers.Customer;

@UiController("sample_Customer.browse")
@UiDescriptor("customer-browse.xml")
@LookupComponent("customersTable")
public class CustomerBrowse extends StandardLookup<Customer> {
}