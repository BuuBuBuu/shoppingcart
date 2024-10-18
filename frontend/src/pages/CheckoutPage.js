import React, { useState } from 'react';

const CheckoutPage = () => {
    const [billingInfo, setBillingInfo] = useState({
        name: '',
        address: '',
        cardNumber: ''
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setBillingInfo(prevInfo => ({ ...prevInfo, [name]: value }));
    };

    const handleCheckout = () => {
        // 这里可以调用后端服务来处理订单
        console.log('Processing payment for', billingInfo);
    };

    return (
        <div>
            <h2>Checkout</h2>
            <form>
                <input
                    type="text"
                    name="name"
                    placeholder="Name"
                    value={billingInfo.name}
                    onChange={handleInputChange}
                />
                <input
                    type="text"
                    name="address"
                    placeholder="Address"
                    value={billingInfo.address}
                    onChange={handleInputChange}
                />
                <input
                    type="text"
                    name="cardNumber"
                    placeholder="Card Number"
                    value={billingInfo.cardNumber}
                    onChange={handleInputChange}
                />
                <button type="button" onClick={handleCheckout}>Place Order</button>
            </form>
        </div>
    );
};

export default CheckoutPage;
