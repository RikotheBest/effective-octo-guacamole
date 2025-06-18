
import axios from "../axios";
import { useState, useEffect, createContext } from "react";



const AppContext = createContext({
  pageSize: 0,
  currentPage: 0,
  totalPages: 0,
  data: [],
  isError: "",
  cart: [],
  addToCart: (product) => {},
  removeFromCart: (productId) => {},
  refreshData:() =>{},
  updateStockQuantity: (productId, newQuantity) =>{}
  
});


export const AppProvider = ({ children }) => {
  const pageSize = 5;
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [data, setData] = useState([]);
  const [isError, setIsError] = useState("");
  const [cart, setCart] = useState(JSON.parse(localStorage.getItem('cart')) || []);
  
  

  const addToCart = (product) => {
    const existingProductIndex = cart.findIndex((item) => item.id === product.id);
    if (existingProductIndex !== -1) {
      const updatedCart = cart.map((item, index) =>
        index === existingProductIndex
          ? { ...item, quantity: item.quantity + 1 }
          : item
      );
      setCart(updatedCart);
      localStorage.setItem('cart', JSON.stringify(updatedCart));
    } else {
      const updatedCart = [...cart, { ...product, quantity: 1 }];
      setCart(updatedCart);
      localStorage.setItem('cart', JSON.stringify(updatedCart));
    }
  };

  const removeFromCart = (productId) => {
    console.log("productID",productId)
    const updatedCart = cart.filter((item) => item.id !== productId);
    setCart(updatedCart);
    localStorage.setItem('cart', JSON.stringify(updatedCart));
    console.log("CART",cart)
  };




  const refreshData = async () => {
    try {
      const token = localStorage.getItem("jwt");
      if (!token && window.location.pathname !== "/") {
        window.location.href = "/";
        return;
      }
      
      const response = await axios.get("/products", {
        params: {
          currentPage : currentPage,
          pageSize : pageSize
      }});
      setData(response.data.content);
      setTotalPages(response.data.page.totalPages)
      // setCurrentPage(response.data.page.number)
    } catch (error) {
      if (error.response.status === 401) {
        localStorage.removeItem("jwt");
        if(window.location.pathname !== "/"){
          window.location.href = "/";
        }
      }
      setIsError(error.message);
    }
  };

 

  const clearCart =() =>{
    setCart([]);
  }
  
  useEffect(() => {
    refreshData();
  }, [currentPage]);

  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cart));
  }, [cart]);


  
  return (
    <AppContext.Provider value={{ pageSize, currentPage, totalPages, data, isError, cart, addToCart, removeFromCart,refreshData, clearCart, setCurrentPage}}>
      {children}
    </AppContext.Provider>
  );
};

export default AppContext;