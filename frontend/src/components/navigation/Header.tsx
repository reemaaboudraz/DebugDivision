import { Link, useLocation } from "react-router-dom";
import { Ticket } from "lucide-react";

export default function Header() {

    const location = useLocation();

    const isActive = (path: string) => {
        if (path === "/") return location.pathname === "/";
        return location.pathname.startsWith(path);
    };

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

            </div>

        </div>
    );
}