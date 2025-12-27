import { createContext, useState } from "react";
import type { EndpointRes } from "@/services/api/endpoints/endpoint";
import type { IdentityEndpoint } from "@/services/api/endpoints/IdentityEndpoint";

interface AuthContextType {
    username: string | null;
    isGuest: boolean;
    setAuthState: (
        identity: EndpointRes<typeof IdentityEndpoint> | null
    ) => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(
    undefined
);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [username, setUsername] = useState<string | null>(null);
    const [isGuest, setIsGuest] = useState<boolean>(true);

    const clearAuthState = () => {
        setUsername(null);
        setIsGuest(true);
    };

    const updateAuthState = (user: string, guest: boolean) => {
        setUsername(user);
        setIsGuest(guest);
    };

    const setAuthState = (
        identity: EndpointRes<typeof IdentityEndpoint> | null
    ) => {
        if (identity === null) {
            clearAuthState();
            return;
        }

        updateAuthState(identity.username, identity.isGuest);
    };

    return (
        <AuthContext.Provider
            value={{
                username,
                isGuest,
                setAuthState
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};
