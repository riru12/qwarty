import { Routes, Route } from "react-router";
import { Login, Signup } from "../pages";

function AppRoutes() {
    return (
        <Routes>
            <Route path = "/" element ={<div>home page</div>}/>
            <Route path = "/login" element ={<Login />}/>
            <Route path = "/signup" element ={<Signup />}/>
            <Route path = "/game" element ={<div>game page</div>}/>
        </Routes>   
    )
}

export default AppRoutes