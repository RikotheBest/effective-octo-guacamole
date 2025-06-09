import  { useState } from "react";
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
        <form onSubmit={handleSubmit}>
            <div className="mb-3">
                <label htmlFor="login" className="form-label">Username</label>
                <input type="text" className="form-control" id="login" value={user.username} onChange={handleInputChange} name="username"/>
            </div>
            <div className="mb-3">
                <label htmlFor="password" className="form-label">Password</label>
                <input type="password" className="form-control" id="password" value={user.password} onChange={handleInputChange} name="password"/>
            </div>
            <button type="submit" className="btn btn-primary">Submit</button>
        </form>
        </div>

    );

};
export default Login;