package org.ecom.shopping_cart.service;

import org.ecom.shopping_cart.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {

    public Category  saveCategory(Category category);
    public Boolean exitCategory( String name);

     public List<Category> getAllCategory();
     public Boolean deleteCategory(Integer id);

     public   Category getCategoryById(Integer id);
    public List<Category> getAllActiveCategory();

}
