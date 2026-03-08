import App from "@/App";
import NotFound from "./NotFound";
import {createBrowserRouter, Navigate} from "react-router-dom";
import RouteError from "./RouteError";
import Home from "@/pages/Home";
import Login from "@/pages/Login";
import Signup from "@/pages/Signup";
import OrganizerDashboard from "@/pages/OrganizerDashboard";
import CreateEventPage from "@/pages/CreateEventPage";
import ProtectedRoute from "./ProtectedRoute";
import { useAuth } from "@/AuthContext";
import { Role } from "@/models/User";



function DashboardRedirect(){
    const {userProfile} = useAuth();
    if (userProfile?.role == Role.organizer)
        return <Navigate to="/organizer/dashboard" replace/>;
    if (userProfile?.role == Role.customer)
        return <Navigate to="/404" replace/>;
}


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
            {
                path: "/dashboard",
                element: <DashboardRedirect/>,
            },
            {
                path: "organizer/events/create",
                element: (
                    <ProtectedRoute role={"ORGANIZER"} errorMessage="Only Organizers May Access This Page">
                        <CreateEventPage/>
                    </ProtectedRoute>
                )
            },
            {
                path: "organizer/dashboard",
                element: (
                    <ProtectedRoute role={"ORGANIZER"} errorMessage="Only Organizers May Access This Page">
                        <OrganizerDashboard/>
                    </ProtectedRoute>
                )
            }
        ]
    },
]);
export default router;

