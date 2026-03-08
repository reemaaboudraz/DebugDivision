import { useState } from "react";
import LoginViaEmail from "@/components/auth/LoginViaEmailForm";
import LoginViaPhone from "@/components/auth/LoginViaPhoneForm";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Link, Navigate } from "react-router-dom";
import { Spinner } from "@/components/ui/spinner";
import { useAuth } from "@/AuthContext";

export default function Login() {
    const [loginMethod, setLoginMethod] = useState<"email" | "phone">("email");

    const {uid, loading} = useAuth();
    if(loading) return <Spinner className="min-h-100"/>
    if(uid) return <Navigate to="/" replace />;
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
                    <RadioGroup
                        value={loginMethod}
                        onValueChange={(value: "email" | "phone") => setLoginMethod(value)}
                        className="flex gap-6 items-center m-6"
                    >
                            <div className="text-gray-500">Log in with</div>
                            <RadioGroupItem value="email" id="email" className="sr-only" />
                            <label className={
                                `rounded-xl border p-2 
                                ${loginMethod == "email" ? "bg-[#EC4899] hover:bg-[#DB2777] text-white" : "hover:bg-accent"} 
                                `
                            }
                            htmlFor="email">Email</label>
                            <RadioGroupItem value="phone" id="phone" className="sr-only"/>
                            <label className={
                                `rounded-xl border p-2 
                                ${loginMethod == "phone" ? "bg-[#EC4899] hover:bg-[#DB2777] text-white" : "hover:bg-accent"} 
                                `
                            } 
                            htmlFor="phone">Phone</label>
                    </RadioGroup>
                    {loginMethod=="phone" ? <LoginViaPhone/>: <LoginViaEmail/>}
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