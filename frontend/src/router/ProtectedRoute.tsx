import { Navigate } from "react-router-dom";
import NoAccess from "./NoAccess"
import { type ReactNode } from "react";
import { useAuth } from "@/AuthContext";
import { Role } from "@/models/User";
import { Spinner } from "@/components/ui/spinner";

interface protectedRouteProps {
    children: ReactNode;
    role: Role;
    fallbackRoute?: string;
    errorMessage?: string;
}   

export default function ProtectedRoute(
    {children, role, fallbackRoute, errorMessage="You do not have acess to this page"}
    :protectedRouteProps){

    const {loading} = useAuth();
    const {authError} = useAuth();
    const {userProfile} = useAuth();

    
    if (loading){
        return <Spinner className="w-50 h-50"></Spinner>
    } else if (authError){
        return (
            <div className="text-2xl flex items-center max-w-100%">
                <h1>{authError}</h1>
            </div>
        )
    } else if (role === userProfile?.role){
        return  children;
    } 
    return fallbackRoute ? <Navigate to={fallbackRoute}/> : <NoAccess description={errorMessage}/>;
}