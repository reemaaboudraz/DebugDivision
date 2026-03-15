
import { useAuth } from "@/AuthContext";
import { Link } from "react-router-dom";


export default function Profile(){
    const {userProfile} = useAuth();
    const name = userProfile?.name;
    const emailOrPhone = userProfile?.email ?? userProfile?.phone;
    const [firstName, lastName] = name?.split(" ") ?? [];
    const initials = firstName?.charAt(0).toUpperCase() + lastName?.charAt(0).toUpperCase()

    return (
        <div className="flex justify-between px-6 py-5
          rounded-4xl my-5 ">
            <div className="flex-col items-center gap-5"> 
                <h1 className="text-3xl">
                    Welcome,
                </h1>
                <div className="flex gap-2">
                    <div className="text-white text-sm font-semibold w-10 h-10 rounded-full bg-[#EC4899] 
                    flex items-center justify-center shadow-sm shrink-0">
                        {initials}
                    </div>
                <h2 className=" flex items-center text-gray-500">
                {name}
                </h2>
                </div>
                
                <h2 className="text-gray-500">
                    {emailOrPhone}
                </h2>               
            </div>

            <Link to={"/editProfile"} className="px-4 py-2 text-blue-600 hover:bg-blue-50">
            edit profile
            </Link>
        </div>
    );
    

}