import { Link, useLocation, useNavigate } from "react-router-dom";
import { Ticket, User } from "lucide-react";
import { useAuth } from "@/AuthContext";
import { Button } from "../ui/button";
import { logout as logoutService } from "@/services/AuthService";
import { Role } from "@/models/User";

export default function Header() {
    const { uid, userProfile, loading } = useAuth();
    const role = userProfile?.role;
    const location = useLocation();
    const navigate = useNavigate();

    if (loading || !uid) return null;

    const isActive = (path: string) => {
        if (path === "/") return location.pathname === "/";
        return location.pathname.includes(path);
    };

    const logout = () => {
        logoutService();
        navigate("/");
    };

    return (
        <div className="flex justify-between items-center w-full px-6 py-3
        bg-white border-b shadow-sm sticky top-0 z-50">

            <Link to="/" className="flex items-center gap-3">
                <div className="bg-[#3B82F6] p-2 rounded-xl shadow-sm">
                    <Ticket className="w-6 h-6 text-white" />
                </div>
                <h1 className="text-xl font-semibold text-blue-600">Tixy</h1>
            </Link>

            <div className="flex gap-3 items-center">
                <Link
                    to="/"
                    className={`px-4 py-2 rounded-full transition ${
                        isActive("/") ? "bg-blue-100 text-blue-600" : "text-gray-600 hover:bg-blue-50"
                    }`}
                >
                    Home
                </Link>

                {role === Role.customer && (
                    <Link
                        to="/events"
                        className={`px-4 py-2 rounded-full transition ${
                            isActive("/events") ? "bg-blue-100 text-blue-600" : "text-gray-600 hover:bg-blue-50"
                        }`}
                    >
                        Events
                    </Link>
                )}

                <Link
                    to="/dashboard"
                    className={`px-4 py-2 rounded-full transition flex items-center gap-1.5 ${
                        isActive("/dashboard") ? "bg-blue-100 text-blue-600" : "text-gray-600 hover:bg-blue-50"
                    }`}
                >
                    <User className="w-4 h-4" />
                    Dashboard
                </Link>

                <Button onClick={logout} className="px-4 py-2 rounded-full transition">
                    Logout
                </Button>
            </div>
        </div>
    );
}
