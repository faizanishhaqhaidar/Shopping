package org.ecom.shopping_cart.repository;

import org.ecom.shopping_cart.model.Category;
import org.ecom.shopping_cart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Productrepo  extends JpaRepository<Product, Integer> {


    List<Product> findByIsActiveTrue();
    List<Product> findByCategory(String category);


    List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2);

}
