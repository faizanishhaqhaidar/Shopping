package org.ecom.shopping_cart.repository;

import org.ecom.shopping_cart.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepo extends JpaRepository<UserDtls, Integer> {

    public UserDtls findByEmail(String username);

    public List<UserDtls> findByRole(String role);

    public UserDtls findByResetToken(String token);

}