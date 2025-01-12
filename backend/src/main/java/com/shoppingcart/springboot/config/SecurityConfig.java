/**
 * @author ZENGXING
 * Generated by script
 */

package com.shoppingcart.springboot.config;

//import com.shoppingcart.springboot.service.UserDetailsServiceImpl;
import com.shoppingcart.springboot.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


  // Use NoOpPasswordEncoder to disable password hashing (use plaintext for testing purposes)

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 使用 BCrypt 进行密码哈希
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

  @Bean
  public AuthenticationManager authenticationManager(
          AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
              .csrf(csrf -> csrf.disable())  // Disable CSRF for simplicity
              .authorizeHttpRequests(authz -> authz
                      .requestMatchers("/admin-ui/login", "/css/**").permitAll()  // Allow access to login page and static resources like CSS
                      .requestMatchers("/admin-ui/**").hasRole("ADMIN")  // Restrict access to admin routes to ADMIN role
                      .anyRequest().permitAll())  // Allow all other requests
              .formLogin(form -> form
                      .loginPage("/admin-ui/login")  // Specify custom login page
                      .loginProcessingUrl("/admin-ui/login")  // URL for form submission
                      .defaultSuccessUrl("/admin-ui/products", true)  // Redirect to this URL after successful login
                      .permitAll())  // Allow access to login page for everyone
              .logout(logout -> logout
                      .logoutUrl("/admin-ui/logout")  // URL to trigger logout
                      .logoutSuccessUrl("/admin-ui/login?logout")  // Redirect here after logout
                      .invalidateHttpSession(true)  // Invalidate the session after logout
                      .deleteCookies("JSESSIONID")  // Remove session cookie after logout
                      .permitAll())  // Allow everyone to access logout functionality
              .authenticationProvider(authenticationProvider());  // Use custom auth provider
      return http.build();

  }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // 禁用 CSRF 保护
//                .authorizeHttpRequests(authz -> authz
//                        .anyRequest().permitAll() // 允许所有请求
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//                .authenticationProvider(authenticationProvider())
//                .httpBasic(Customizer.withDefaults());
//
//        return http.build();
//    }

}

