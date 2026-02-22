import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { User, Mail, Lock, CheckCircle } from "lucide-react";

export default function SignUp() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        fullName: "",
        email: "",
        role: "CUSTOMER", // default
        password: "",
        confirmPassword: "",
    });

    const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();

      if (formData.password !== formData.confirmPassword) {
        alert("Passwords do not match!");
        return;
      }

      try {
        const res = await fetch("/api/auth/register", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            email: formData.email,
            password: formData.password,
            name: formData.fullName,
            role: formData.role, // "CUSTOMER" | "ORGANIZER"
          }),
        });

        if (!res.ok) {
          // backend returns AuthResponseDTO (sometimes as JSON, sometimes plain)
          const text = await res.text();
          throw new Error(text || "Registration failed");
        }

        const data = await res.json();
        console.log("Registration success:", data);

        // NEED TO REDIRECT TO PROFILE OR HOMEPAGE
        navigate("/login");
      } catch (err: any) {
        alert(err?.message ?? "Registration failed");
      }
    };

    return (
        <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-12">
            <div className="w-full max-w-md">
                <div className="bg-white rounded-3xl shadow-lg p-8 border border-gray-200">
                    <div className="text-center mb-8">
                        <h2 className="text-3xl mb-2 text-[#1F2937]">
                            Create an Account
                        </h2>
                        <p className="text-[#6B7280]">Join Tixy and start your booking journey today!</p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label htmlFor="fullName" className="block text-sm text-[#1F2937] mb-2">
                                Full Name
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                    <User className="h-5 w-5 text-[#6B7280]" />
                                </div>
                                <input
                                    type="text"
                                    id="fullName"
                                    value={formData.fullName}
                                    onChange={(e) =>
                                        setFormData({ ...formData, fullName: e.target.value })
                                    }
                                    className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#EC4899] focus:border-transparent bg-white text-[#1F2937] placeholder:text-[#6B7280]"
                                    placeholder="Enter your full name"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="email" className="block text-sm text-[#1F2937] mb-2">
                                Email
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                    <Mail className="h-5 w-5 text-[#6B7280]" />
                                </div>
                                <input
                                    type="email"
                                    id="email"
                                    value={formData.email}
                                    onChange={(e) =>
                                        setFormData({ ...formData, email: e.target.value })
                                    }
                                    className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#EC4899] focus:border-transparent bg-white text-[#1F2937] placeholder:text-[#6B7280]"
                                    placeholder="Enter your email"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                          <label htmlFor="role" className="block text-sm text-[#1F2937] mb-2">
                            Account Type
                          </label>

                          <select
                            id="role"
                            value={formData.role}
                            onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                            className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#EC4899] focus:border-transparent bg-white text-[#1F2937]"
                          >
                            <option value="CUSTOMER">Customer</option>
                            <option value="ORGANIZER">Organizer</option>
                          </select>
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
                                    className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#EC4899] focus:border-transparent bg-white text-[#1F2937] placeholder:text-[#6B7280]"
                                    placeholder="Create a password"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="confirmPassword" className="block text-sm text-[#1F2937] mb-2">
                                Confirm Password
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                    <CheckCircle className="h-5 w-5 text-[#6B7280]" />
                                </div>
                                <input
                                    type="password"
                                    id="confirmPassword"
                                    value={formData.confirmPassword}
                                    onChange={(e) =>
                                        setFormData({ ...formData, confirmPassword: e.target.value })
                                    }
                                    className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#EC4899] focus:border-transparent bg-white text-[#1F2937] placeholder:text-[#6B7280]"
                                    placeholder="Confirm your password"
                                    required
                                />
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="w-full px-6 py-3 bg-[#3B82F6] text-white rounded-xl hover:bg-[#2563EB] hover:shadow-lg transition-all text-lg"
                        >
                            Sign Up
                        </button>
                    </form>

                    <div className="mt-6 text-center">
                        <p className="text-sm text-[#6B7280]">
                            Already have an account?{" "}
                            <Link to="/login" className="text-[#EC4899] hover:text-[#DB2777] font-medium transition-colors">
                                Go to Login
                            </Link>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}