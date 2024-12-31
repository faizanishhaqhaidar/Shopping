package org.ecom.shopping_cart.service;

import org.ecom.shopping_cart.model.Cart;

import java.util.List;

public interface CartService {
    public Cart saveCart(Integer productId, Integer userId);

    public Integer getCountCart(Integer userId);

    public List<Cart> getCartsByUser(Integer userId);


    public void updateQuantity(String sy, Integer cid);
}
