package org.ecom.shopping_cart.service.Imp;

import org.ecom.shopping_cart.model.Category;
import org.ecom.shopping_cart.model.Product;
import org.ecom.shopping_cart.repository.Productrepo;
import org.ecom.shopping_cart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private Productrepo productrepo;
    @Override
    public Product saveProduct(Product product) {
        return productrepo.save(product);
    }

    @Override
    public Boolean deleteProduct(Integer id) {

         Product product =productrepo.findById(id).get();
         if(product!=null){
             productrepo.delete(product);
             return true;
         }
         return false;
    }

    @Override
    public Product getProductBYId(Integer id) {
         Product product = productrepo.findById(id).get();
        return product;
    }

    @Override
    public List<Product> getAllProducts() {
          return productrepo.findAll();
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image) {
                 Product dbProduct=    getProductBYId(product.getId());
        String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();

        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setStock(product.getStock());
        dbProduct.setImage(imageName);
//        dbProduct.setIsActive(product.getIsActive());
        dbProduct.setDiscount(product.getDiscount());

        // 5=100*(5/100); 100-5=95
        Double disocunt = product.getPrice() * (product.getDiscount() / 100.0);
        Double discountPrice = product.getPrice() - disocunt;
        dbProduct.setDiscountPrice(discountPrice);

        Product updateProduct = productrepo.save(dbProduct);

        if (updateProduct!=null) {

            if (!image.isEmpty()) {

                try {
                    File saveFile = new ClassPathResource("static/img").getFile();

                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                            + image.getOriginalFilename());
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return product;
        }
        return null;
    }

    @Override
    public List<Product> getAllActiveProducts( String category) {
        List<Product> products=null;
        if (ObjectUtils.isEmpty(category)) {
            products = productrepo.findByIsActiveTrue();
        }else {
            products=  productrepo.findByCategory(category);
        }
        return products;
    }

    @Override
    public List<Product> searchProduct(String ch) {
        return productrepo.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
    }
}








