import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // 导入 useNavigate
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css'; // 导入 Bootstrap CSS
import '../style/RegisterPage.css'; // 自定义 CSS 文件

const RegisterPage = () => {
    const [userName, setUserName] = useState(''); // 用户名状态
    const [firstName, setFirstName] = useState(''); // 名字状态
    const [lastName, setLastName] = useState(''); // 姓氏状态
    const [email, setEmail] = useState(''); // 邮箱状态
    const [password, setPassword] = useState(''); // 密码状态
    const [confirmPassword, setConfirmPassword] = useState(''); // 确认密码状态
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false); // 加载状态
    const navigate = useNavigate(); // 创建跳转函数

    const handleRegister = async (e) => {
        e.preventDefault(); // 阻止页面刷新
        setError(''); // 清除之前的错误
        setLoading(true); // 开始加载

        if (password !== confirmPassword) {
            setError('Passwords do not match.');
            setLoading(false);
            return;
        }

        try {
            const response = await axios.post('http://localhost:8080/login/register', {
                email,
                password,
                userName,
                firstName,
                lastName,
                isStaff: false, // 默认非管理员注册
                isActive: true,
            });

            window.alert('Registration successful!');
            navigate('/'); // 注册成功后跳转到登录页面
        } catch (err) {
            // 检查后端是否返回了特定的错误信息
            const errorMessage = err.response?.data?.message || 'Registration failed. Please try again.';
            setError(errorMessage);
        } finally {
            setLoading(false); // 结束加载
        }
    };

    const handleBackToLogin = () => {
        navigate('/'); // 返回登录页面
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-6">
                    <h2 className="text-center mb-4">Create an Account</h2>
                    <div className="card shadow-lg">
                        <div className="card-body">
                            <form onSubmit={handleRegister}>
                                <div className="mb-3">
                                    <label className="form-label">Username:</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={userName}
                                        onChange={(e) => setUserName(e.target.value)}
                                        required
                                    />
                                </div>
                                <div className="mb-3">
                                    <label className="form-label">First Name (Optional):</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={firstName}
                                        onChange={(e) => setFirstName(e.target.value)}
                                    />
                                </div>
                                <div className="mb-3">
                                    <label className="form-label">Last Name (Optional):</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={lastName}
                                        onChange={(e) => setLastName(e.target.value)}
                                    />
                                </div>
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
                                <div className="mb-3">
                                    <label className="form-label">Confirm Password:</label>
                                    <input
                                        type="password"
                                        className="form-control"
                                        value={confirmPassword}
                                        onChange={(e) => setConfirmPassword(e.target.value)}
                                        required
                                    />
                                </div>
                                {error && <p className="text-danger">{error}</p>}
                                <div className="d-grid gap-2">
                                    <button className="btn btn-primary" type="submit" disabled={loading}>
                                        {loading ? 'Registering...' : 'Register'}
                                    </button>
                                </div>
                            </form>
                            <div className="mt-3">
                                <button className="btn btn-link" onClick={handleBackToLogin}>
                                    Back to Login
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default RegisterPage;
