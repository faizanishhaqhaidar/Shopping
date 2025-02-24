package org.ecom.shopping_cart.config;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ecom.shopping_cart.model.UserDtls;
import org.ecom.shopping_cart.repository.UserRepo;
import org.ecom.shopping_cart.service.UserService;
import org.ecom.shopping_cart.utils.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException, ServletException, IOException {

        String email = request.getParameter("username");

        UserDtls userDtls = userRepo.findByEmail(email);

        if (userDtls.getIsEnable()) {

            if (userDtls.getAccountNonLocked()) {

                if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
                    userService.increaseFailedAttempt(userDtls);
                } else
                {
                    userService.userAccountLock(userDtls);
                    exception = new LockedException("Your account is locked !! failed attempt 3");
                }
                }
            else {

                if (userService.unlockAccountTimeExpired(userDtls)) {
                    exception = new LockedException("Your account is unlocked !! Please try to login");
                } else {
                    exception = new LockedException("your account is Locked !! Please try after sometimes");
                }
            }

        }

        else {
            exception = new LockedException("your account is inactive");
        }

        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }

}