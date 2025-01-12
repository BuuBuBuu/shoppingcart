/**
 * @author HUANGZEYUAN
 * Generated by script
 */

package com.shoppingcart.springboot.repository;

import com.shoppingcart.springboot.model.ShoppingCartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ShoppingCartProductRepository extends JpaRepository<ShoppingCartProduct, Long> {

  Optional<ShoppingCartProduct> findByShoppingCartShoppingCartIdAndProductProductId(Long cartId, Long productId);

  void deleteByShoppingCartShoppingCartIdAndProductProductId(Long cartId, Long productId);

  void deleteByShoppingCartShoppingCartId(Long cartId);

}
