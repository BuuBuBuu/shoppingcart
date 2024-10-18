import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../style/userProfilePage.css';

const UserProfilePage = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState({
        firstName: '',
        lastName: '',
        email: '',
        userName: '',
        isStaff: false,
        isActive: true,
        avatar: null,
        socialMedia: '',
    });

    const [isEditing, setIsEditing] = useState(false);
    const [isChangingPassword, setIsChangingPassword] = useState(false);
    const [isManagingAddress, setIsManagingAddress] = useState(false); // Manage Address state
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [formData, setFormData] = useState({ ...user });
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [oldPassword, setOldPassword] = useState('');
    const [storedUserId, setStoredUserId] = useState(null);
    const [addresses, setAddresses] = useState([]); // Addresses state
    const [newAddress, setNewAddress] = useState({ streetAddress: '', city: '', state: '', postalCode: '' }); // New address state

    useEffect(() => {
        fetchSessionData();
    }, []);

    useEffect(() => {
        const fetchUser = async () => {
            if (storedUserId) {
                try {
                    const response = await axios.get(`http://localhost:8080/user/${storedUserId}`, {
                        withCredentials: true, // Ensure request includes cookie
                    });
                    setUser(response.data);
                    setFormData(response.data);
                } catch (error) {
                    console.error('Error fetching user data:', error);
                    setError('Error fetching user information.');
                }
            }
        };

        fetchUser();
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

    const handleManageAddress = async () => {
        setError(''); // 清除错误信息
        await fetchAddresses(); // 获取最新地址
        setIsManagingAddress(true); // 打开管理地址弹窗
    };



    const handleAddAddress = async () => {
        try {
            const response = await axios.post(
                `http://localhost:8080/addresses/create`,
                newAddress,
                {
                    params: { userId: storedUserId }, // 传递 userId
                    withCredentials: true,
                }
            );

            setSuccess(response.data); // 显示成功消息
            setNewAddress({ streetAddress: '', city: '', state: '', postalCode: '' }); // 重置输入框
            fetchAddresses(); // 刷新地址列表
        } catch (error) {
            console.error('Error adding address:', error);
            setError('Failed to add address.');
        }
    };

    const fetchAddresses = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/addresses/getAll`, {
                params: { userId: storedUserId },
                withCredentials: true,
            });
            setAddresses(response.data); // 更新地址列表
        } catch (error) {
            console.error('Error fetching addresses:', error);
            setError('Failed to load addresses.');
        }
    };


    const handleDeleteAddress = (index) => {
        const updatedAddresses = addresses.filter((_, i) => i !== index);
        setAddresses(updatedAddresses);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };

    const handleAddressChange = (e) => {
        const { name, value } = e.target;
        setNewAddress({
            ...newAddress,
            [name]: value,
        });
    };

    const handleEditProfile = () => {
        setIsEditing(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!formData.firstName || !formData.lastName || !formData.userName) {
            setError('All fields are required.');
            setSuccess('');
            return;
        }

        try {
            // 发送请求更新用户信息
            const response = await axios.post(`http://localhost:8080/user/update-profile`, {
                "firstName": formData.firstName,
                "lastName": formData.lastName,
                "userName":  formData.userName
            }, {
                params: { userId: storedUserId },
                withCredentials: true,
            });

            // 更新用户状态
            setUser({ ...formData, avatar: user.avatar });
            setSuccess(response.data || 'User information updated successfully!');
            setError('');
            setIsEditing(false);
        } catch (error) {
            console.error('Error updating user information:', error);
            setError('Failed to update user information.');
            setSuccess('');
        }
    };


    const handleViewAllOrders = () => {
        navigate('/orders');
    };

    const handleViewProducts = () => {
        navigate('/products');
    };

    const handleViewCart = () => {
        navigate('/cart');
    };

    const handleLogout = async () => {
        if (storedUserId) {
            try {
                await axios.post(`http://localhost:8080/user/logout`, null, {
                    params: { userId: storedUserId },
                    withCredentials: true,
                });
                sessionStorage.removeItem('user_id');
                navigate('/');
            } catch (error) {
                console.error('Logout error:', error);
                alert('Error logging out, please try again.');
            }
        } else {
            navigate('/');
        }
    };



    const handleChangePassword = async () => {
        if (newPassword !== confirmPassword) {
            setError('Passwords do not match.');
            return;
        }

        try {
            const email = user.email;
            const response = await axios.post('http://localhost:8080/user/change-password', null, {
                params: {
                    email: email,
                    oldPassword: oldPassword,
                    newPassword: newPassword,
                },
            });
            setSuccess('Password changed successfully!');
            setError('');
            setIsChangingPassword(false);
        } catch (error) {
            setError(`Error: ${error.response?.data || 'Password change failed'}`);
            setSuccess('');
        } finally {
            setOldPassword('');
            setNewPassword('');
            setConfirmPassword('');
        }
    };

    return (
        <div className="container mt-5 user-profile">
            {/* Navigation Bar */}
            <nav className="navbar navbar-expand-lg navbar-light bg-light mb-4">
                <div className="container-fluid">
                    <a className="navbar-brand" href="#">User Profile</a>
                    <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <div className="collapse navbar-collapse" id="navbarNav">
                        <ul className="navbar-nav ms-auto">
                            <li className="nav-item">
                                <button className="btn btn-link nav-link" onClick={handleViewProducts}>View Products</button>
                            </li>
                            <li className="nav-item">
                                <button className="btn btn-link nav-link" onClick={handleViewCart}>View Cart</button>
                            </li>
                            <li className="nav-item">
                                <button className="btn btn-link nav-link" onClick={handleViewAllOrders}>View Orders</button>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            {error && <p className="text-danger">{error}</p>}
            {success && <p className="text-success">{success}</p>}

            <div className="card p-4">
                {isEditing ? (
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label className="form-label">First Name:</label>
                            <input
                                type="text"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleInputChange}
                                className="form-control"
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label className="form-label">Last Name:</label>
                            <input
                                type="text"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleInputChange}
                                className="form-control"
                            />
                        </div>
                        <div className="mb-3">
                            <label className="form-label">Username:</label>
                            <input
                                type="text"
                                name="userName"
                                value={formData.userName}
                                onChange={handleInputChange}
                                className="form-control"
                            />
                        </div>
                        <button type="submit" className="btn btn-primary me-2">Save</button>
                        <button type="button" className="btn btn-secondary" onClick={() => setIsEditing(false)}>Cancel</button>
                    </form>
                ) : (
                    <div>
                        {user.avatar && (
                            <img src={user.avatar} alt="User Avatar" className="img-fluid mt-2" style={{ maxWidth: '100px' }} />
                        )}
                        <p><strong>Nick name:</strong> {user.userName}</p>
                        <p><strong>First Name:</strong> {user.firstName}</p>
                        <p><strong>Last Name:</strong> {user.lastName}</p>
                        <p><strong>Email:</strong> {user.email}</p>
                        <p><strong>Role:</strong> {user.isStaff ? 'Staff' : 'Customer'}</p>

                        <div className="d-flex mb-3">
                            <button className="btn btn-primary me-2" onClick={handleEditProfile}>Edit Profile</button>
                            <button className="btn btn-info me-2" onClick={handleManageAddress}>Manage Address</button>
                            <button className="btn btn-warning me-2" onClick={() => setIsChangingPassword(true)}>Change Password</button>
                            <button className="btn btn-danger me-2" onClick={handleLogout}>Logout</button>

                        </div>
                    </div>
                )}
            </div>

            {/* Manage Address Modal */}
            {/* Manage Address Modal */}
            {isManagingAddress && (
                <div className="modal show" style={{ display: 'block' }} onClick={() => setIsManagingAddress(false)}>
                    <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Manage Addresses</h5>
                                <button type="button" className="btn-close" onClick={() => setIsManagingAddress(false)}></button>
                            </div>
                            <div className="modal-body">
                                <h6>Add New Address</h6>
                                <input
                                    type="text"
                                    name="streetAddress"
                                    placeholder="Street Address"
                                    value={newAddress.streetAddress}
                                    onChange={handleAddressChange}
                                    className="form-control mb-2"
                                />
                                <input
                                    type="text"
                                    name="city"
                                    placeholder="City"
                                    value={newAddress.city}
                                    onChange={handleAddressChange}
                                    className="form-control mb-2"
                                />
                                <input
                                    type="text"
                                    name="state"
                                    placeholder="State (optional)"
                                    value={newAddress.state}
                                    onChange={handleAddressChange}
                                    className="form-control mb-2"
                                />
                                <input
                                    type="text"
                                    name="postalCode"
                                    placeholder="Postal Code"
                                    value={newAddress.postalCode}
                                    onChange={handleAddressChange}
                                    className="form-control mb-2"
                                />
                                <button className="btn btn-success" onClick={handleAddAddress}>Add Address</button>

                                <h6 className="mt-3">Your Addresses:</h6>
                                {addresses.length > 0 ? (
                                    <ul className="list-group">
                                        {addresses.map((address, index) => (
                                            <li key={index} className="list-group-item d-flex justify-content-between align-items-center">
                                                {`${address.streetAddress}, ${address.city}, ${address.state || ''}, ${address.postalCode}`}
                                                <button className="btn btn-danger btn-sm" onClick={() => handleDeleteAddress(index)}>Delete</button>
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p>No addresses added yet.</p>
                                )}
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={() => setIsManagingAddress(false)}>Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}



            {/* Change Password Modal */}
            {isChangingPassword && (
                <div className="modal show" style={{ display: 'block' }} onClick={() => setIsChangingPassword(false)}>
                    <div className="modal-dialog" onClick={(e) => e.stopPropagation()}> {/* 阻止冒泡 */}
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Change Password</h5>
                                <button type="button" className="btn-close" onClick={() => setIsChangingPassword(false)}></button>
                            </div>
                            <div className="modal-body">
                                <input
                                    type="password"
                                    placeholder="Old Password"
                                    value={oldPassword}
                                    onChange={(e) => setOldPassword(e.target.value)}
                                    className="form-control mb-2"
                                />
                                <input
                                    type="password"
                                    placeholder="New Password"
                                    value={newPassword}
                                    onChange={(e) => setNewPassword(e.target.value)}
                                    className="form-control mb-2"
                                />
                                <input
                                    type="password"
                                    placeholder="Confirm New Password"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    className="form-control mb-2"
                                />
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-primary" onClick={handleChangePassword}>Change Password</button>
                                <button type="button" className="btn btn-secondary" onClick={() => setIsChangingPassword(false)}>Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default UserProfilePage;
