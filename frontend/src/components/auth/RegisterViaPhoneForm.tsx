import { useEffect, useState } from "react";
import {
  setupPhoneRecaptcha,
  sendPhoneVerificationCode,
  confirmPhoneVerificationCode,
} from "@/services/AuthService";
import { User, Phone, CheckCircle } from "lucide-react";
import { authenticatedPost } from "@/lib/api";

export default function RegisterViaPhone() {

    const [formData, setFormData] = useState({
        fullName: "",
        phoneNumber: "",
        role: "CUSTOMER", // default
    });
    const [code, setCode] = useState("");
    const [step, setStep] = useState<"phone" | "code">("phone");
    const [message, setMessage] = useState("");

  useEffect(() => {
    setupPhoneRecaptcha("recaptcha-container");
  }, []);


    const formatPhoneNumber= (phoneNumber: string):string => {
        phoneNumber.replaceAll(" ", "");
        phoneNumber.replaceAll("-", "");
        phoneNumber.replaceAll("(", "");
        phoneNumber.replaceAll(")", "");
        phoneNumber = "+1"+phoneNumber;
        return phoneNumber;
    }

    async function handleSendCode(e: React.SubmitEvent) {
        e.preventDefault();
        setMessage("");

        try {
            await sendPhoneVerificationCode(formatPhoneNumber(formData.phoneNumber));
            setStep("code");
            setMessage("Verification code sent.");
        } catch (err: any) {
            setMessage(err?.message || "Failed to send verification code.");
        }
    }

  async function handleVerifyCode(e: React.SubmitEvent) {
    e.preventDefault();
    setMessage("");

    try {
      await confirmPhoneVerificationCode(code);

      const res = await authenticatedPost("/api/auth/register-phone", 
        JSON.stringify({
          name: formData.fullName,
          role: formData.role,
          phoneNumber: formatPhoneNumber(formData.phoneNumber)
        }),
      );

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || data.message || "Failed to create profile");
      }

      setMessage("Phone registration complete.");
    } catch (err: any) {
      setMessage(err?.message || "Verification failed.");
    }
  }

  return (
    
    <div className="max-w-md mx-auto space-y-4">
      <div id="recaptcha-container" />

      {step === "phone" ? (
        <form onSubmit={handleSendCode} className="space-y-5">
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
                <label htmlFor="phone" className="block text-sm text-[#1F2937] mb-2">
                    Phone
                </label>
                <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                        <Phone className="h-5 w-5 text-[#6B7280]" />
                    </div>
                    <input
                        type="tel"
                        id="phone"
                        value={formData.phoneNumber}
                        onChange={(e) =>
                            setFormData({ ...formData, phoneNumber: e.target.value })
                        }
                        className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#EC4899] focus:border-transparent bg-white text-[#1F2937] placeholder:text-[#6B7280]"
                        placeholder="514-000-0000"
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
            <button
                type="submit"
                className="w-full px-6 py-3 bg-[#3B82F6] text-white rounded-xl hover:bg-[#2563EB] hover:shadow-lg transition-all text-lg flex items-center gap-x-2"
            >
                <CheckCircle className="h-5 w-5 text-[#6B7280]" />
                SendCode
            </button>
        </form>
      ) : (
        <form onSubmit={handleVerifyCode} className="space-y-3">
          <input
            type="text"
            placeholder="123456"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            required
            className="w-full border rounded p-2"
          />
          <button type="submit" className="w-full rounded bg-blue-600 text-white p-2">
            Verify code
          </button>
        </form>
      )}

      {message && <p>{message}</p>}
    </div>
  );
}