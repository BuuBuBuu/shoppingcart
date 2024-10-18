import React, { useEffect, useState } from 'react';

const OrderPage = () => {
    const [orders, setOrders] = useState([
        { id: 1, date: '2024-10-01', total: 100, status: 'Delivered' },
        { id: 2, date: '2024-10-05', total: 50, status: 'Processing' }
    ]);

    return (
        <div>
            <h2>Order History</h2>
            {orders.map(order => (
                <div key={order.id}>
                    <p>Order ID: {order.id}</p>
                    <p>Date: {order.date}</p>
                    <p>Total: ${order.total}</p>
                    <p>Status: {order.status}</p>
                </div>
            ))}
        </div>
    );
};

export default OrderPage;
