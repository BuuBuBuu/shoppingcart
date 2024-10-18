/**
 * @author MAMINGYANG
 * Generated by script
 */

package com.shoppingcart.springboot.service;

import com.shoppingcart.springboot.model.User;
import com.shoppingcart.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.*;

// UserDetailsServiceImpllsServiceImpl.java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    User user = userRepository.findByEmailOrUserName(usernameOrEmail, usernameOrEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return new org.springframework.security.core.userdetails.User(
            user.getUserName(),
            user.getPassword(),
            user.isActive(),
            true, true, true,
            user.getAuthorities()); // You need to define getAuthorities()
  }
}

