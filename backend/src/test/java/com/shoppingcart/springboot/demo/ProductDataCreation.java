package com.shoppingcart.springboot.demo;

import com.shoppingcart.springboot.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductDataCreation {
    @Autowired
    private ProductRepository prepo;
    @Test
    void conTextLoad() {
//Product

    }
}
