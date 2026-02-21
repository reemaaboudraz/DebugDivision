import App from "@/App";
import NotFound from "./NotFound";
import {createBrowserRouter} from "react-router-dom";
import RouteError from "./RouteError";
import Home from "@/pages/Home";
import Login from "@/pages/Login";
import Signup from "@/pages/Signup";



const router = createBrowserRouter([
    {
        path:"/",
        element:<App/>,
        errorElement:<RouteError/>,
        children:[
            {
                index:true,
                element:<Home/>
            },
            {
                path:"/login",
                element:<Login/>
            },
            {
                path:"/signup",
                element:<Signup/>
            },
            {
                path:"/404",
                element:<NotFound/>
            },
        ]
    },
]);
export default router;
