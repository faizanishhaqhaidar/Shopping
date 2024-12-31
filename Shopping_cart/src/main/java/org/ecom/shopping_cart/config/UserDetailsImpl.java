package org.ecom.shopping_cart.config;

import org.ecom.shopping_cart.model.UserDtls;
import org.ecom.shopping_cart.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsImpl implements UserDetailsService {

    @Autowired
    private UserRepo  userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

             UserDtls user= userRepo.findByEmail(username);
             if(user==null){
                 throw new UsernameNotFoundException(username);}
             return new CostumUser(user);

    }
}
