import { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axios from "../axios";
import AppContext from "../Context/Context";
import unplugged from "../assets/unplugged.png"

const Home = () => {
  const {data, isError, addToCart} = useContext(AppContext);
  const [products, setProducts] = useState([]);




  useEffect(() => {
    if (data && data.length > 0) {
      fetchImagesAndUpdateProducts();
    }
  }, [data]);



  const fetchImagesAndUpdateProducts = async () => {
    const updatedProducts = await Promise.all(
      data.map(async (product) => {
        try {
          const response = await axios.get(
            `/product/${product.id}/image`,
            { 
              responseType: "blob",

              }
          )
          const imageURL = URL.createObjectURL(response.data);
          return { ...product, imageURL };
        } catch (error) {
          console.error(
            "Error fetching image for product ID:",
            product.id,
          );
          return { ...product, imageURL: "placeholder-image-url" };
        }
      })
    );
    setProducts(updatedProducts);
  };

  useEffect(() => {
    // console.log("filter triggered the image fetch")
    if(data && data.length > 0){
      fetchImagesAndUpdateProducts();
    }
    return () => {
    products.forEach(product => {
      if (product.imageURL.startsWith('blob:')) {
        URL.revokeObjectURL(product.imageURL);
      }
    });
    };
  }, [data]);
  

  // const filterByCategory = async() => {
  //   // console.log("filter called")
  //   res = await axios.get("/filter", {
  //       params: {
  //         category : selectedCategory,
  //         currentPage : currentPage,
  //         pageSize : pageSize
  //     }});
    
  //   setData(res.data.content)
  //   setTotalPages(res.data.page.totalPages)
  //   console.log(res.data.content)
  // }

  // useEffect( () => {filterByCategory()},[selectedCategory]);
  

  if (isError) {
    return (
      <h2 className="text-center" style={{ padding: "18rem" }}>
      <img src={unplugged} alt="Error" style={{ width: '100px', height: '100px' }}/>
      </h2>
    );
  }
  return (
    <>
      <div
        className="grid"
        style={{
          marginTop: "64px",
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
          gap: "20px",
          padding: "20px",
        }}
      >
        {products.length === 0 ? (
          <h2
            className="text-center"
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            No Products Available
          </h2>
        ) : (
          products.map((product) => {
            const { id, brand, name, price, productAvailable, imageURL } =
              product;
            const cardStyle = {
              width: "18rem",
              height: "12rem",
              boxShadow: "rgba(0, 0, 0, 0.24) 0px 2px 3px",
              backgroundColor: productAvailable ? "#fff" : "#ccc",
            };
            return (
              <div
                className="card mb-3"
                style={{
                  width: "250px",
                  height: "360px",
                  boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
                  borderRadius: "10px",
                  overflow: "hidden", 
                  backgroundColor: productAvailable ? "#fff" : "#ccc",
                  display: "flex",
                  flexDirection: "column",
                  justifyContent:'flex-start',
                  alignItems:'stretch'
                }}
                key={id}
              >
                <Link
                  to={`/product/${id}`}
                  style={{ textDecoration: "none", color: "inherit" }}
                >
                  <img
                    src={imageURL}
                    alt={name}
                    style={{
                      width: "100%",
                      height: "150px", 
                      objectFit: "cover",  
                      padding: "5px",
                      margin: "0",
                      borderRadius: "10px 10px 10px 10px", 
                    }}
                  />
                  <div
                    className="card-body"
                    style={{
                      flexGrow: 1,
                      display: "flex",
                      flexDirection: "column",
                      justifyContent: "space-between",
                      padding: "10px",
                    }}
                  >
                    <div>
                      <h5
                        className="card-title"
                        style={{ margin: "0 0 10px 0", fontSize: "1.2rem" }}
                      >
                        {name.toUpperCase()}
                      </h5>
                      <i
                        className="card-brand"
                        style={{ fontStyle: "italic", fontSize: "0.8rem" }}
                      >
                        {"~ " + brand}
                      </i>
                    </div>
                    <hr className="hr-line" style={{ margin: "10px 0" }} />
                    <div className="home-cart-price">
                      <h5
                        className="card-text"
                        style={{ fontWeight: "600", fontSize: "1.1rem",marginBottom:'5px' }}
                      >
                        <i className="bi bi-currency-dollar"></i>
                        {price}
                      </h5>
                    </div>
                    <button
                      className="btn-hover color-9"
                      style={{margin:'10px 25px 0px '  }}
                      onClick={(e) => {
                        e.preventDefault();
                        addToCart(product);
                      }}
                      disabled={!productAvailable}
                    >
                      {productAvailable ? "Add to Cart" : "Out of Stock"}
                    </button> 
                  </div>
                </Link>
              </div>
            );
          })
        )}
      </div>
    </>
  );
};

export default Home;
