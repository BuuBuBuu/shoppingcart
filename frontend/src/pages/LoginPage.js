import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../style/LoginPage.css';
import axios from 'axios';

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [rememberMe, setRememberMe] = useState(false);
    const [forgotPasswordEmail, setForgotPasswordEmail] = useState('');
    const [isForgotPassword, setIsForgotPassword] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const savedEmail = localStorage.getItem('savedEmail');
        const savedPassword = localStorage.getItem('savedPassword');
        const savedRememberMe = localStorage.getItem('rememberMe') === 'true';

        if (savedRememberMe) {
            if (savedEmail) setEmail(savedEmail);
            if (savedPassword) setPassword(savedPassword);
            setRememberMe(true);
        }
    }, []);

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await axios.post('http://localhost:8080/login/', null, {
                params: { email, password },
                withCredentials: true,
            });

            console.log('Login successful:', response.data);

            if (rememberMe) {
                localStorage.setItem('savedEmail', email);
                localStorage.setItem('savedPassword', password); // 如果安全性允许
                localStorage.setItem('rememberMe', 'true');
            } else {
                localStorage.removeItem('savedEmail');
                localStorage.removeItem('savedPassword');
                localStorage.setItem('rememberMe', 'false');
            }

            navigate('/products'); // 登录成功后跳转到产品页面
        } catch (err) {
            setError(err.response?.data || 'Login failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };


    const handleGuestLogin = async () => {
        setError('');
        try {
            console.log('Logged in as guest');
            navigate('/products');
        } catch (err) {
            alert('Failed to log in as guest. Please try again.');
        }
    };

    const handleRegister = () => navigate('/register');
    const handleForgotPassword = () => setIsForgotPassword(true);

    const handleSendResetEmail = async () => {
        if (forgotPasswordEmail) {
            try {
                const response = await axios.post(
                    `http://127.0.0.1:8080/user/reset_password?email=${encodeURIComponent(forgotPasswordEmail)}`
                );
                alert(response.data);
                setForgotPasswordEmail('');
                setIsForgotPassword(false);
            } catch (err) {
                alert(err.response?.data || 'Failed to send reset email.');
            }
        } else {
            alert('Please enter your email address.');
        }
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-5">
                    <div className="card shadow-lg p-4">
                        <h2 className="text-center mb-4 text-primary">Sign in to Shopping Cart</h2>
                        <form onSubmit={handleLogin}>
                            <div className="mb-3">
                                <label className="form-label">Email:</label>
                                <input
                                    type="email"
                                    className="form-control"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="mb-3">
                                <label className="form-label">Password:</label>
                                <input
                                    type="password"
                                    className="form-control"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="form-check mb-3">
                                <input
                                    type="checkbox"
                                    className="form-check-input"
                                    checked={rememberMe}
                                    onChange={() => setRememberMe(!rememberMe)}
                                />
                                <label className="form-check-label">Remember Me</label>
                            </div>
                            {error && <p className="text-danger">{error}</p>}
                            <div className="d-grid gap-2">
                                <button type="submit" className="btn btn-primary" disabled={loading}>
                                    {loading ? 'Logging in...' : 'Login'}
                                </button>
                                <button type="button" className="btn btn-light" onClick={handleGuestLogin} disabled={loading}>
                                    {loading ? 'Loading...' : 'Login as Guest'}
                                </button>
                            </div>
                        </form>
                        <div className="mt-3 d-flex justify-content-between">
                            <button className="btn btn-link" onClick={handleRegister}>Register</button>
                            <button className="btn btn-link" onClick={handleForgotPassword}>Forgot Password?</button>
                        </div>
                    </div>
                </div>
            </div>

            {isForgotPassword && (
                <div className="modal show" style={{ display: 'block', zIndex: 1050 }}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Reset Password</h5>
                                <button type="button" className="btn-close" onClick={() => setIsForgotPassword(false)}></button>
                            </div>
                            <div className="modal-body">
                                <div className="mb-3">
                                    <label className="form-label">Email:</label>
                                    <input
                                        type="email"
                                        className="form-control"
                                        value={forgotPasswordEmail}
                                        onChange={(e) => setForgotPasswordEmail(e.target.value)}
                                        required
                                    />
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button className="btn btn-primary" onClick={handleSendResetEmail}>
                                    Send Reset Email
                                </button>
                                <button className="btn btn-secondary" onClick={() => setIsForgotPassword(false)}>
                                    Cancel
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default LoginPage;
