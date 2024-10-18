import React, { useEffect, useState } from 'react';
import '../style/orderListPage.css'; // Import styles
import 'bootstrap/dist/css/bootstrap.min.css';
import { useNavigate } from "react-router-dom";
import axios from "axios"; // Import Axios for HTTP requests

const OrderListPage = () => {
    const navigate = useNavigate();
    const [orders, setOrders] = useState([]);
    const [selectedOrder, setSelectedOrder] = useState(null); // Selected order
    const [storedUserId, setStoredUserId] = useState(null);
    const [reviews, setReviews] = useState({}); // Store reviews for each product
    const [ratings, setRatings] = useState({}); // Store ratings for each product

    useEffect(() => {
        fetchSessionData();
    }, []);

    useEffect(() => {
        if (storedUserId) {
            fetchOrders();
        }
    }, [storedUserId]); // Fetch orders when the component mounts

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

    const fetchOrders = async () => {
        try {
            const response = await fetch(`http://localhost:8080/orders/purchase-history?userId=${storedUserId}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            const formattedOrders = data.map(order => ({
                ...order, // Include full order details
                date: new Date(order.orderDate).toLocaleDateString(), // Format date
                status: order.status.charAt(0).toUpperCase() + order.status.slice(1), // Capitalize first letter
                paid: order.status === 'completed',
                delivered: order.status === 'delivered'
            }));
            setOrders(formattedOrders);
        } catch (error) {
            console.error('Error fetching orders:', error);
        }
    };

    const handleViewDetails = (order) => {
        setSelectedOrder(order);
    };

    const handleCloseDetails = () => {
        setSelectedOrder(null);
    };

    const handlePay = (orderId) => {
        console.log(`Processing payment for Order ID: ${orderId}`);
        setOrders(orders.map(order =>
            order.orderId === orderId ? { ...order, paid: true, status: 'Completed' } : order
        ));
    };

    const handleReviewChange = (productId, value) => {
        setReviews(prevReviews => ({ ...prevReviews, [productId]: value }));
    };

    const handleRatingChange = (productId, value) => {
        setRatings(prevRatings => ({ ...prevRatings, [productId]: value }));
    };

    const handleSubmitReview = async (productId) => {
        const reviewText = reviews[productId];
        const rating = ratings[productId];

        if (!reviewText || !rating) {
            alert('Please enter a review and a rating');
            return;
        }

        const reviewData = {
            comment: reviewText,
            rating: rating
        };

        try {
            await axios.post('http://localhost:8080/reviews/add', {
                comment: reviewText,
                rating: rating
            }, {
                params: {
                    productId: productId,
                    userId: storedUserId
                },
                withCredentials: true });

            alert('Review submitted successfully');
            // Optionally, reset the review and rating state
            setReviews(prev => ({ ...prev, [productId]: '' }));
            setRatings(prev => ({ ...prev, [productId]: null }));
        } catch (error) {
            console.error('Error submitting review:', error);
            alert('Failed to submit review');
        }
    };

    return (
        <div className="container mt-5 order-list">
            <h2 className="text-center mb-4">Order History</h2>
            {orders.length === 0 ? (
                <div className="alert alert-warning text-center">No orders found.</div>
            ) : (
                <table className="table table-striped table-hover">
                    <thead className="table-dark">
                    <tr>
                        <th>Order ID</th>
                        <th>Date</th>
                        <th>Total</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {orders.map(order => (
                        <tr key={order.orderId} className="order-row">
                            <td>{order.orderId}</td>
                            <td>{order.date}</td>
                            <td>${order.finalPrice.toFixed(2)}</td>
                            <td>
                                <span className={`badge ${order.status === 'Completed' ? 'bg-success' : 'bg-warning'}`}>
                                    {order.status}
                                </span>
                            </td>
                            <td>
                                <button className="btn btn-info me-2" onClick={() => handleViewDetails(order)}>
                                    View Details
                                </button>
                                <button
                                    className="btn btn-primary"
                                    onClick={() => handlePay(order.orderId)}
                                    disabled={order.paid}
                                >
                                    {order.paid ? 'Paid' : 'Pay'}
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
            {selectedOrder && (
                <div className="modal fade show" style={{ display: 'block' }} id="orderDetailsModal">
                    <div className="modal-dialog modal-lg">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Order Details</h5>
                                <button type="button" className="btn-close" onClick={handleCloseDetails}></button>
                            </div>
                            <div className="modal-body">
                                <div className="mb-3">
                                    <p><strong>Order ID:</strong> {selectedOrder.orderId}</p>
                                    <p><strong>Date:</strong> {selectedOrder.date}</p>
                                    <p><strong>Total:</strong> ${selectedOrder.finalPrice.toFixed(2)}</p>
                                    <p><strong>Status:</strong> {selectedOrder.status}</p>
                                    <p><strong>Delivered:</strong> {selectedOrder.delivered ? 'Yes' : 'No'}</p>
                                </div>
                                <h5>Products in this Order:</h5>
                                <ul className="list-group">
                                    {selectedOrder.orderDetails.map(detail => (
                                        <li key={detail.orderDetailId} className="list-group-item">
                                            <img
                                                src={detail.product.imageUrl}
                                                alt={detail.product.name}
                                                style={{ width: '50px', marginRight: '10px' }}
                                            />
                                            <strong>{detail.product.name}</strong> - {detail.product.category}
                                            <p>Product ID: {detail.product.productId}</p>
                                            <p>Quantity: {detail.quantity}</p>
                                            <p>Price: ${detail.product.price.toFixed(2)}</p>

                                            {/* 添加评论和评分输入框 */}
                                            <div className="mb-3">
                                                <textarea
                                                    placeholder="Write your review here..."
                                                    value={reviews[detail.product.productId] || ''}
                                                    onChange={(e) => handleReviewChange(detail.product.productId, e.target.value)}
                                                    rows="3"
                                                    className="form-control"
                                                />
                                                <select
                                                    value={ratings[detail.product.productId] || ''}
                                                    onChange={(e) => handleRatingChange(detail.product.productId, e.target.value)}
                                                    className="form-select mt-2"
                                                >
                                                    <option value="" disabled>Select rating</option>
                                                    <option value="1">1 Star</option>
                                                    <option value="2">2 Stars</option>
                                                    <option value="3">3 Stars</option>
                                                    <option value="4">4 Stars</option>
                                                    <option value="5">5 Stars</option>
                                                </select>
                                                <button
                                                    className="btn btn-success mt-2"
                                                    onClick={() => handleSubmitReview(detail.product.productId)}
                                                >
                                                    Submit Review
                                                </button>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                            <div className="modal-footer">
                                <button className="btn btn-secondary" onClick={handleCloseDetails}>Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            <div className="text-center mt-4">
                <button className="btn btn-primary" onClick={() => navigate('/products')}>
                    Back to Products
                </button>
            </div>
        </div>
    );
};

export default OrderListPage;
