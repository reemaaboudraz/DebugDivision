import { Link, useLocation, useNavigate } from "react-router-dom";
import { Ticket, User } from "lucide-react";
import { useAuth } from "@/AuthContext";
import { Button } from "../ui/button";
import { logout as logoutService } from "@/services/AuthService";

export default function Header() {
    
    const {uid} = useAuth();
    const location = useLocation();
    const navigate = useNavigate();

    const isActive = (path: string) => {
        if (path === "/") return location.pathname === "/";
        return location.pathname.includes(path);
    };

    const logout = async () => {    
        logoutService();
        navigate("/")
    }

    return (

        <div className="flex justify-between items-center w-full px-6 py-3
        bg-white border-b shadow-sm sticky top-0 z-50">

            <Link to="/" className="flex items-center gap-3">

                <div className="bg-[#3B82F6] p-2 rounded-xl shadow-sm">
                    <Ticket className="w-6 h-6 text-white"/>
                </div>

                <h1 className="text-xl font-semibold text-blue-600">
                    Tixy
                </h1>

            </Link>

            {/* Navigation */}
            <div className="flex gap-3">

                <Link
                    to="/"
                    className={`px-4 py-2 rounded-full transition ${
                        isActive("/")
                            ? "bg-blue-100 text-blue-600"
                            : "text-gray-600 hover:bg-blue-50"
                    }`}
                >
                    Home
                </Link>
                { uid ? (
                <>
                    <Link
                        to="/dashboard"
                        className={`px-4 py-2 rounded-full transition ${
                            isActive("/dashboard")
                                ? "bg-blue-100 text-blue-600"
                                : "text-gray-600 hover:bg-blue-50"
                        }`}
                    >
                        <div className="flex">
                            <User className="w-6 h-6"></User>
                            Dashboard
                        </div>
                        
                    </Link>
                    <Link
                        to="/events"
                        className={`px-4 py-2 rounded-full transition ${
                            isActive("/events")
                                ? "bg-blue-100 text-blue-600"
                                : "text-gray-600 hover:bg-blue-50"
                        }`}
                    >
                        Events
                    </Link>


                    <Button onClick={logout} className="px-4 py-2 rounded-full transition">
                        Logout
                    </Button>
                </>
                ) : (
                <>
                    <Link
                        to="/login"
                        className={`px-4 py-2 rounded-full transition ${
                            isActive("/login")
                                ? "bg-blue-100 text-blue-600"
                                : "text-gray-600 hover:bg-blue-50"
                        }`}
                    >
                        Login
                    </Link>
                    <Link
                        to="/signup"
                        className={`px-4 py-2 rounded-full transition ${
                            isActive("/signup")
                                ? "bg-blue-100 text-blue-600"
                                : "text-gray-600 hover:bg-blue-50"
                        }`}
                    >
                        Sign Up
                    </Link>
                </>
                )}

            </div>

        </div>
    );
}