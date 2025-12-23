import { createContext, useState } from "react";

interface AuthContextType {
    accessToken: string | null;
    setAccessToken: (token: string | null) => void;
    username: string | null;
    setUsername: (name: string | null) => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children } : { children : React.ReactNode }) => {
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [username, setUsername] = useState<string | null>(null);

    return (
        <AuthContext.Provider 
            value = {{ 
                accessToken,
                setAccessToken,
                username, 
                setUsername 
            }}>
            { children }
        </AuthContext.Provider>
    )
}