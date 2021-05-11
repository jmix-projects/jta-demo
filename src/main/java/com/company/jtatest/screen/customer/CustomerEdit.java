package com.company.jtatest.screen.customer;

import io.jmix.ui.screen.*;
import com.company.jtatest.entity.customers.Customer;

@UiController("sample_Customer.edit")
@UiDescriptor("customer-edit.xml")
@EditedEntityContainer("customerDc")
public class CustomerEdit extends StandardEditor<Customer> {
}