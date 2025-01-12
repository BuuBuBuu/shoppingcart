/**
 * @author MAMINGYANG, Benjamin
 * Generated by script
 */

package com.shoppingcart.springboot.repository;

import com.shoppingcart.springboot.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // 根据客户ID查找所有地址
    List<Address> findByCustomer_CustomerId(Long customerId);

    List<Address> findByCustomer_CustomerIdAndIsVisibleTrue(Long customerId);
}
