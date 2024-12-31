package org.ecom.shopping_cart.repository;

import org.ecom.shopping_cart.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Categoryrepo extends JpaRepository<Category, Integer> {

    public Boolean existsByName(String name);

    public List<Category> findByIsActiveTrue();

}
