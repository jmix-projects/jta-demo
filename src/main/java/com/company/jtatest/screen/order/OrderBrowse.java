package com.company.jtatest.screen.order;

import com.company.jtatest.entity.LocalEntity;
import com.company.jtatest.entity.customers.Customer;
import com.company.jtatest.entity.orders.Order;
import com.company.jtatest.service.CompositeJTAService;
import com.company.jtatest.service.orders.OrdersService;
import io.jmix.core.DataManager;
import io.jmix.core.EntitySet;
import io.jmix.core.SaveContext;
import io.jmix.ui.action.Action;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.DataLoader;
import io.jmix.ui.screen.*;

import javax.inject.Inject;

@UiController("sample_Order.browse")
@UiDescriptor("order-browse.xml")
@LookupComponent("ordersTable")
public class OrderBrowse extends StandardLookup<Order> {
    @Inject
    CompositeJTAService compositeJTAService;

    @Inject
    OrdersService ordersService;

    @Inject
    CollectionContainer<Order> ordersDc;

    @Inject
    DataLoader ordersDl;

    @Inject
    DataManager dataManager;

    @Subscribe("ordersTable.invokeService")
    protected void onInvokeServiceActionPerformed(Action.ActionPerformedEvent event) {
        Order selected = ordersDc.getItemOrNull();
        if (selected != null) {
            compositeJTAService.updateCustomerAndOrder(selected);
        }
        ordersDl.load();
    }

    @Subscribe("ordersTable.invokeCreation")
    protected void onInvokeCreationPerformed(Action.ActionPerformedEvent event) {
        Order o = dataManager.create(Order.class);
        Customer c = dataManager.create(Customer.class);
        LocalEntity l = dataManager.create(LocalEntity.class);
        c.setName("Customer created");
        c.setEmail("-");
        o.setNumber("1");
        o.setCustomer(c);
        l.setName("LocalEntity created");
        SaveContext sc = new SaveContext();
        sc.saving(c, o, l);
        sc.setJoinTransaction(true);
        dataManager.save(sc);
        ordersDl.load();
    }

    @Subscribe("ordersTable.updateOrder")
    protected void onUpdateOrderActionPerformed(Action.ActionPerformedEvent event) {
        Order selected = ordersDc.getItemOrNull();
        if (selected != null) {
            ordersService.saveOrder(selected);
        }
        ordersDl.load();
    }
}