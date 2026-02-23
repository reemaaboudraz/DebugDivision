import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { Mail, Lock } from "lucide-react";

export default function Login() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        emailOrPhone: "",
        password: "",
    });

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        // Mock login - in a real app, this would authenticate with Firebase
        console.log("Login attempt:", formData);
        // Redirect to home page after "login"
        navigate("/");
    };

    return (
        <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-12">
            <div className="w-full max-w-md">
                <div className="bg-white rounded-3xl shadow-lg p-8 border border-gray-200">
                    <div className="text-center mb-8">
                        <h2 className="text-3xl mb-2 text-[#1F2937]">
                            Login to Tixy
                        </h2>
                        <p className="text-[#6B7280]">Welcome back! Please login to your account.</p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label htmlFor="emailOrPhone" className="block text-sm text-[#1F2937] mb-2">
                                Email or Phone Number
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                    <Mail className="h-5 w-5 text-[#6B7280]" />
                                </div>
                                <input
                                    type="text"
                                    id="emailOrPhone"
                                    value={formData.emailOrPhone}
                                    onChange={(e) =>
                                        setFormData({ ...formData, emailOrPhone: e.target.value })
                                    }
                                    className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#3B82F6] focus:border-transparent bg-white text-[#1F2937] placeholder:text-[#6B7280]"
                                    placeholder="Enter your email or phone"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="password" className="block text-sm text-[#1F2937] mb-2">
                                Password
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                    <Lock className="h-5 w-5 text-[#6B7280]" />
                                </div>
                                <input
                                    type="password"
                                    id="password"
                                    value={formData.password}
                                    onChange={(e) =>
                                        setFormData({ ...formData, password: e.target.value })
                                    }
                                    className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#3B82F6] focus:border-transparent bg-white text-[#1F2937] placeholder:text-[#6B7280]"
                                    placeholder="Enter your password"
                                    required
                                />
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="w-full px-6 py-3 bg-[#3B82F6] text-white rounded-xl hover:bg-[#2563EB] hover:shadow-lg transition-all text-lg"
                        >
                            Login
                        </button>
                    </form>

                    <div className="mt-6 text-center">
                        <p className="text-sm text-[#6B7280]">
                            Don't have an account?{" "}
                            <Link to="/signup" className="text-[#EC4899] hover:text-[#DB2777] font-medium transition-colors">
                                Go to Sign Up
                            </Link>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}