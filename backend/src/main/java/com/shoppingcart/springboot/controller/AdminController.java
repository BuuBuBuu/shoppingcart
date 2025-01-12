/**
 * @author Benjamin
 * Generated by script
 */

package com.shoppingcart.springboot.controller;

import com.shoppingcart.springboot.model.Product;
import com.shoppingcart.springboot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  @Autowired
  private ProductService productService;

  // Add a new product
  @PostMapping("/products")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
    Product newProduct = productService.saveProduct(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
  }

  // Get ALL products with pagination and sorting, including sorting direction
  @GetMapping("/products")
  public ResponseEntity<Page<Product>> getAllProducts(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "productId") String sortBy,
          @RequestParam(defaultValue = "asc") String order // Added 'order' parameter with default value 'asc'
  ) {
    Page<Product> products = productService.getAllProductsPaginated(page, size, sortBy, order);
    return ResponseEntity.ok(products);
  }

  // Search products by ID, name, or category with pagination and sorting direction
  @GetMapping("/products/search")
  public ResponseEntity<Page<Product>> searchProducts(
          @RequestParam(required = false) Long productId,
          @RequestParam(required = false) String name,
          @RequestParam(required = false) String category,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "productId") String sortBy,
          @RequestParam(defaultValue = "asc") String order // Added 'order' parameter with default value 'asc'
  ) {
    Page<Product> products = productService.searchProductsPaginated(productId, name, category, page, size, sortBy, order);
    return ResponseEntity.ok(products);
  }

  // Get ONE product by ID
  @GetMapping("/products/{productId}")
  public ResponseEntity<Product> findProductById(@PathVariable Long productId) {
    Product product = productService.findProductById(productId);
    return ResponseEntity.ok(product);
  }

  // Update an existing product
  @PutMapping("/products/{productId}")
  public ResponseEntity<Product> updateProduct(
          @PathVariable Long productId,
          @Valid @RequestBody Product product) {
    Product updatedProduct = productService.updateProduct(productId, product);
    return ResponseEntity.ok(updatedProduct);
  }

  // Delete a product
  @DeleteMapping("/products/{productId}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
    productService.deleteProductById(productId);
    return ResponseEntity.noContent().build();
  }
}
