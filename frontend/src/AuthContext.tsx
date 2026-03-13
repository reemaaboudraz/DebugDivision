import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { auth } from "./lib/firebaseConfig";
import { onAuthStateChanged, type User } from "firebase/auth";
import { authenticatedGet } from "./lib/api";
import { type UserProfile } from "./models/User";


interface AuthContextState {
    uid: string | null;
    userProfile: UserProfile | null;
    loading: boolean;
    authError: string | null;
}

const AuthContext = createContext<AuthContextState | undefined>(undefined);

async function fetchUser(uid: string | null): Promise<UserProfile | null>{
    if(!uid){
        return null;
    }
    const res = await authenticatedGet(`api/auth/profile?uid=${uid}`);

    if (!res.ok) {
        throw new Error();
    } 
    const data = await res.json();
    const user: UserProfile = { ...data };
    if(!user.name || !user.role){
        throw new Error();
    }
    return user;
}

function sleep(ms:number):Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}

async function fetchUserRetryOnErr(uid: string | null,  nbOfRetry: number): Promise<UserProfile | null>{

    if(nbOfRetry <= 1){
        return await fetchUser(uid); 
    }

    try{
        const userProfile = await fetchUser(uid);
        return userProfile;
    } catch (err: any){
        sleep(1000)
        return fetchUserRetryOnErr(uid, nbOfRetry-1)
    }

}

export const AuthProvider = ({ children }: { children: ReactNode }) => {

    const [uid, setUID] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
    const [authError, setError] = useState<string | null>(null);

    useEffect(() => {

        const unsubscribe = onAuthStateChanged(auth, async (user: User | null) => {
            const numOfProfileFetchRetries = 5;
            try{
                setLoading(true);
                setError(null);
                if(user?.uid){
                    setUID(user.uid);
                    setUserProfile(await fetchUserRetryOnErr(user.uid, numOfProfileFetchRetries));
                } else {
                    setUID(null);
                    setUserProfile(null);
                }
            } catch (err: any){
                setUID(null);
                setUserProfile(null);
                setError("Authentication failed. Please log out and log back in.");
            } finally{
                setLoading(false);
            }
        });

        return unsubscribe;
    }, []);

    return (
        <AuthContext.Provider value={{ uid, loading, userProfile, authError }}>
        {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within AuthProvider");
    return context;
};
