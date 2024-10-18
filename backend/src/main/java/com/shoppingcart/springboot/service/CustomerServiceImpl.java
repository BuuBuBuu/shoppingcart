package com.shoppingcart.springboot.service;


import com.shoppingcart.springboot.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shoppingcart.springboot.model.Customer;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {


  @Autowired
  private CustomerRepository customerRepository;

  @Override
  public Customer findCustomerById(Long userId) {
    return customerRepository.findByUser_UserId(userId)
        .orElseThrow(() -> new RuntimeException("Customer not found with user_id: " + userId));
  }

  @Override
  public List<Customer> getAllCustomers() {
    return customerRepository.findAll();
  }

  // 根据 userId 获取头像
  public String getProfileImageByUserId(Long userId) {
    Optional<Customer> optionalCustomer = customerRepository.findByUser_UserId(userId);
    if (optionalCustomer.isPresent()) {
      return optionalCustomer.get().getProfileImage();
    } else {
      throw new RuntimeException("Profile picture for the user not found: " + userId);
    }
  }

  // 根据 userId 获取 Customer 详情
  public Customer getCustomerDetailsByUserId(Long userId) {
    return customerRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new RuntimeException("User details not found: " + userId));
  }

}
