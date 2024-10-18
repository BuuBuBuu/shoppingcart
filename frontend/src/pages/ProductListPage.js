import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../style/productListPage.css'; // 自定义样式
import axios from 'axios';
import { debounce } from 'lodash'; // 引入 Lodash 防抖动

const ProductListPage = () => {
    const navigate = useNavigate();
    const [products, setProducts] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);
    const [sortOrder, setSortOrder] = useState('asc');
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const productsPerPage = 9;
    const [userName, setUserName] = useState('');
    const [storedUserId, setStoredUserId] = useState(null);

    useEffect(() => {
        fetchSessionData();
    }, []);

    useEffect(() => {
        fetchProducts(searchTerm); // 每次页面、排序或搜索内容变化时触发
    }, [currentPage, sortOrder, searchTerm]);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/user/${storedUserId}`, {
                    withCredentials: true,
                });
                setUserName(response.data.userName);
            } catch (error) {
                console.error('Error fetching user data:', error);
            }
        };
        if (storedUserId) fetchUser();
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

    const fetchProducts = async (searchValue = '') => {
        setLoading(true);
        try {
            const response = await axios.get(
                `http://localhost:8080/products/searchProducts?searchContent=${searchValue}&page=${currentPage - 1}&size=${productsPerPage}&sortOrder=${sortOrder}`
            );
            setProducts(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error('Error fetching products:', error);
        } finally {
            setLoading(false);
        }
    };

    const debouncedFetchProducts = useCallback(
        debounce((value) => fetchProducts(value), 500),
        [currentPage, sortOrder]
    );

    const handleSearch = (e) => {
        const value = e.target.value;
        setSearchTerm(value);
        setCurrentPage(1); // 搜索时重置为第一页
        debouncedFetchProducts(value);
    };

    const handleSort = (e) => {
        setSortOrder(e.target.value);
    };

    const handleViewDetails = (productId) => {
        handleIncreaseViewCount(productId);
        navigate(`/products/${productId}`);
    };

    const handleIncreaseViewCount = (productId) => {
        axios.post(`http://localhost:8080/products/viewCountIncrement/${productId}`)
            .then(response => {
                console.log('View count incremented successfully!');
            })
            .catch(error => {
                console.error('Failed to increment view count:', error);
            });
    }
    const handleUserProfile = () => {
        navigate(`/profile`);
    };

    return (
        <div className="container mt-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>Lalada</h2>
                <div className="d-flex align-items-center">
                    <div
                        className="d-flex align-items-center me-4"
                        style={{ cursor: 'pointer' }}
                        onClick={handleUserProfile}
                    >
                        <img
                            src="https://www.icecreamcookieco.com/cdn/shop/products/Cookies_And_Cream_800x.png?v=1569317116"
                            alt="Avatar"
                            className="rounded-circle me-2"
                            style={{ width: '40px', height: '40px', objectFit: 'cover' }}
                        />
                        <span>{userName || 'Guest'}</span>
                    </div>
                    <button className="btn btn-outline-primary" onClick={() => navigate('/cart')}>
                        🛒 Cart
                    </button>
                </div>
            </div>

            <div className="row mb-4">
                <div className="col-md-8">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Search for products..."
                        value={searchTerm}
                        onChange={handleSearch}
                    />
                </div>
                <div className="col-md-4">
                    <select onChange={handleSort} value={sortOrder} className="form-select">
                        <option value="asc">Sort by Price: Low to High</option>
                        <option value="desc">Sort by Price: High to Low</option>
                    </select>
                </div>
            </div>

            {loading ? (
                <div className="text-center">Loading products...</div>
            ) : products.length === 0 ? (
                <div className="text-center">No products found.</div>
            ) : (
                <div className="row">
                    {products.map((product) => (
                        <div key={product.productId} className="col-md-4 mb-4">
                            <div
                                className="card product-card h-100 shadow-sm"
                                onClick={() => handleViewDetails(product.productId)}
                                style={{ cursor: 'pointer' }}
                            >
                                <img
                                    src={product.imageUrl}
                                    alt={product.name}
                                    className="card-img-top product-image"
                                />
                                <div className="card-body text-center">
                                    <h5 className="card-title">{product.name}</h5>
                                    <p className="card-text">${product.price.toFixed(2)}</p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <nav aria-label="Page navigation">
                <ul className="pagination justify-content-center">
                    {Array.from({ length: totalPages }, (_, index) => (
                        <li className={`page-item ${currentPage === index + 1 ? 'active' : ''}`} key={index}>
                            <button className="page-link" onClick={() => setCurrentPage(index + 1)}>
                                {index + 1}
                            </button>
                        </li>
                    ))}
                </ul>
            </nav>
        </div>
    );
};

export default ProductListPage;
