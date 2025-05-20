import React, { useContext, useState } from "react";
import axios from "axios";



const Login = () => {
    const [user, setUser] = useState({
        username : "",
        password : ""
    })

   const handleInputChange = (e) => {
        const {name, value} = e.target;
        setUser({...user, [name]: value});
    }

    const handleSubmit = async (e) =>{
        try{
            e.preventDefault();
            // localStorage.removeItem("jwt");
          
            const response = await axios.post("http://localhost:8080/api/login", user)
            
            const jwt = response.data;
            localStorage.setItem("jwt", jwt);
            window.location.href = "/home";
            
        }catch(error){
            console.error("Error logging in", error);
            alert(error);    
        }
    
    }


    return (
        <div className="container">
            <div className="center-container">
            <form onSubmit={handleSubmit}>
                <label>
                    <h6>Username</h6>
                </label>
                <input 
                type="text"
                value={user.username}
                onChange={handleInputChange}
                name="username" 
                />
                <label>
                    <h6>Password</h6>
                </label>
                <input 
                type="password"
                value={user.password}
                onChange={handleInputChange}
                name="password"
                />
                <button type="submit" className="btn btn-primary">
                    submit
                </button>
            </form>
            </div>
            
        </div>
    );

};
export default Login;