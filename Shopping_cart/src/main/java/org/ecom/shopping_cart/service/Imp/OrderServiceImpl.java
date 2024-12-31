package org.ecom.shopping_cart.service.Imp;

import org.ecom.shopping_cart.model.Cart;
import org.ecom.shopping_cart.model.OrderAddres;
import org.ecom.shopping_cart.model.OrderRequest;
import org.ecom.shopping_cart.model.ProductOrder;
import org.ecom.shopping_cart.repository.CartRepo;
import org.ecom.shopping_cart.repository.ProductOrderRepository;
import org.ecom.shopping_cart.service.OrderService;
import org.ecom.shopping_cart.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductOrderRepository orderRepository;
    @Autowired
    private CartRepo cartRepo;

    @Override
    public void saveOrder(Integer userid, OrderRequest orderRequest) {

        List<Cart> carts = cartRepo.findByUserId(userid);

        for (Cart cart : carts) {

            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddres address = new OrderAddres();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddres(address);
            orderRepository.save(order);

        }
    }

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        List<ProductOrder> orders = orderRepository.findByUserId(userId);
        return orders;
    }

    @Override
    public Boolean updateOrderStatus(Integer id, String status) {
        Optional<ProductOrder> findById = orderRepository.findById(id);
        if (findById.isPresent()) {
            ProductOrder productOrder = findById.get();
            productOrder.setStatus(status);
            orderRepository.save(productOrder);
            return true;
        }
        return false;
    }
    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }


}
