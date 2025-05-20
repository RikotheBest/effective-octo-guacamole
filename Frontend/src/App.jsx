import "./App.css";
import React, { useState, useEffect } from "react";
import Login from "./components/Login"
import Home from "./components/Home";
import Navbar from "./components/Navbar";
import Cart from "./components/Cart";
import AddProduct from "./components/AddProduct";
import Product from "./components/Product";
import { BrowserRouter, Routes, Route, useLocation } from "react-router-dom";
import { AppProvider } from "./Context/Context";
import UpdateProduct from "./components/UpdateProduct";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import 'bootstrap/dist/css/bootstrap.min.css';


function App() {
  const [cart, setCart] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");


  

  const handleCategorySelect = (category) => {
    setSelectedCategory(category);
    console.log("Selected category:", category);
  };
  const addToCart = (product) => {
    const existingProduct = cart.find((item) => item.id === product.id);
    if (existingProduct) {
      setCart(
        cart.map((item) =>
          item.id === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        )
      );
    } else {
      setCart([...cart, { ...product, quantity: 1 }]);
    }
  };

  return (
  <AppProvider>
    <BrowserRouter>
      <AppContent 
        handleCategorySelect={handleCategorySelect} 
        selectedCategory={selectedCategory}
        addToCart={addToCart}
      />
    </BrowserRouter>
  </AppProvider>       
  );
}
function AppContent({ handleCategorySelect, selectedCategory, addToCart }) {
  const location = useLocation();
  const isLoginPage = location.pathname === '/';
  
  return (
    <>
      {!isLoginPage && (<Navbar onSelectCategory={handleCategorySelect} />)}
      <Routes>
        <Route path="/" element={<Login />} />
        <Route
          path="/home"
          element={<Home addToCart={addToCart} selectedCategory={selectedCategory} />}
        />
        <Route path="/add_product" element={<AddProduct />} />
        <Route path="/product" element={<Product />} />
        <Route path="product/:id" element={<Product />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/product/update/:id" element={<UpdateProduct />} />
      </Routes>
    </>
  );
}

export default App;
