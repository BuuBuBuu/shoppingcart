/**
 * @author ZENGXING, Benjamin, MAMINGYANG
 * Generated by script
 */

package com.shoppingcart.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders") // 修改表名避免使用 SQL 保留字

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    private double finalPrice;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "address_id")
    private Address deliveryAddress;


    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;
    // 使用 String 字段来管理订单状态
    private String status;




    public Order() {
    }

    public Order(String status, Customer customer, List<OrderDetail> orderDetails, double finalPrice,
                 Address deliveryAddress, Date orderDate) {
        this.status = status;
        this.customer = customer;
        this.orderDetails = orderDetails;
        this.finalPrice = finalPrice;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = orderDate;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
