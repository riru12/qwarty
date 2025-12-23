import { createContext, useState } from "react";

interface AuthContextType {
    accessToken: string | null;
    username: string | null;
    setAuthStates: (token: string | null, username: string | null) => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children } : { children : React.ReactNode }) => {
    const [accessToken, setAccessToken] = useState<string | null>(() => localStorage.getItem("accessToken"));
    const [username, setUsername] = useState<string | null>(() => localStorage.getItem("username"));

    const setAuthStates = (accessToken: string | null, username: string | null) => {
        setAccessToken(accessToken);
        setUsername(username);

        if (accessToken) localStorage.setItem("accessToken", accessToken);
        else localStorage.removeItem("accessToken");

        if (username) localStorage.setItem("username", username);
        else localStorage.removeItem("username");
    };

    return (
        <AuthContext.Provider 
            value = {{ 
                accessToken,
                username,
                setAuthStates
            }}>
            { children }
        </AuthContext.Provider>
    )
}