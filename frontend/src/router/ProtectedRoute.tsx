import { Outlet, Navigate } from "react-router-dom";
import NoAccess from "./NoAccess"

interface protectedRouteProps {
    condition: boolean;
    fallbackRoute?: string;
    errorMessage?: string;
}   

export default function ProtectedRoute(
    {condition, fallbackRoute, errorMessage="You do not have acess to this page"}
    :protectedRouteProps){

    if (condition){
        return  <Outlet/>
    } 
    return fallbackRoute ? <Navigate to={fallbackRoute}/> : <NoAccess description={errorMessage}/>;
}