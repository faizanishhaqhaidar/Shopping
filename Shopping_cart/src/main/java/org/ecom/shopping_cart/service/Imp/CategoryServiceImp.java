package org.ecom.shopping_cart.service.Imp;

import org.ecom.shopping_cart.model.Category;
//import org.ecom.shopping_cart.repository.categoryRepository;
import org.ecom.shopping_cart.repository.Categoryrepo;
import org.ecom.shopping_cart.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    private Categoryrepo categoryrepo;

    @Override
    public Boolean exitCategory( String name) {
            return    categoryrepo.existsByName(name);
    }

    @Override
    public List<Category> getAllCategory() {
          return categoryrepo.findAll();
    }

    @Override
    public Boolean deleteCategory(  Integer id) {
            Category category =  categoryrepo.findById(id).get();
            if(category != null) {
                categoryrepo.delete(category);
                 return true;
            }
            else {
                return false;
            }
    }

    @Override
    public Category getCategoryById(Integer id) {
         return categoryrepo.findById(id).get();
    }

    @Override
    public List<Category> getAllActiveCategory() {
        List<Category> categories = categoryrepo.findByIsActiveTrue();
        return categories;
    }

    @Override
         public Category saveCategory(Category category) {
           return  categoryrepo.save(category);}


}
