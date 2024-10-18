//  @author ZENGXING

import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import ProductListPage from "./pages/ProductListPage";
import ProductDetailPage from "./pages/productDetailPage";
import UserProfilePage from "./pages/UserProfilePage";
import OrderListPage from "./pages/OrderListPage";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/cart" element={<CartPage/>}/>
                <Route path="/checkout" element={<CheckoutPage/>}/>
                <Route path="/" element={<LoginPage/>}/>
                <Route path="/products" element={<ProductListPage />} />
                <Route path="/profile" element={<UserProfilePage />} />
                <Route path="/orders" element={<OrderListPage />} />
                <Route path="/products/:id" element={<ProductDetailPage />} />
                <Route path="/register" element={<RegisterPage/>}/>
            </Routes>
        </Router>
    );
}

export default App;
