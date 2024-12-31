package org.ecom.shopping_cart.Controller;


import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.ecom.shopping_cart.model.Cart;
import org.ecom.shopping_cart.model.Category;
import org.ecom.shopping_cart.model.Product;
import org.ecom.shopping_cart.model.UserDtls;
import org.ecom.shopping_cart.service.CartService;
import org.ecom.shopping_cart.service.CategoryService;
import org.ecom.shopping_cart.service.ProductService;
import org.ecom.shopping_cart.service.UserService;
import org.ecom.shopping_cart.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.ecom.shopping_cart.utils.OrderStatus;

@Controller
public class HomeController {

     @Autowired
    private ProductService productService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private  UserService userService;

    @Autowired
    private CartService   cartService;


    public HomeController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String index(Model m) {

        List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
                .limit(6).toList();
        List<Product> allActiveProducts = productService.getAllActiveProducts("").stream()
//                .sorted((p1,p2)->p2.getId().compareTo(p1.getId()))
                .limit(8).toList();
        m.addAttribute("category", allActiveCategory);
        m.addAttribute("products", allActiveProducts);
        return "index";
    }

    @GetMapping("/signin")
    public  String login(){
        return "login";
    }
    @GetMapping("/register")
    public  String register(){

        return "register";
    }
    @ModelAttribute
    public void  getUserDetails(Principal p , Model m){

        if(p != null){
            String email= p.getName();
            UserDtls user= userService.getUserByEmail(email);
            m.addAttribute("user",user);

            Integer count= cartService.getCountCart(user.getId());
            m.addAttribute("count",count);


        }        List<Category> categories =categoryService.getAllActiveCategory();
          m.addAttribute("categories",categories);
    }
    @GetMapping("/product")
      public  String product(Model m ,@RequestParam(value="category",defaultValue ="") String category){

//        System.out.println(category);
        List<Product> products=productService.getAllActiveProducts(category);
        List<Category> categories=categoryService.getAllActiveCategory();
        m.addAttribute("products", products);
        m.addAttribute("categories", categories);
        m.addAttribute("paramValue", category);
        return "product";
          }
          @GetMapping("/product/{id}")
          public  String viewProduct(@PathVariable Integer id,Model m){

               Product product=productService.getProductBYId(id);
               m.addAttribute("product", product);
               return "view_product";
          }
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile
            file, HttpSession session)
            throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);
        UserDtls saveUser = userService.saveUser(user);

        if (!ObjectUtils.isEmpty(saveUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + file.getOriginalFilename());

                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Register successfully");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/register";
    }
    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session,
                                        HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        UserDtls userByEmail = userService.getUserByEmail(email);

        if (ObjectUtils.isEmpty(userByEmail)) {
            session.setAttribute("errorMsg", "Invalid email");
        } else {

            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            // Generate URL :
            // http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg

            String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

            Boolean sendMail = commonUtil.sendMail(url, email);

            if (sendMail) {
                session.setAttribute("succMsg", "Please check your email..Password Reset link sent");
            } else {
                session.setAttribute("errorMsg", "Somethong wrong on server ! Email not send");
            }
        }
        return "redirect:/forgot-password";
    }


    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

        UserDtls userByToken = userService.getUserByToken(token);

        if (userByToken == null) {
            m.addAttribute("msg", "Your link is invalid or expired !!");
            return "message";
        }
        m.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
                                Model m) {

        UserDtls userByToken = userService.getUserByToken(token);
        if (userByToken == null) {
            m.addAttribute("errorMsg", "Your link is invalid or expired !!");
            return "message";
        } else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            //session.setAttribute("succMsg", "Password change successfully");
            m.addAttribute("msg","Password change successfully");

            return "message";
        }

    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model m) {
        List<Product> searchProducts = productService.searchProduct(ch);
        m.addAttribute("products", searchProducts);
        List<Category> categories = categoryService.getAllActiveCategory();
        m.addAttribute("categories", categories);
        return "product";

    }


}
