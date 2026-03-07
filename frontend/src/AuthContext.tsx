import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { auth } from "./lib/firebaseConfig";
import { onAuthStateChanged, type User } from "firebase/auth";


interface AuthContextState {
    UID: string | null;
    loading: boolean;
}

const AuthContext = createContext<AuthContextState | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {

    const [UID, setUID] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (user: User | null) => {
      setUID(user?.uid ?? null);
      setLoading(false);
    });

    return unsubscribe;
  }, []);

    return (
        <AuthContext.Provider value={{ UID, loading }}>
        {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within AuthProvider");
    return context;
};
