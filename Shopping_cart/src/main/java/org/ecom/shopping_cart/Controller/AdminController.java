package org.ecom.shopping_cart.Controller;

 import org.ecom.shopping_cart.model.Product;
 import org.ecom.shopping_cart.model.ProductOrder;
 import org.ecom.shopping_cart.model.UserDtls;
 import org.ecom.shopping_cart.service.*;
 import org.ecom.shopping_cart.utils.OrderStatus;
 import org.springframework.transaction.support.ResourceTransactionManager;
 import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.ecom.shopping_cart.model.Category;
//import org.ecom.shopping_cart.service.Imp.CategoryServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
 import java.security.Principal;
 import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController{
    @Autowired
    private  CategoryService categoryService;
    @Autowired
    private ProductService productService;


    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;



    @Autowired
    private OrderService orderService;


    @GetMapping("/")
    public   String index(){
        return "admin/index";
    }


    @ModelAttribute
    public void  getUserDetails(Principal p , Model m){

               if(p != null){
                  String email= p.getName();
                  UserDtls user= userService.getUserByEmail(email);
                  m.addAttribute("user",user);
                    Integer count= cartService.getCountCart(user.getId());
                   m.addAttribute("count",count);
                     }
              List<Category> categories =categoryService.getAllActiveCategory();
               m.addAttribute("categories",categories);


    }
    @GetMapping("/loadaddproduct")
    public String loadAddproduct(Model model){
                List<Category> category= categoryService.getAllCategory();
                model.addAttribute("categories",category);
             return "admin/add_product";
    }
    
    @GetMapping("/category")
    public String category(Model m){
                    m.addAttribute("categories",categoryService.getAllCategory());
                    return "admin/category";
    }
       @PostMapping("/saveCategory")
      public  String savecategory(@ModelAttribute Category category ,@RequestParam("file") MultipartFile file,
                                 HttpSession session ) throws IOException {
          String imageName = file != null ? file.getOriginalFilename() : "default.png";
          category.setImageName(imageName);
          Boolean exitCategory =  categoryService.exitCategory(category.getName());
//           System.out.println("exitCategory:"+exitCategory);
           if(exitCategory){
               session.setAttribute("errorMsg","Category Name is already exits");
           }
           else {
               Category savecategory = categoryService.saveCategory(category);

               if (savecategory != null) {
                   File saveFile = new ClassPathResource("static/img").getFile();
                   Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
                           + file.getOriginalFilename());
                   System.out.println(path);
                   Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                   session.setAttribute("succMsg", "Category added successfully");

               } else {
                   session.setAttribute("errorMsg", "Category not added");
               }
           }
           System.out.println("post request");

//           return "redirect:/carmodelget";
           return "redirect:/admin/category";

       }
        @GetMapping("/deleteCategory/{id}")
       public String  deletecategory(@PathVariable Integer id ,HttpSession session){
                Boolean category=  categoryService.deleteCategory(id);
                if(category){
                    session.setAttribute("errorMsg","Category deleted successfully");

                }
                else{
                    session.setAttribute("errorMsg","Category not deleted");
                }
                return "redirect:/admin/category";

       }
       @GetMapping("/loadEditCategory/{id}")
       public String loadEditCategory(@PathVariable int id, Model m) {
           m.addAttribute("category", categoryService.getCategoryById(id));
           return "admin/edit_category";
       }

        @PostMapping("/updateCategory")
       public  String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file
                ,HttpSession session) throws IOException {

               Category oldcategory=categoryService.getCategoryById(category.getId());

            String imageName= file.isEmpty() ? oldcategory.getImageName() : file.getOriginalFilename();

                if(category!=null){
                    oldcategory.setImageName(imageName);
                    oldcategory.setName(category.getName());
                    oldcategory.setIsActive(category.getIsActive());
                }

                Category update= categoryService.saveCategory(oldcategory);
                if(update!=null){

                    if (!file.isEmpty()) {
                        File saveFile = new ClassPathResource("static/img").getFile();

                        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
                                + file.getOriginalFilename());

                        // System.out.println(path);
                        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    }
                      session.setAttribute("succMsg", "Category updated successfully");


                }
               else {
                   session.setAttribute("errorMsg","Category not updated");
                }
               return "redirect:/admin/loadEditCategory/" + category.getId();

        }
    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
                              HttpSession session) throws IOException {

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product saveProduct = productService.saveProduct(product);

        if (saveProduct!=null) {

            File saveFile = new ClassPathResource("static/img").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                    + image.getOriginalFilename());

            System.out.println(path);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            session.setAttribute("succMsg", "Product Saved Success");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/admin/loadaddproduct";
    }
       @GetMapping("/products")
       public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch) {
           List<Product> products = null;
           if (ch != null && ch.length() > 0) {
               products = productService.searchProduct(ch);
           } else {
               products = productService.getAllProducts();
           }
           m.addAttribute("products", products);
           return "admin/products";
       }
      @GetMapping("/deleteProduct/{id}")
      public  String deleteProduct( @PathVariable Integer id ,HttpSession session){

              Boolean is= productService.deleteProduct(id);
              if(is){
                  session.setAttribute("succMsg","Product deleted successfully");
              }
              else{
                  session.setAttribute("errorMsg","Product not deleted");
              }return "redirect:/admin/products";
      }
      @GetMapping("/editProduct/{id}")
      public  String loadeditproduct(@PathVariable Integer id,Model m){
             Product product= productService.getProductBYId(id);
              m.addAttribute("product", product);
              m.addAttribute("categories",categoryService.getAllCategory());
              return "admin/edit_product";

      }
        @PostMapping("/updateProduct")
      public  String  updateProduct(@ModelAttribute Product product, @RequestParam("file")MultipartFile image,
                                    HttpSession session) throws IOException {


            if (product.getDiscount() < 0 || product.getDiscount() > 100) {
                session.setAttribute("errorMsg", "invalid Discount");}
                else{
                Product pro=   productService.updateProduct(product,image);
            if(pro!=null){
                session.setAttribute("succMsg","Product updated successfully");
            }
            else{
                session.setAttribute("errorMsg","something wrong on server");
            }}
            return "redirect:/admin/products";
      }


    @GetMapping("/users")
      public String getAllUsers(Model m) {
        List<UserDtls> users = userService.getUsers("ROLE_USER");
        m.addAttribute("users", users);
        return "/admin/users";
    }
    @GetMapping("/updateSts")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {
        Boolean f = userService.updateAccountStatus(id, status);
        if (f) {
            session.setAttribute("succMsg", "Account Status Updated");
        } else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }
        return "redirect:/admin/users";
    }


    @GetMapping("/orders")
    public String getAllOrders(Model m) {
        List<ProductOrder> allOrders = orderService.getAllOrders();
        m.addAttribute("orders", allOrders);
        return "/admin/order";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }

        Boolean updateOrder = orderService.updateOrderStatus(id, status);

        if (updateOrder) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "status not updated");
        }
        return "redirect:/admin/orders";
    }

}



