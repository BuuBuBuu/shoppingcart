/**
 * @author TANGYINGRUI
 * Generated by script
 */

package com.shoppingcart.springboot.service;

import jakarta.transaction.Transactional;
import java.util.List;

import com.shoppingcart.springboot.model.Product;
import com.shoppingcart.springboot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findByIsVisibleTrue(Pageable.unpaged()).getContent();
    }

    @Override
    public List<Product> getProductsByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).getContent();
    }

    @Override
    public Product getProductById(Long productId) {
        return (Product) productRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryContainingIgnoreCaseAndIsVisibleTrue(category, Pageable.unpaged()).getContent();
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndIsVisibleTrue(name, Pageable.unpaged()).getContent();
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Page<Product> getProductsByPage(Pageable pageable) {
        return productRepository.findByIsVisibleTrue(pageable);
    }

    // Search products based on the searchContent key word and visibility
    @Override
    public Page<Product> searchProducts(String searchContent, int page, int size, String sortOrder) {
        // Define how to order，asc / des ?
        Sort sort = (sortOrder != null && sortOrder.equalsIgnoreCase("asc"))
                ? Sort.by("price").ascending()
                : Sort.by("price").descending();

        // Create PageRequest object
        Pageable pageable = PageRequest.of(page, size, sort);

        // Return the results of products based on the searchContent key word and visibility
        return productRepository.findByIsVisibleNotAndNameContainingIgnoreCase(false, searchContent, pageable);
    }

    // Get product based on productId
    @Override
    public Product findProductById(Long productId) {
        return (Product) productRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    // List products based on category
    @Override
    public List<Product> findProductsByCategory(String category) {
        return productRepository.findByCategoryContainingIgnoreCaseAndIsVisibleTrue(category, Pageable.unpaged())
                .getContent();
    }

    @Override
    public void incrementProductViewCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);
}








//    @Override
//    public Page<Product> getProductsByPage(Pageable pageable) {
//        return productRepository.findAll(pageable);
//    }



//    @Override
//    public Product findProductById(Long productId) {
//        return productRepository.findByProductIdAndIsVisibleTrue(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
//    }
//
//    @Override
//    public List<Product> findProductByName(String name) {
//        return productRepository.findByNameContainingIgnoreCaseAndIsVisibleTrue(name, Pageable.unpaged()).getContent();
//    }
//
//    @Override
//    public List<Product> findProductByCategory(String category) {
//        return productRepository.findByCategoryContainingIgnoreCaseAndIsVisibleTrue(category, Pageable.unpaged())
//                .getContent();
//    }

    @Override
    public Product updateProduct(Long productId, Product productDetails) {
        Product existingProduct = findProductById(productId);
        existingProduct.setName(productDetails.getName());
        existingProduct.setCategory(productDetails.getCategory());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setDiscount(productDetails.getDiscount());
        existingProduct.setImageUrl(productDetails.getImageUrl());
        existingProduct.setStoreQuantity(productDetails.getStoreQuantity());
        existingProduct.setIsVisible(productDetails.getIsVisible());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProductById(Long productId) {
        Product product = findProductById(productId);
        productRepository.delete(product);
    }

    @Override
    public Page<Product> getAllProductsPaginated(int page, int size, String sortBy, String order) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> getVisibleProductsPaginated(int page, int size, String sortBy, String order) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return productRepository.findByIsVisibleTrue(pageable);
    }

    @Override
    public Page<Product> searchProductsPaginated(Long id, String name, String category, int page, int size, String sortBy, String order) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        // If productId is provided, search by productId
        if (id != null) {
            Product product = findProductById(id);
            return new PageImpl<>(List.of(product), pageable, 1);
        } else if (name != null && !name.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCaseAndIsVisibleTrue(name, pageable);
        } else if (category != null && !category.isEmpty()) {
            return productRepository.findByCategoryContainingIgnoreCaseAndIsVisibleTrue(category, pageable);
        } else {
            throw new IllegalArgumentException("At least one search criterion (id, name, category) must be provided.");
        }
    }

    @Override
    public List<Product> getAllProductsSorted(String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return productRepository.findAll(sort);
    }

    @Override
    public List<Product> searchProductsByIdOrName(Long productId, String name, String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        if (productId != null) {
            return productRepository.findByProductId(productId, sort);
        } else {
            return productRepository.findByNameContainingIgnoreCase(name, sort);
        }
    }

    // Updated method for multi-parameter search and pagination
    @Override
    public Page<Product> searchAndFilterProducts(Long productId, String name, String category, String sortBy, String order, Pageable pageable) {
        if (productId != null) {
            return productRepository.findByProductId(productId, pageable);
        } else if (name != null && !name.isEmpty() && category != null && !category.isEmpty()) {
            return productRepository.findByNameContainingAndCategoryContainingIgnoreCase(name, category, pageable);
        } else if (name != null && !name.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (category != null && !category.isEmpty()) {
            return productRepository.findByCategoryContainingIgnoreCase(category, pageable);
        } else {
            return productRepository.findAll(pageable);  // Correctly apply pagination here
        }
    }

    @Override
    public List<Product> findProductByCategory(String category) {
        return productRepository.findByCategoryContainingIgnoreCase(category);
    }

    @Override
    public List<Product> findProductByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
