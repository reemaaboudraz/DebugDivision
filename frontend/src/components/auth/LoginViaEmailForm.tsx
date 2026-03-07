import { useState } from "react";
import { useNavigate } from "react-router";
import { Mail, Lock } from "lucide-react";
import { login } from "@/services/AuthService";

export default function LoginViaEmail() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: "",
        password: "",
    });
    const [hint, setHint] = useState<string>();

    const handleSubmit = async (e: React.SubmitEvent) => {
        e.preventDefault();
        setHint("Loading...");

        try{
            await login(formData.email, formData.password)
            setHint("")
            navigate("/dashboard");
        } catch (err: any) {
            setHint(err?.message || "Login failed for unknown reason")
        }
    };

    return (
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
                            id="email"
                            value={formData.email}
                            onChange={(e) =>
                                setFormData({ ...formData, email: e.target.value })
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
                {hint && 
                    <div className="text-foreground text-sm">
                        {hint}
                    </div>
                }
                <button
                    type="submit"
                    className="w-full px-6 py-3 bg-[#3B82F6] text-white rounded-xl hover:bg-[#2563EB] hover:shadow-lg transition-all text-lg"
                >
                    Login
                </button>
            </form>
    );
}