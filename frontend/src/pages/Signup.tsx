import { useState } from "react";
import { Link} from "react-router";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import RegisterViaEmail from "@/components/auth/RegisterViaEmailFOrm";
import RegisterViaPhone from "@/components/auth/RegisterViaPhoneForm";

export default function SignUp() {
    const [signinMethod, setSigninMethod] = useState<"email" | "phone">("email");

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
                    <RadioGroup
                        value={signinMethod}
                        onValueChange={(val: "email" | "phone") => setSigninMethod(val)}
                        className="flex gap-6 items-center m-6"
                    >
                            <div className="text-gray-500">Register with</div>
                            <RadioGroupItem value="email" id="email" className="sr-only" />
                            <label className={
                                `rounded-xl border p-2 
                                ${signinMethod == "email" ? "bg-[#EC4899] hover:bg-[#DB2777] text-white" : "hover:bg-accent"} 
                                `
                            }
                            htmlFor="email">Email</label>
                            <RadioGroupItem value="phone" id="phone" className="sr-only"/>
                            <label className={
                                `rounded-xl border p-2 
                                ${signinMethod == "phone" ? "bg-[#EC4899] hover:bg-[#DB2777] text-white" : "hover:bg-accent"} 
                                `
                            } 
                            htmlFor="phone">Phone</label>
                    </RadioGroup>
                    {signinMethod=="phone" ? <RegisterViaPhone/>: <RegisterViaEmail/>}
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