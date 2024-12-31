package org.ecom.shopping_cart.service;

import org.ecom.shopping_cart.model.OrderRequest;
import org.ecom.shopping_cart.model.ProductOrder;

import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userid, OrderRequest orderRequest);

    public List<ProductOrder> getOrdersByUser(Integer userId);

    public Boolean updateOrderStatus(Integer id, String status);


    public List<ProductOrder> getAllOrders();

}