package org.ecom.shopping_cart.repository;
import org.ecom.shopping_cart.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    List<ProductOrder> findByUserId(Integer userId);

}