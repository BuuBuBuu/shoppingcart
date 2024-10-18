import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../style/cartPage.css';
import axios from "axios";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash, faShoppingCart } from '@fortawesome/free-solid-svg-icons';

const CartPage = () => {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState([]);
    const [isCheckout, setIsCheckout] = useState(false);
    const [discountCode, setDiscountCode] = useState('');
    const [discountAmount, setDiscountAmount] = useState(0);
    const [storedUserId, setStoredUserId] = useState(null);
    const [isLoading, setIsLoading] = useState(true); // Loading state
    const [selectedItems, setSelectedItems] = useState([]); // State for selected items
    const [showModal, setShowModal] = useState(false); // Modal visibility state

    useEffect(() => {
        fetchSessionData();
    }, []);

    useEffect(() => {
        if (storedUserId) {
            fetchCartItems().finally(() => setIsLoading(false)); // Hide loading after data fetch
        }
    }, [storedUserId]);

    const fetchSessionData = async () => {
        try {
            const sessionResponse = await axios.get('http://localhost:8080/session/getSessionData', {
                withCredentials: true,
            });
            setStoredUserId(sessionResponse.data.user_id);
        } catch (error) {
            console.error('Error fetching session data:', error);
        }
    };

    const fetchCartItems = async () => {
        if (storedUserId) {
            try {
                const response = await axios.get(`http://localhost:8080/shoppingCart/view-cart`, {
                    params: { userId: storedUserId },
                    withCredentials: true,
                });
                setCartItems(response.data.shoppingCartProducts);
            } catch (error) {
                console.error('Error fetching cart items:', error);
            }
        }
    };

    const updateQuantity = (id, newQuantity) => {
        setCartItems(cartItems.map(item =>
            item.product.productId === id ? { ...item, quantity: newQuantity } : item
        ));
    };

    const removeItem = async (productId,quantity) => {
        try {
            const response = await axios.post('http://localhost:8080/shoppingCart/remove-product', null, {
                params: {
                    userId: storedUserId,
                    productId: productId,
                    quantity: quantity,
                },
                withCredentials: true,
            });
            setCartItems(response.data.shoppingCartProducts);
        } catch (error) {
            console.error('Error removing product:', error);
            alert('Failed to remove product from the cart.');
        }
    };

    const clearCart = async () => {
        try {
            const response = await axios.post('http://localhost:8080/shoppingCart/clear-cart', null, {
                params: { userId: storedUserId }, // 使用存储的用户ID
                withCredentials: true,
            });

            if (response.status === 200) {
                setCartItems([]); // 清空购物车的前端状态
                alert('购物车已清空');
            } else {
                alert('清空购物车失败');
            }
        } catch (error) {
            console.error('Error clearing cart:', error);
            alert('无法清空购物车，请稍后再试');
        }
    };


    const handleCheckboxChange = (id) => {
        setCartItems(cartItems.map(item =>
            item.product.productId === id ? { ...item, isChecked: !item.isChecked } : item
        ));
    };

    const calculateTotal = () => {
        return cartItems.reduce((total, item) => {
            return item.isChecked ? total + item.product.price * item.quantity : total;
        }, 0);
    };

    const calculateDiscountedTotal = (total) => {
        return total - discountAmount;
    };

    const handleCheckout = () => {
        const selected = cartItems.filter(item => item.isChecked);
        setSelectedItems(selected);
        if (selected.length === 0) {
            alert('请选择至少一件商品进行结账');
            return;
        }
        setShowModal(true); // 显示模态框
    };

    const handleApplyDiscount = () => {
        if (discountCode === 'DISCOUNT10') {
            const total = calculateTotal();
            setDiscountAmount(total * 0.1);
        } else {
            alert('Invalid discount code');
            setDiscountAmount(0);
        }
    };

    const handleConfirmCheckout = async () => {
        try {
            const productIds = selectedItems.map(item => item.product.productId);
            const quantities = selectedItems.map(item => item.quantity);

            // 生成请求体
            const requestBody = {
                userId: storedUserId,  // 使用已存储的用户ID
                // voucherCode: '',  // 代金券可选
            };

            // 构建查询参数
            const params = {
                ...requestBody,
                // 将 productIds 和 quantities 分开处理
                productIds: productIds.join(','),  // 将数组合并为字符串，使用逗号分隔
                quantities: quantities.join(','),  // 将数组合并为字符串，使用逗号分隔
            };

            const response = await axios.post('http://localhost:8080/shoppingCart/create-order', requestBody, {
                params: params,  // 传递 params
                withCredentials: true,
            });

            if (response.status === 200) {
                alert(`订单创建成功，总价：$${response.data.toFixed(2)}`);
                setCartItems([]);  // 清空购物车
                navigate('/orders');  // 跳转到订单确认页面
            }
        } catch (error) {
            console.error('Error creating order:', error);
            alert('创建订单失败，请稍后再试');
        }
    };



    const selectedItemsCount = cartItems.filter(item => item.isChecked).length;

    if (isLoading) {
        return (
            <div className="text-center mt-5">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-5 cart-container">
            <h2 className="mb-4 text-center">
                <FontAwesomeIcon icon={faShoppingCart} /> Shopping Cart
            </h2>
            {cartItems.length === 0 ? (
                <p className="text-center">Your cart is empty.</p>
            ) : (
                <div className="list-group">
                    {cartItems.map(item => (
                        <div key={item.product.productId} className="list-group-item d-flex justify-content-between align-items-center mb-3 p-3 border rounded shadow">
                            <div className="d-flex align-items-center w-100">
                                <img
                                    src={item.product.imageUrl}
                                    alt={item.product.name}
                                    className="img-fluid me-3"
                                    style={{ width: '70px', height: '70px', cursor: 'pointer' }}
                                    onClick={() => navigate(`/products/${item.product.productId}`)}
                                />
                                <div className="flex-grow-1">
                                    <div className="form-check">
                                        <input
                                            type="checkbox"
                                            className="form-check-input"
                                            checked={item.isChecked || false}
                                            onChange={() => handleCheckboxChange(item.product.productId)}
                                        />
                                        <label className="form-check-label">{item.product.name}</label>
                                    </div>
                                    <p className="mb-1" style={{ fontSize: '0.9rem' }}>{item.product.description}</p>
                                    <span className="fw-bold">Price: ${item.product.price.toFixed(2)}</span>
                                    <span className="ms-3">
                                        Subtotal: ${(item.product.price * item.quantity).toFixed(2)}
                                    </span>
                                </div>
                                <div className="d-flex align-items-center">
                                    <input
                                        type="number"
                                        className="form-control me-2"
                                        value={item.quantity}
                                        onChange={e => updateQuantity(item.product.productId, +e.target.value)}
                                        min="1"
                                        style={{ width: '60px' }}
                                    />
                                    <button
                                        className="btn btn-danger btn-sm"
                                        onClick={() => removeItem(item.product.productId, item.quantity)}
                                    >
                                        <FontAwesomeIcon icon={faTrash} />
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
            <div className="mt-4 d-flex justify-content-between">
                <h3>Selected Items: {selectedItemsCount}</h3>
                <h3>Total: ${calculateTotal().toFixed(2)}</h3>
            </div>
            {discountAmount > 0 && (
                <h4 className="mt-2 text-end text-success">Discount Applied: -${discountAmount.toFixed(2)}</h4>
            )}
            {/*<h3 className="mt-4 text-end">Total after Discount: ${calculateDiscountedTotal(calculateTotal()).toFixed(2)}</h3>*/}
            <div className="d-flex justify-content-between mt-3">
                <button className="btn btn-success" onClick={handleCheckout} disabled={calculateTotal() === 0}>
                    Checkout
                </button>
                <button className="btn btn-secondary" onClick={() => navigate('/products')}>Back to Products</button>
                <button className="btn btn-warning" onClick={clearCart}>Clear Cart</button>
            </div>

            {/* Modal for Selected Items */}
            <div className={`modal fade ${showModal ? 'show' : ''}`} style={{ display: showModal ? 'block' : 'none' }} tabIndex="-1" role="dialog">
                <div className="modal-dialog" role="document">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title">Selected Items</h5>

                        </div>
                        <div className="modal-body">
                            {selectedItems.length === 0 ? (
                                <p>No items selected.</p>
                            ) : (
                                <ul className="list-group mb-3">
                                    {selectedItems.map(item => (
                                        <li key={item.product.productId} className="list-group-item">
                                            <img src={item.product.imageUrl} alt={item.product.name} style={{ width: '50px', height: '50px', marginRight: '10px' }} />
                                            {item.product.name} - ${item.product.price.toFixed(2)} x {item.quantity}
                                        </li>
                                    ))}
                                </ul>
                            )}
                            <h5>Total: ${calculateTotal().toFixed(2)}</h5>
                            {discountAmount > 0 && (
                                <h5>Discount Applied: -${discountAmount.toFixed(2)}</h5>
                            )}
                            {/*<h4>Total after Discount: ${calculateDiscountedTotal(calculateTotal()).toFixed(2)}</h4>*/}
                            {/*<input*/}
                            {/*    type="text"*/}
                            {/*    className="form-control mb-2"*/}
                            {/*    placeholder="Enter discount code"*/}
                            {/*    value={discountCode}*/}
                            {/*    onChange={e => setDiscountCode(e.target.value)}*/}
                            {/*/>*/}
                            {/*<button className="btn btn-primary" onClick={handleApplyDiscount}>Apply Discount</button>*/}
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Close</button>
                            <button
                                type="button"
                                className="btn btn-success"
                                onClick={handleConfirmCheckout}
                            >
                                Confirm Checkout
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CartPage;
