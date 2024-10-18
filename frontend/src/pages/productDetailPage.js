import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../style/productDetailPage.css';

const ProductDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [reviews, setReviews] = useState([]);
    const [averageRating, setAverageRating] = useState(0);
    const [quantity, setQuantity] = useState(1); // 添加数量状态
    const [storedUserId, setStoredUserId] = useState(null);

    useEffect(() => {
        fetchSessionData();
    }, []);

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

    useEffect(() => {
        const fetchProductDetails = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/products/getProductDetails/${id}`);
                setProduct(response.data);
                console.log(response.data);

                // 获取评论
                const reviewsResponse = await axios.get(`http://localhost:8080/reviews/products/${id}`);
                setReviews(reviewsResponse.data);

            } catch (error) {
                console.error('Error fetching product details:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchProductDetails();
    }, [id]);

    // 新增的 useEffect 用于获取平均评分
    useEffect(() => {
        const fetchAverageRating = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/reviews/averageRating/${id}`);
                setAverageRating(response.data);
            } catch (error) {
                console.error('Error fetching average rating:', error);
            }
        };

        fetchAverageRating();
    }, [id]);

    useEffect(() => {
        if (reviews.length > 0) {
            const avgRating = reviews.reduce((acc, review) => acc + review.rating, 0) / reviews.length;
            setAverageRating(avgRating || 0);
        }
    }, [reviews]);

    const handleAddToCart = async () => {
        try {
            const userId = storedUserId; // 假设用户 ID 是 1，实际应该根据登录用户来设置
            console.log(userId);
            console.log(product.productId);
            console.log(quantity);
            const response = await axios.get('http://localhost:8080/shoppingCart/add-product', {
                params: {
                    userId,
                    productId: product.productId,
                    quantity,
                },
                withCredentials: true,
            });
            console.log(`Added to cart: ${product.name}`, response.data);
            alert('Product added to cart successfully!');
        } catch (error) {
            console.error('Error adding product to cart:', error);
            alert('Failed to add product to cart. Please try again.');
        }
    };

    const handleBack = () => {
        navigate(-1);
    };

    if (loading) {
        return <div className="text-center">Loading...</div>;
    }

    if (!product) {
        return <h2 className="text-danger text-center">Product not found!</h2>;
    }

    return (
        <div className="container mt-5">
            <div className="product-detail card p-4 shadow-lg">
                <div className="d-flex align-items-start">
                    <img
                        src={product.imageUrl}
                        alt={product.name}
                        className="img-fluid product-image"
                    />
                    <div className="ms-3">
                        <h2>{product.name}</h2>
                        <p className="text-muted">{product.description}</p>
                        <p className="font-weight-bold">Price: <span
                            className="text-success">${product.price.toFixed(2)}</span></p>
                        <p className="font-weight-bold">Category: {product.category}</p>
                        <p className="font-weight-bold">Tags: {product.tags}</p>
                        <p className="font-weight-bold">View Count: {product.viewCount}</p>
                        <div className="mt-3">
                            <label htmlFor="quantity" className="font-weight-bold">Quantity:</label>
                            <input
                                type="number"
                                id="quantity"
                                value={quantity}
                                onChange={(e) => setQuantity(Number(e.target.value))}
                                min="1"
                                className="form-control w-25"
                            />
                        </div>
                        <div className="d-flex justify-content-between mt-3">
                            <button className="btn btn-primary me-2" onClick={handleAddToCart}>Add to Cart</button>
                            <button className="btn btn-secondary" onClick={handleBack}>Back to Product List</button>
                        </div>
                    </div>
                </div>
            </div>

            <div className="mt-4">
                <h4>User Reviews</h4>
                <p className="font-weight-bold">Average Rating: {averageRating.toFixed(1)} / 5</p>
                <ul className="list-group review-list">
                    {reviews.map((review, index) => (
                        <li key={index} className="list-group-item review-item">
                            <strong>{review.user}</strong> - {review.rating} Stars
                            <p>{review.comment}</p>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default ProductDetailPage;
