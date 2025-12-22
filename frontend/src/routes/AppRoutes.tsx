import { Routes, Route } from "react-router";

import Signup from "../pages/Signup";

function AppRoutes() {
    return (
        <Routes>
            <Route path = "/" element ={<div>home page</div>}/>
            <Route path = "/login" element ={<div>login page</div>}/>
            <Route path = "/signup" element ={<Signup />}/>
            <Route path = "/game" element ={<div>game page</div>}/>
        </Routes>   
    )
}

export default AppRoutes