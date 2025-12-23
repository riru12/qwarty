import { Routes, Route } from "react-router";

import Signup from "../pages/Signup";
import Login from "../pages/Login";

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