package org.ecom.shopping_cart.repository;


import org.ecom.shopping_cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepo extends JpaRepository<Cart, Integer> {

    public Cart findByProductIdAndUserId(Integer productId, Integer userId);
    public Integer countByUserId(Integer userId);
    public List<Cart> findByUserId(Integer userId);

}