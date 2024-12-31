package org.ecom.shopping_cart.config;

//mport org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    @Lazy
    private  AuthFailureHandlerImpl authFailureHandler;

  @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService getUserDetailsService() {
      return new UserDetailsImpl();
    }
    @Bean
    public DaoAuthenticationProvider getDaoAuthenticationProvider() {
     DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
     authProvider.setUserDetailsService(getUserDetailsService());
     authProvider.setPasswordEncoder(getPasswordEncoder());
     return authProvider;

    }
    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity http)
    throws Exception {

        http.csrf(AbstractHttpConfigurer::disable).
                cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req->req.
                        requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/**").permitAll())
                .formLogin(form->form.loginPage("/signin")
                        .loginProcessingUrl("/login")
						.defaultSuccessUrl("/")
                        .failureHandler(authFailureHandler)
                        .successHandler(authenticationSuccessHandler)        );
//                        .logout



        return http.build();
    }
}
